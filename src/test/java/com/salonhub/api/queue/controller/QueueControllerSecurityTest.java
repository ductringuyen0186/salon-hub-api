package com.salonhub.api.queue.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salonhub.api.config.TestSecurityConfig;
import com.salonhub.api.queue.dto.QueueEntryDTO;
import com.salonhub.api.queue.dto.QueueUpdateDTO;
import com.salonhub.api.queue.model.QueueStatus;
import com.salonhub.api.queue.service.QueueService;
import com.salonhub.api.testfixtures.QueueTestDataBuilder;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Security tests for QueueController role-based permissions.
 * Tests that endpoints properly enforce role requirements:
 * - GET operations: All authenticated users
 * - UPDATE operations: FRONT_DESK, MANAGER, ADMIN
 * - DELETE operations: FRONT_DESK, MANAGER, ADMIN  
 * - Stats operations: MANAGER, ADMIN
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
        return QueueTestDataBuilder.aQueueEntry().buildEntryDTO();
    }

    private QueueUpdateDTO createValidUpdateRequest() {
        QueueUpdateDTO update = new QueueUpdateDTO();
        update.setEstimatedWaitTime(20);
        update.setNotes("Updated notes");
        return update;
    }

    // ===== GET /api/queue - List all queue entries (ALL AUTHENTICATED) =====

    @Test
    void getQueue_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/queue"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void getQueue_withTechnicianRole_shouldReturn200() throws Exception {
        List<QueueEntryDTO> entries = List.of(createValidQueueEntry());
        when(queueService.getAllQueueEntries()).thenReturn(entries);

        mockMvc.perform(get("/api/queue"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void getQueue_withFrontDeskRole_shouldReturn200() throws Exception {
        List<QueueEntryDTO> entries = List.of(createValidQueueEntry());
        when(queueService.getAllQueueEntries()).thenReturn(entries);

        mockMvc.perform(get("/api/queue"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void getQueue_withManagerRole_shouldReturn200() throws Exception {
        List<QueueEntryDTO> entries = List.of(createValidQueueEntry());
        when(queueService.getAllQueueEntries()).thenReturn(entries);

        mockMvc.perform(get("/api/queue"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getQueue_withAdminRole_shouldReturn200() throws Exception {
        List<QueueEntryDTO> entries = List.of(createValidQueueEntry());
        when(queueService.getAllQueueEntries()).thenReturn(entries);

        mockMvc.perform(get("/api/queue"))
                .andExpect(status().isOk());
    }

    // ===== GET /api/queue/{id} - Get specific queue entry (ALL AUTHENTICATED) =====

    @Test
    void getQueueEntry_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/queue/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void getQueueEntry_withTechnicianRole_shouldReturn200() throws Exception {
        QueueEntryDTO entry = createValidQueueEntry();
        when(queueService.getQueueEntry(anyLong())).thenReturn(entry);

        mockMvc.perform(get("/api/queue/1"))
                .andExpect(status().isOk());
    }

    // ===== PUT /api/queue/{id} - Update queue entry (FRONT_DESK, MANAGER, ADMIN) =====

    @Test
    void updateQueueEntry_withoutAuth_shouldReturn401() throws Exception {
        QueueUpdateDTO update = createValidUpdateRequest();

        mockMvc.perform(put("/api/queue/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void updateQueueEntry_withTechnicianRole_shouldReturn403() throws Exception {
        QueueUpdateDTO update = createValidUpdateRequest();

        mockMvc.perform(put("/api/queue/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void updateQueueEntry_withFrontDeskRole_shouldReturn200() throws Exception {
        QueueUpdateDTO update = createValidUpdateRequest();
        QueueEntryDTO entry = createValidQueueEntry();
        when(queueService.updateQueueEntry(anyLong(), any())).thenReturn(entry);

        mockMvc.perform(put("/api/queue/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void updateQueueEntry_withManagerRole_shouldReturn200() throws Exception {
        QueueUpdateDTO update = createValidUpdateRequest();
        QueueEntryDTO entry = createValidQueueEntry();
        when(queueService.updateQueueEntry(anyLong(), any())).thenReturn(entry);

        mockMvc.perform(put("/api/queue/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateQueueEntry_withAdminRole_shouldReturn200() throws Exception {
        QueueUpdateDTO update = createValidUpdateRequest();
        QueueEntryDTO entry = createValidQueueEntry();
        when(queueService.updateQueueEntry(anyLong(), any())).thenReturn(entry);

        mockMvc.perform(put("/api/queue/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk());
    }

    // ===== DELETE /api/queue/{id} - Remove from queue (FRONT_DESK, MANAGER, ADMIN) =====

    @Test
    void deleteQueueEntry_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(delete("/api/queue/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void deleteQueueEntry_withTechnicianRole_shouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/queue/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void deleteQueueEntry_withFrontDeskRole_shouldReturn204() throws Exception {
        doNothing().when(queueService).removeFromQueue(anyLong());

        mockMvc.perform(delete("/api/queue/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void deleteQueueEntry_withManagerRole_shouldReturn204() throws Exception {
        doNothing().when(queueService).removeFromQueue(anyLong());

        mockMvc.perform(delete("/api/queue/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteQueueEntry_withAdminRole_shouldReturn204() throws Exception {
        doNothing().when(queueService).removeFromQueue(anyLong());

        mockMvc.perform(delete("/api/queue/1"))
                .andExpect(status().isNoContent());
    }

    // ===== PATCH /api/queue/{id}/status - Update status (FRONT_DESK, MANAGER, ADMIN) =====

    @Test
    void updateQueueStatus_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(patch("/api/queue/1/status")
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void updateQueueStatus_withTechnicianRole_shouldReturn403() throws Exception {
        mockMvc.perform(patch("/api/queue/1/status")
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void updateQueueStatus_withFrontDeskRole_shouldReturn200() throws Exception {
        QueueEntryDTO entry = createValidQueueEntry();
        when(queueService.updateQueueStatus(anyLong(), any())).thenReturn(entry);

        mockMvc.perform(patch("/api/queue/1/status")
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk());
    }

    // ===== GET /api/queue/stats - Queue statistics (MANAGER, ADMIN) =====

    @Test
    void getQueueStatistics_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/queue/stats"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void getQueueStatistics_withTechnicianRole_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/queue/stats"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void getQueueStatistics_withFrontDeskRole_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/queue/stats"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void getQueueStatistics_withManagerRole_shouldReturn200() throws Exception {
        QueueService.QueueStatistics stats = new QueueService.QueueStatistics(5, 15, 30);
        when(queueService.getQueueStatistics()).thenReturn(stats);

        mockMvc.perform(get("/api/queue/stats"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getQueueStatistics_withAdminRole_shouldReturn200() throws Exception {
        QueueService.QueueStatistics stats = new QueueService.QueueStatistics(3, 20, 45);
        when(queueService.getQueueStatistics()).thenReturn(stats);

        mockMvc.perform(get("/api/queue/stats"))
                .andExpect(status().isOk());
    }

    // ===== POST /api/queue/refresh - Refresh queue positions (FRONT_DESK, MANAGER, ADMIN) =====

    @Test
    void refreshQueue_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(post("/api/queue/refresh"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void refreshQueue_withTechnicianRole_shouldReturn403() throws Exception {
        mockMvc.perform(post("/api/queue/refresh"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void refreshQueue_withFrontDeskRole_shouldReturn200() throws Exception {
        doNothing().when(queueService).refreshQueuePositions();

        mockMvc.perform(post("/api/queue/refresh"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void refreshQueue_withManagerRole_shouldReturn200() throws Exception {
        doNothing().when(queueService).refreshQueuePositions();

        mockMvc.perform(post("/api/queue/refresh"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void refreshQueue_withAdminRole_shouldReturn200() throws Exception {
        doNothing().when(queueService).refreshQueuePositions();

        mockMvc.perform(post("/api/queue/refresh"))
                .andExpect(status().isOk());
    }
}
