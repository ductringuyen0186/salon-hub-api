package com.salonhub.api.checkin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salonhub.api.checkin.dto.CheckInRequestDTO;
import com.salonhub.api.checkin.dto.CheckInResponseDTO;
import com.salonhub.api.checkin.service.CheckInService;
import com.salonhub.api.config.TestSecurityConfig;
import com.salonhub.api.customer.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Security tests for CheckInController role-based permissions.
 * Tests that endpoints properly enforce role requirements:
 * - Basic check-in endpoints: Public (for customers)
 * - View guest data: FRONT_DESK, MANAGER, ADMIN
 */
@WebMvcTest(CheckInController.class)
@Import(TestSecurityConfig.class)
class CheckInControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CheckInService checkInService;

    private CheckInRequestDTO createValidCheckInRequest() {
        CheckInRequestDTO request = new CheckInRequestDTO();
        request.setName("Test Customer");
        request.setContact("1234567890"); // Required field
        request.setPhoneNumber("1234567890");
        request.setEmail("test@example.com");
        return request;
    }

    private CheckInResponseDTO createValidCheckInResponse() {
        CheckInResponseDTO response = new CheckInResponseDTO(
            1L, 
            "Test Customer", 
            "1234567890", 
            "test@example.com", 
            "Test note", 
            false, 
            LocalDateTime.now(), 
            "Check-in successful",
            15,  // estimatedWaitTime
            1,   // queuePosition
            1L   // queueId
        );
        return response;
    }

    private Customer createValidCustomer() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Test Customer");
        customer.setPhoneNumber("1234567890");
        customer.setEmail("test@example.com");
        return customer;
    }

    // ===== POST /api/checkin - Unified check-in endpoint (PUBLIC) =====

    @Test
    void checkIn_withoutAuth_shouldReturn200() throws Exception {
        CheckInRequestDTO request = createValidCheckInRequest();
        when(checkInService.checkIn(any())).thenReturn(createValidCheckInResponse());

        mockMvc.perform(post("/api/checkin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void checkIn_withAuth_shouldReturn200() throws Exception {
        CheckInRequestDTO request = createValidCheckInRequest();
        when(checkInService.checkIn(any())).thenReturn(createValidCheckInResponse());

        mockMvc.perform(post("/api/checkin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // ===== POST /api/checkin/existing - Existing customer check-in (PUBLIC) =====

    @Test
    void checkInExisting_withoutAuth_shouldReturn200() throws Exception {
        when(checkInService.checkInExistingCustomer(anyString())).thenReturn(createValidCustomer());

        mockMvc.perform(post("/api/checkin/existing")
                        .param("phoneOrEmail", "test@example.com"))
                .andExpect(status().isOk());
    }

    // ===== POST /api/checkin/guest - Guest check-in (PUBLIC) =====

    @Test
    void checkInGuest_withoutAuth_shouldReturn200() throws Exception {
        when(checkInService.checkInGuest(anyString(), anyString())).thenReturn(createValidCustomer());

        mockMvc.perform(post("/api/checkin/guest")
                        .param("name", "Test Guest")
                        .param("phoneNumber", "1234567890"))
                .andExpect(status().isOk());
    }

    // ===== GET /api/checkin/guests/today - View guest data (PROTECTED) =====

    @Test
    void getTodayCheckedInGuests_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/checkin/guests/today"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void getTodayCheckedInGuests_withTechnicianRole_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/checkin/guests/today"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void getTodayCheckedInGuests_withFrontDeskRole_shouldReturn200() throws Exception {
        when(checkInService.getTodayCheckedInGuests()).thenReturn(List.of(createValidCustomer()));

        mockMvc.perform(get("/api/checkin/guests/today"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void getTodayCheckedInGuests_withManagerRole_shouldReturn200() throws Exception {
        when(checkInService.getTodayCheckedInGuests()).thenReturn(List.of(createValidCustomer()));

        mockMvc.perform(get("/api/checkin/guests/today"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTodayCheckedInGuests_withAdminRole_shouldReturn200() throws Exception {
        when(checkInService.getTodayCheckedInGuests()).thenReturn(List.of(createValidCustomer()));

        mockMvc.perform(get("/api/checkin/guests/today"))
                .andExpect(status().isOk());
    }
}
