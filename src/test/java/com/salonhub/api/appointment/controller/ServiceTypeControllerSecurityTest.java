package com.salonhub.api.appointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salonhub.api.appointment.ServiceTypeTestDataBuilder;
import com.salonhub.api.appointment.dto.ServiceTypeRequestDTO;
import com.salonhub.api.appointment.dto.ServiceTypeResponseDTO;
import com.salonhub.api.appointment.service.ServiceTypeService;
import com.salonhub.api.config.TestSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security tests for ServiceTypeController role-based permissions.
 * Tests that endpoints properly enforce role requirements:
 * - ALL operations: MANAGER, ADMIN only
 * - No access for: TECHNICIAN, FRONT_DESK
 */
@WebMvcTest(controllers = ServiceTypeController.class)
@Import(TestSecurityConfig.class)
class ServiceTypeControllerSecurityTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockitoBean
    private ServiceTypeService service;
    
    @BeforeEach
    void setUp() {
        // Mock service methods to return successful responses for authorized access
        ServiceTypeResponseDTO mockResponse = ServiceTypeResponseDTO.builder()
                .id(1L)
                .name("Test Service")
                .estimatedDurationMinutes(30)
                .price(new java.math.BigDecimal("25.00"))
                .build();
        
        when(service.findAll()).thenReturn(List.of(mockResponse));
        when(service.findById(anyLong())).thenReturn(java.util.Optional.of(mockResponse));
        when(service.findByName(anyString())).thenReturn(java.util.Optional.of(mockResponse));
        when(service.create(any(ServiceTypeRequestDTO.class))).thenReturn(mockResponse);
        when(service.update(anyLong(), any(ServiceTypeRequestDTO.class))).thenReturn(mockResponse);
        // deleteById returns void, no need to mock
    }
    
    // ===== GET /api/service-types - MANAGER/ADMIN only =====
    
    @Test
    void getAllServiceTypes_withoutAuth_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/service-types"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void getAllServiceTypes_withTechnicianRole_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/service-types"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void getAllServiceTypes_withFrontDeskRole_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/service-types"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "MANAGER")
    void getAllServiceTypes_withManagerRole_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/service-types"))
                .andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllServiceTypes_withAdminRole_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/service-types"))
                .andExpect(status().isOk());
    }
    
    // ===== GET /api/service-types/{id} - MANAGER/ADMIN only =====
    
    @Test
    void getServiceTypeById_withoutAuth_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/service-types/1"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void getServiceTypeById_withTechnicianRole_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/service-types/1"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void getServiceTypeById_withFrontDeskRole_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/service-types/1"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "MANAGER")
    void getServiceTypeById_withManagerRole_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/service-types/1"))
                .andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void getServiceTypeById_withAdminRole_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/service-types/1"))
                .andExpect(status().isOk());
    }
    
    // ===== GET /api/service-types/search - MANAGER/ADMIN only =====
    
    @Test
    void getServiceTypeByName_withoutAuth_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/service-types/search?name=Haircut"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void getServiceTypeByName_withTechnicianRole_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/service-types/search?name=Haircut"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void getServiceTypeByName_withFrontDeskRole_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/service-types/search?name=Haircut"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "MANAGER")
    void getServiceTypeByName_withManagerRole_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/service-types/search?name=Haircut"))
                .andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void getServiceTypeByName_withAdminRole_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/service-types/search?name=Haircut"))
                .andExpect(status().isOk());
    }
    
    // ===== POST /api/service-types - MANAGER/ADMIN only =====
    
    @Test
    void createServiceType_withoutAuth_shouldReturn403() throws Exception {
        ServiceTypeRequestDTO request = ServiceTypeTestDataBuilder.aServiceType().buildRequestDTO();
        
        mockMvc.perform(post("/api/service-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void createServiceType_withTechnicianRole_shouldReturn403() throws Exception {
        ServiceTypeRequestDTO request = ServiceTypeTestDataBuilder.aServiceType().buildRequestDTO();
        
        mockMvc.perform(post("/api/service-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void createServiceType_withFrontDeskRole_shouldReturn403() throws Exception {
        ServiceTypeRequestDTO request = ServiceTypeTestDataBuilder.aServiceType().buildRequestDTO();
        
        mockMvc.perform(post("/api/service-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "MANAGER")
    void createServiceType_withManagerRole_shouldReturn200() throws Exception {
        ServiceTypeRequestDTO request = ServiceTypeTestDataBuilder.aServiceType().buildRequestDTO();
        
        mockMvc.perform(post("/api/service-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void createServiceType_withAdminRole_shouldReturn200() throws Exception {
        ServiceTypeRequestDTO request = ServiceTypeTestDataBuilder.aServiceType().buildRequestDTO();
        
        mockMvc.perform(post("/api/service-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
    
    // ===== PUT /api/service-types/{id} - MANAGER/ADMIN only =====
    
    @Test
    void updateServiceType_withoutAuth_shouldReturn403() throws Exception {
        ServiceTypeRequestDTO request = ServiceTypeTestDataBuilder.aServiceType().buildRequestDTO();
        
        mockMvc.perform(put("/api/service-types/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void updateServiceType_withTechnicianRole_shouldReturn403() throws Exception {
        ServiceTypeRequestDTO request = ServiceTypeTestDataBuilder.aServiceType().buildRequestDTO();
        
        mockMvc.perform(put("/api/service-types/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void updateServiceType_withFrontDeskRole_shouldReturn403() throws Exception {
        ServiceTypeRequestDTO request = ServiceTypeTestDataBuilder.aServiceType().buildRequestDTO();
        
        mockMvc.perform(put("/api/service-types/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "MANAGER")
    void updateServiceType_withManagerRole_shouldReturn200() throws Exception {
        ServiceTypeRequestDTO request = ServiceTypeTestDataBuilder.aServiceType().buildRequestDTO();
        
        mockMvc.perform(put("/api/service-types/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateServiceType_withAdminRole_shouldReturn200() throws Exception {
        ServiceTypeRequestDTO request = ServiceTypeTestDataBuilder.aServiceType().buildRequestDTO();
        
        mockMvc.perform(put("/api/service-types/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
    
    // ===== DELETE /api/service-types/{id} - MANAGER/ADMIN only =====
    
    @Test
    void deleteServiceType_withoutAuth_shouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/service-types/1"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void deleteServiceType_withTechnicianRole_shouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/service-types/1"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void deleteServiceType_withFrontDeskRole_shouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/service-types/1"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "MANAGER")
    void deleteServiceType_withManagerRole_shouldReturn200() throws Exception {
        mockMvc.perform(delete("/api/service-types/1"))
                .andExpect(status().isNoContent());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteServiceType_withAdminRole_shouldReturn200() throws Exception {
        mockMvc.perform(delete("/api/service-types/1"))
                .andExpect(status().isNoContent());
    }
    
    // ===== GET /api/service-types/{id}/exists - MANAGER/ADMIN only =====
    
    @Test
    void existsById_withoutAuth_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/service-types/1/exists"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void existsById_withTechnicianRole_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/service-types/1/exists"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void existsById_withFrontDeskRole_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/service-types/1/exists"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "MANAGER")
    void existsById_withManagerRole_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/service-types/1/exists"))
                .andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void existsById_withAdminRole_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/service-types/1/exists"))
                .andExpect(status().isOk());
    }
}
