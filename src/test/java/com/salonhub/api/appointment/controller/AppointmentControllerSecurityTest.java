package com.salonhub.api.appointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salonhub.api.appointment.dto.AppointmentRequestDTO;
import com.salonhub.api.appointment.service.AppointmentService;
import com.salonhub.api.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AppointmentController.class)
@Import(TestSecurityConfig.class)
class AppointmentControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AppointmentService appointmentService;

    @Test
    void createAppointment_withoutAuth_shouldReturn403() throws Exception {
        AppointmentRequestDTO requestDTO = new AppointmentRequestDTO();
        requestDTO.setCustomerId(1L);
        requestDTO.setEmployeeId(1L);
        requestDTO.setStartTime(LocalDateTime.now().plusDays(1));
        requestDTO.setServiceIds(List.of(1L));

        mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void createAppointment_withTechnicianRole_shouldReturn403() throws Exception {
        AppointmentRequestDTO requestDTO = new AppointmentRequestDTO();
        requestDTO.setCustomerId(1L);
        requestDTO.setEmployeeId(1L);
        requestDTO.setStartTime(LocalDateTime.now().plusDays(1));
        requestDTO.setServiceIds(List.of(1L));

        mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void createAppointment_withFrontDeskRole_shouldReturn200() throws Exception {
        AppointmentRequestDTO requestDTO = new AppointmentRequestDTO();
        requestDTO.setCustomerId(1L);
        requestDTO.setEmployeeId(1L);
        requestDTO.setStartTime(LocalDateTime.now().plusDays(1));
        requestDTO.setServiceIds(List.of(1L));

        mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void getAppointment_withoutAuth_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/appointments/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void getAppointment_withTechnicianRole_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/appointments/1"))
                .andExpect(status().isOk());
    }

    @Test
    void updateAppointment_withoutAuth_shouldReturn403() throws Exception {
        AppointmentRequestDTO requestDTO = new AppointmentRequestDTO();
        requestDTO.setCustomerId(1L);
        requestDTO.setEmployeeId(1L);
        requestDTO.setStartTime(LocalDateTime.now().plusDays(1));
        requestDTO.setServiceIds(List.of(1L));

        mockMvc.perform(put("/api/appointments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void updateAppointment_withFrontDeskRole_shouldReturn200() throws Exception {
        AppointmentRequestDTO requestDTO = new AppointmentRequestDTO();
        requestDTO.setCustomerId(1L);
        requestDTO.setEmployeeId(1L);
        requestDTO.setStartTime(LocalDateTime.now().plusDays(1));
        requestDTO.setServiceIds(List.of(1L));

        mockMvc.perform(put("/api/appointments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteAppointment_withoutAuth_shouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/appointments/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void deleteAppointment_withManagerRole_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/appointments/1"))
                .andExpect(status().isNoContent());
    }
}
