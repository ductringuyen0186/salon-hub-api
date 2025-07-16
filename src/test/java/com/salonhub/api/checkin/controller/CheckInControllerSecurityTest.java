package com.salonhub.api.checkin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salonhub.api.config.TestSecurityConfig;
import com.salonhub.api.checkin.dto.CheckInRequestDTO;
import com.salonhub.api.checkin.service.CheckInService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CheckInController.class)
@Import(TestSecurityConfig.class)
class CheckInControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CheckInService checkInService;

    @Test
    void checkIn_isPublicEndpoint_shouldReturn200() throws Exception {
        CheckInRequestDTO requestDTO = new CheckInRequestDTO();
        requestDTO.setName("Test Customer");
        requestDTO.setContact("test@example.com");

        mockMvc.perform(post("/api/checkin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void getTodayGuests_withoutAuth_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/checkin/guests/today"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void getTodayGuests_withTechnicianRole_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/checkin/guests/today"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void getTodayGuests_withFrontDeskRole_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/checkin/guests/today"))
                .andExpect(status().isOk());
    }

    @Test
    void checkInExisting_withoutAuth_shouldReturn200() throws Exception {
        mockMvc.perform(post("/api/checkin/existing")
                .param("phoneOrEmail", "test@example.com"))
                .andExpect(status().isOk());
    }

    @Test
    void checkInGuest_withoutAuth_shouldReturn200() throws Exception {
        mockMvc.perform(post("/api/checkin/guest")
                .param("name", "Test Guest")
                .param("phoneNumber", "123-456-7890"))
                .andExpect(status().isOk());
    }
}
