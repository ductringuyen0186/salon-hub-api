package com.salonhub.api.appointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salonhub.api.appointment.dto.AppointmentRequestDTO;
import com.salonhub.api.appointment.dto.AppointmentResponseDTO;
import com.salonhub.api.appointment.model.BookingStatus;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Security tests for AppointmentController.
 * Tests role-based access control for appointment operations.
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

    private AppointmentRequestDTO createValidAppointmentRequest() {
        AppointmentRequestDTO request = new AppointmentRequestDTO();
        request.setCustomerId(1L);
        request.setEmployeeId(1L);
        request.setServiceIds(List.of(1L, 2L));
        request.setStartTime(LocalDateTime.now().plusDays(1));
        return request;
    }

    private AppointmentResponseDTO createValidAppointmentResponse() {
        AppointmentResponseDTO response = new AppointmentResponseDTO();
        response.setId(1L);
        response.setCustomerId(1L);
        response.setEmployeeId(1L);
        response.setStartTime(LocalDateTime.now().plusDays(1));
        response.setStatus(BookingStatus.PENDING);
        return response;
    }

    // ===== POST /api/appointments - Requires FRONT_DESK role or above =====

    @Test
    void createAppointment_withoutAuth_shouldReturn401() throws Exception {
        AppointmentRequestDTO request = createValidAppointmentRequest();

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"TECHNICIAN"})
    void createAppointment_withTechnicianRole_shouldReturn403() throws Exception {
        AppointmentRequestDTO request = createValidAppointmentRequest();

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"FRONT_DESK"})
    void createAppointment_withFrontDeskRole_shouldReturn200() throws Exception {
        AppointmentRequestDTO request = createValidAppointmentRequest();
        AppointmentResponseDTO response = createValidAppointmentResponse();
        when(appointmentService.book(any())).thenReturn(response);

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void createAppointment_withManagerRole_shouldReturn200() throws Exception {
        AppointmentRequestDTO request = createValidAppointmentRequest();
        AppointmentResponseDTO response = createValidAppointmentResponse();
        when(appointmentService.book(any())).thenReturn(response);

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createAppointment_withAdminRole_shouldReturn200() throws Exception {
        AppointmentRequestDTO request = createValidAppointmentRequest();
        AppointmentResponseDTO response = createValidAppointmentResponse();
        when(appointmentService.book(any())).thenReturn(response);

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // ===== GET /api/appointments/{id} - Requires TECHNICIAN role or above =====

    @Test
    void getAppointment_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/appointments/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"TECHNICIAN"})
    void getAppointment_withTechnicianRole_shouldReturn200() throws Exception {
        AppointmentResponseDTO response = createValidAppointmentResponse();
        when(appointmentService.getById(anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/appointments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = {"FRONT_DESK"})
    void getAppointment_withFrontDeskRole_shouldReturn200() throws Exception {
        AppointmentResponseDTO response = createValidAppointmentResponse();
        when(appointmentService.getById(anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/appointments/1"))
                .andExpect(status().isOk());
    }

    // ===== PUT /api/appointments/{id} - Requires FRONT_DESK role or above =====

    @Test
    void updateAppointment_withoutAuth_shouldReturn401() throws Exception {
        AppointmentRequestDTO request = createValidAppointmentRequest();

        mockMvc.perform(put("/api/appointments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"TECHNICIAN"})
    void updateAppointment_withTechnicianRole_shouldReturn403() throws Exception {
        AppointmentRequestDTO request = createValidAppointmentRequest();

        mockMvc.perform(put("/api/appointments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"FRONT_DESK"})
    void updateAppointment_withFrontDeskRole_shouldReturn200() throws Exception {
        AppointmentRequestDTO request = createValidAppointmentRequest();
        AppointmentResponseDTO response = createValidAppointmentResponse();
        when(appointmentService.update(anyLong(), any())).thenReturn(response);

        mockMvc.perform(put("/api/appointments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    // ===== DELETE /api/appointments/{id} - Requires MANAGER role or above =====

    @Test
    void deleteAppointment_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(delete("/api/appointments/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"FRONT_DESK"})
    void deleteAppointment_withFrontDeskRole_shouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/appointments/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void deleteAppointment_withManagerRole_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/appointments/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteAppointment_withAdminRole_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/appointments/1"))
                .andExpect(status().isNoContent());
    }

    // ===== GET /api/appointments - Requires TECHNICIAN role or above =====

    @Test
    void getAllAppointments_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/appointments"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"TECHNICIAN"})
    void getAllAppointments_withTechnicianRole_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/appointments"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"FRONT_DESK"})
    void getAllAppointments_withFrontDeskRole_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/appointments"))
                .andExpect(status().isOk());
    }
}
