package com.salonhub.api.appointment.controller;

import com.f    @Test
    void createAppointment_withoutAuth_shouldReturn403() throws Exception {
        AppointmentRequestDTO request = createValidAppointmentRequest();

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }jackson.databind.ObjectMapper;
import com.salonhub.api.appointment.dto.AppointmentRequestDTO;
import com.salonhub.api.appointment.dto.AppointmentResponseDTO;
import com.salonhub.api.appointment.service.AppointmentService;
import com.salonhub.api.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Security tests for AppointmentController role-based permissions.
 * Tests that endpoints properly enforce role requirements:
 * - CREATE appointments: FRONT_DESK, MANAGER, ADMIN
 * - VIEW appointments: TECHNICIAN (own), FRONT_DESK, MANAGER, ADMIN
 * - UPDATE appointments: FRONT_DESK, MANAGER, ADMIN
 * - DELETE appointments: MANAGER, ADMIN
 */
@WebMvcTest(AppointmentController.class)
@Import(TestSecurityConfig.class)
class AppointmentControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AppointmentService appointmentService;

    private AppointmentRequestDTO createValidRequest() {
        AppointmentRequestDTO request = new AppointmentRequestDTO();
        request.setCustomerId(1L);
        request.setEmployeeId(1L);
        request.setServiceIds(List.of(1L));
        return request;
    }

    private AppointmentResponseDTO createValidResponse() {
        AppointmentResponseDTO response = new AppointmentResponseDTO();
        response.setId(1L);
        response.setCustomerId(1L);
        response.setEmployeeId(1L);
        return response;
    }

    // ===== POST /api/appointments - Create appointment =====

    @Test
    void createAppointment_withoutAuth_shouldReturn401() throws Exception {
        AppointmentRequestDTO request = createValidRequest();

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void createAppointment_withTechnicianRole_shouldReturn403() throws Exception {
        AppointmentRequestDTO request = createValidRequest();

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void createAppointment_withFrontDeskRole_shouldReturn200() throws Exception {
        AppointmentRequestDTO request = createValidRequest();
        when(appointmentService.book(any())).thenReturn(createValidResponse());

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void createAppointment_withManagerRole_shouldReturn200() throws Exception {
        AppointmentRequestDTO request = createValidRequest();
        when(appointmentService.book(any())).thenReturn(createValidResponse());

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createAppointment_withAdminRole_shouldReturn200() throws Exception {
        AppointmentRequestDTO request = createValidRequest();
        when(appointmentService.book(any())).thenReturn(createValidResponse());

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // ===== GET /api/appointments/{id} - Get appointment by ID =====

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void getAppointmentById_withTechnicianRole_shouldReturn200() throws Exception {
        // Technician can view appointments (would normally check if it's their own)
        when(appointmentService.getById(anyLong())).thenReturn(createValidResponse());

        mockMvc.perform(get("/api/appointments/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void getAppointmentById_withFrontDeskRole_shouldReturn200() throws Exception {
        when(appointmentService.getById(anyLong())).thenReturn(createValidResponse());

        mockMvc.perform(get("/api/appointments/1"))
                .andExpect(status().isOk());
    }

    // ===== GET /api/appointments/customer/{customerId} - Get appointments by customer =====

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void getAppointmentsByCustomer_withTechnicianRole_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/appointments/customer/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void getAppointmentsByCustomer_withFrontDeskRole_shouldReturn200() throws Exception {
        when(appointmentService.listByCustomer(anyLong())).thenReturn(List.of(createValidResponse()));

        mockMvc.perform(get("/api/appointments/customer/1"))
                .andExpect(status().isOk());
    }

    // ===== GET /api/appointments/employee/{employeeId} - Get appointments by employee =====

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void getAppointmentsByEmployee_withTechnicianRole_shouldReturn403() throws Exception {
        // Without proper self-access setup, this should fail
        mockMvc.perform(get("/api/appointments/employee/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void getAppointmentsByEmployee_withFrontDeskRole_shouldReturn200() throws Exception {
        when(appointmentService.listByEmployee(anyLong())).thenReturn(List.of(createValidResponse()));

        mockMvc.perform(get("/api/appointments/employee/1"))
                .andExpect(status().isOk());
    }

    // ===== PUT /api/appointments/{id} - Update appointment =====

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void updateAppointment_withTechnicianRole_shouldReturn403() throws Exception {
        AppointmentRequestDTO request = createValidRequest();

        mockMvc.perform(put("/api/appointments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void updateAppointment_withFrontDeskRole_shouldReturn200() throws Exception {
        AppointmentRequestDTO request = createValidRequest();
        when(appointmentService.update(anyLong(), any())).thenReturn(createValidResponse());

        mockMvc.perform(put("/api/appointments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // ===== PATCH /api/appointments/{id}/status - Update appointment status =====

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void updateAppointmentStatus_withTechnicianRole_shouldReturn403() throws Exception {
        mockMvc.perform(patch("/api/appointments/1/status")
                        .param("status", "COMPLETED"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void updateAppointmentStatus_withFrontDeskRole_shouldReturn200() throws Exception {
        when(appointmentService.updateStatus(anyLong(), anyString())).thenReturn(createValidResponse());

        mockMvc.perform(patch("/api/appointments/1/status")
                        .param("status", "COMPLETED"))
                .andExpect(status().isOk());
    }

    // ===== DELETE /api/appointments/{id} - Delete appointment =====

    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void deleteAppointment_withFrontDeskRole_shouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/appointments/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void deleteAppointment_withManagerRole_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/appointments/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteAppointment_withAdminRole_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/appointments/1"))
                .andExpect(status().isNoContent());
    }
}
