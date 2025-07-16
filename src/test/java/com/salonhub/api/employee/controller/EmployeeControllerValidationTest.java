package com.salonhub.api.employee.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salonhub.api.employee.dto.EmployeeRequestDTO;
import com.salonhub.api.employee.mapper.EmployeeMapper;
import com.salonhub.api.employee.model.Employee;
import com.salonhub.api.employee.model.Role;
import com.salonhub.api.employee.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.BDDMockito.given;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


// Load only EmployeeController and validation machinery
@WebMvcTest(controllers = EmployeeController.class)
@Import(com.salonhub.api.config.TestSecurityConfig.class)
class EmployeeControllerValidationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper mapper;
    
    @MockitoBean                                           // <-- add this
    private EmployeeService employeeService;

    @MockitoBean                        // satisfy the controller’s Mapper dependency
    private EmployeeMapper employeeMapper;

    @BeforeEach
    void setup() {
    // make sure ID 1 always “exists” in your tests
        given(employeeService.setAvailability(eq(1L), anyBoolean()))
        .willReturn(Optional.of(new Employee("Test", Role.TECHNICIAN)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/employees with blank name => 400")
    void whenPostBlankName_thenBadRequest() throws Exception {
        EmployeeRequestDTO dto = new EmployeeRequestDTO();
        dto.setName("");
        dto.setAvailable(true);
        dto.setRole(Role.TECHNICIAN.name());
        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors", hasItem("name: Name must not be blank")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PATCH availability with non-boolean => 400")
    void whenPatchInvalidBoolean_thenBadRequest() throws Exception {
      mockMvc.perform(patch("/api/employees/1/availability")
              .param("available", "notaboolean"))
          .andExpect(status().isBadRequest());  // now it will be 400 if you’ve done (A)
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/employees/0 (ID ≤ 0) => 400")
    void whenGetWithZeroId_thenBadRequest() throws Exception {
        mockMvc.perform(get("/api/employees/0"))
            .andExpect(status().isBadRequest());
    }
}
