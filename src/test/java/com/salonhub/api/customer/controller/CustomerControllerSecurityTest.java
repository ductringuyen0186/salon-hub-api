package com.salonhub.api.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salonhub.api.customer.dto.CustomerRequestDTO;
import com.salonhub.api.customer.dto.CustomerResponseDTO;
import com.salonhub.api.customer.mapper.CustomerMapper;
import com.salonhub.api.customer.model.Customer;
import com.salonhub.api.customer.service.CustomerService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Security tests for CustomerController role-based permissions.
 * Tests that endpoints properly enforce role requirements:
 * - GET operations: FRONT_DESK, MANAGER, ADMIN
 * - CREATE operations: FRONT_DESK, MANAGER, ADMIN  
 * - UPDATE operations: MANAGER, ADMIN
 * - DELETE operations: ADMIN only
 */
@WebMvcTest(CustomerController.class)
@Import(TestSecurityConfig.class)
class CustomerControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    @MockitoBean
    private CustomerMapper customerMapper;

    private CustomerRequestDTO createValidRequest() {
        CustomerRequestDTO request = new CustomerRequestDTO();
        request.setName("Test Customer");
        request.setEmail("test@example.com");
        request.setPhoneNumber("1234567890");
        return request;
    }

    private CustomerResponseDTO createValidResponse() {
        CustomerResponseDTO response = new CustomerResponseDTO();
        response.setId(1L);
        response.setName("Test Customer");
        response.setEmail("test@example.com");
        response.setPhoneNumber("1234567890");
        return response;
    }

    // ===== GET /api/customers - List all customers =====

    @Test
    void listCustomers_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void listCustomers_withTechnicianRole_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void listCustomers_withFrontDeskRole_shouldReturn200() throws Exception {
        when(customerService.findAll()).thenReturn(List.of(new Customer()));
        when(customerMapper.toResponse(any())).thenReturn(createValidResponse());

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void listCustomers_withManagerRole_shouldReturn200() throws Exception {
        when(customerService.findAll()).thenReturn(List.of(new Customer()));
        when(customerMapper.toResponse(any())).thenReturn(createValidResponse());

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listCustomers_withAdminRole_shouldReturn200() throws Exception {
        when(customerService.findAll()).thenReturn(List.of(new Customer()));
        when(customerMapper.toResponse(any())).thenReturn(createValidResponse());

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk());
    }

    // ===== GET /api/customers/{id} - Get customer by ID =====

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void getCustomerById_withTechnicianRole_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void getCustomerById_withFrontDeskRole_shouldReturn200() throws Exception {
        when(customerService.findById(1L)).thenReturn(Optional.of(new Customer()));
        when(customerMapper.toResponse(any())).thenReturn(createValidResponse());

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk());
    }

    // ===== GET /api/customers?email - Get customer by email =====

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void getCustomerByEmail_withTechnicianRole_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/customers").param("email", "test@example.com"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void getCustomerByEmail_withFrontDeskRole_shouldReturn200() throws Exception {
        when(customerService.findByEmail(anyString())).thenReturn(Optional.of(new Customer()));
        when(customerMapper.toResponse(any())).thenReturn(createValidResponse());

        mockMvc.perform(get("/api/customers").param("email", "test@example.com"))
                .andExpect(status().isOk());
    }

    // ===== POST /api/customers - Create customer =====

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void createCustomer_withTechnicianRole_shouldReturn403() throws Exception {
        CustomerRequestDTO request = createValidRequest();

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void createCustomer_withFrontDeskRole_shouldReturn200() throws Exception {
        CustomerRequestDTO request = createValidRequest();
        when(customerMapper.toEntity(any())).thenReturn(new Customer());
        when(customerService.create(any())).thenReturn(new Customer());
        when(customerMapper.toResponse(any())).thenReturn(createValidResponse());

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void createCustomer_withManagerRole_shouldReturn200() throws Exception {
        CustomerRequestDTO request = createValidRequest();
        when(customerMapper.toEntity(any())).thenReturn(new Customer());
        when(customerService.create(any())).thenReturn(new Customer());
        when(customerMapper.toResponse(any())).thenReturn(createValidResponse());

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // ===== PUT /api/customers/{id} - Update customer =====

    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void updateCustomer_withFrontDeskRole_shouldReturn403() throws Exception {
        CustomerRequestDTO request = createValidRequest();

        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void updateCustomer_withManagerRole_shouldReturn200() throws Exception {
        CustomerRequestDTO request = createValidRequest();
        when(customerMapper.toEntity(any())).thenReturn(new Customer());
        when(customerService.update(anyLong(), any())).thenReturn(Optional.of(new Customer()));
        when(customerMapper.toResponse(any())).thenReturn(createValidResponse());

        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCustomer_withAdminRole_shouldReturn200() throws Exception {
        CustomerRequestDTO request = createValidRequest();
        when(customerMapper.toEntity(any())).thenReturn(new Customer());
        when(customerService.update(anyLong(), any())).thenReturn(Optional.of(new Customer()));
        when(customerMapper.toResponse(any())).thenReturn(createValidResponse());

        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // ===== DELETE /api/customers/{id} - Delete customer =====

    @Test
    @WithMockUser(roles = "MANAGER")
    void deleteCustomer_withManagerRole_shouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCustomer_withAdminRole_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isNoContent());
    }
}
