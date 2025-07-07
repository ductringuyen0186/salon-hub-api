package com.salonhub.api.customer.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.salonhub.api.customer.dto.CustomerRequestDTO;
import com.salonhub.api.customer.mapper.CustomerMapper;
import com.salonhub.api.customer.service.CustomerService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(controllers = CustomerController.class,
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
    })
@Import(com.salonhub.api.config.TestSecurityConfig.class)
class CustomerControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService service;

    @MockitoBean
    private CustomerMapper mapper;

    @Test
    @DisplayName("POST /api/customers - missing email")
    void testMissingEmail() throws Exception {
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setName("John Doe");
        dto.setPhoneNumber("1234567890");

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0]").exists());
    }

    @Test
    @DisplayName("POST /api/customers - invalid email format")
    void testInvalidEmailFormat() throws Exception {
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setEmail("bademail");
        dto.setName("John Doe");
        dto.setPhoneNumber("1234567890");

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0]").exists());
    }

    @Test
    @DisplayName("POST /api/customers - missing name")
    void testMissingName() throws Exception {
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setEmail("john@example.com");
        dto.setPhoneNumber("1234567890");

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0]").exists());
    }

    @Test
    @DisplayName("POST /api/customers - phone number too long")
    void testPhoneNumberTooLong() throws Exception {
        CustomerRequestDTO dto = new CustomerRequestDTO();
        dto.setEmail("john@example.com");
        dto.setName("John Doe");
        dto.setPhoneNumber("123456789012345678901234567890"); // too long

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0]").exists());
    }
}