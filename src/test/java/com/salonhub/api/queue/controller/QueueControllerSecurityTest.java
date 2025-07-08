package com.salonhub.api.queue.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salonhub.api.queue.dto.QueueEntryDTO;
import com.salonhub.api.queue.dto.QueueUpdateDTO;
import com.salonhub.api.queue.model.QueueStatus;
import com.salonhub.api.queue.service.QueueService;
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
 * Security tests for QueueController role-based permissions.
 * Tests that endpoints properly enforce role requirements:
 * - VIEW queue: All authenticated users can view queue
 * - MODIFY queue: FRONT_DESK, MANAGER, ADMIN
 * - STATS: MANAGER, ADMIN
 */
@WebMvcTest(QueueController.class)
@Import(TestSecurityConfig.class)
class QueueControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private QueueService queueService;

    private QueueEntryDTO createValidQueueEntry() {
        QueueEntryDTO entry = new QueueEntryDTO();
        entry.setId(1L);
        entry.setCustomerId(1L);
        entry.setCustomerName("Test Customer");
        entry.setStatus(QueueStatus.WAITING);
        entry.setCreatedAt(LocalDateTime.now());
        entry.setPosition(1);
        return entry;
    }

    private QueueUpdateDTO createValidUpdateRequest() {
        QueueUpdateDTO update = new QueueUpdateDTO();
        update.setStatus("IN_PROGRESS");
        update.setEmployeeId(1L);
        return update;
    }

    // ===== GET /api/queue - Get current queue =====

    @Test
    void getCurrentQueue_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/queue"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void getCurrentQueue_withTechnicianRole_shouldReturn200() throws Exception {
        when(queueService.getCurrentQueue()).thenReturn(List.of(createValidQueueEntry()));

        mockMvc.perform(get("/api/queue"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void getCurrentQueue_withFrontDeskRole_shouldReturn200() throws Exception {
        when(queueService.getCurrentQueue()).thenReturn(List.of(createValidQueueEntry()));

        mockMvc.perform(get("/api/queue"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void getCurrentQueue_withManagerRole_shouldReturn200() throws Exception {
        when(queueService.getCurrentQueue()).thenReturn(List.of(createValidQueueEntry()));

        mockMvc.perform(get("/api/queue"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCurrentQueue_withAdminRole_shouldReturn200() throws Exception {
        when(queueService.getCurrentQueue()).thenReturn(List.of(createValidQueueEntry()));

        mockMvc.perform(get("/api/queue"))
                .andExpect(status().isOk());
    }

    // ===== GET /api/queue/{id} - Get specific queue entry =====

    @Test
    void getQueueEntry_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/queue/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void getQueueEntry_withTechnicianRole_shouldReturn200() throws Exception {
        when(queueService.getQueueEntry(1L)).thenReturn(createValidQueueEntry());

        mockMvc.perform(get("/api/queue/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void getQueueEntry_withFrontDeskRole_shouldReturn200() throws Exception {
        when(queueService.getQueueEntry(1L)).thenReturn(createValidQueueEntry());

        mockMvc.perform(get("/api/queue/1"))
                .andExpect(status().isOk());
    }

    // ===== PUT /api/queue/{id} - Update queue entry status =====

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void updateQueueEntry_withTechnicianRole_shouldReturn403() throws Exception {
        QueueUpdateDTO request = createValidUpdateRequest();

        mockMvc.perform(put("/api/queue/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void updateQueueEntry_withFrontDeskRole_shouldReturn200() throws Exception {
        QueueUpdateDTO request = createValidUpdateRequest();
        when(queueService.updateQueueEntry(anyLong(), any())).thenReturn(createValidQueueEntry());

        mockMvc.perform(put("/api/queue/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void updateQueueEntry_withManagerRole_shouldReturn200() throws Exception {
        QueueUpdateDTO request = createValidUpdateRequest();
        when(queueService.updateQueueEntry(anyLong(), any())).thenReturn(createValidQueueEntry());

        mockMvc.perform(put("/api/queue/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateQueueEntry_withAdminRole_shouldReturn200() throws Exception {
        QueueUpdateDTO request = createValidUpdateRequest();
        when(queueService.updateQueueEntry(anyLong(), any())).thenReturn(createValidQueueEntry());

        mockMvc.perform(put("/api/queue/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // ===== DELETE /api/queue/{id} - Remove from queue =====

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void removeFromQueue_withTechnicianRole_shouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/queue/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void removeFromQueue_withFrontDeskRole_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/queue/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void removeFromQueue_withManagerRole_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/queue/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removeFromQueue_withAdminRole_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/queue/1"))
                .andExpect(status().isNoContent());
    }

    // ===== GET /api/queue/stats - Get queue statistics =====

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void getQueueStats_withTechnicianRole_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/queue/stats"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void getQueueStats_withFrontDeskRole_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/queue/stats"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void getQueueStats_withManagerRole_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/queue/stats"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getQueueStats_withAdminRole_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/queue/stats"))
                .andExpect(status().isOk());
    }
}
