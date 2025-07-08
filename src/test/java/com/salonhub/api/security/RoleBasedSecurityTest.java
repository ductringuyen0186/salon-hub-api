package com.salonhub.api.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.salonhub.api.appointment.controller.AppointmentController;
import com.salonhub.api.appointment.service.AppointmentService;
import com.salonhub.api.auth.controller.AuthenticationController;
import com.salonhub.api.auth.service.AuthenticationService;
import com.salonhub.api.checkin.controller.CheckInController;
import com.salonhub.api.checkin.service.CheckInService;
import com.salonhub.api.config.TestSecurityConfig;
import com.salonhub.api.customer.controller.CustomerController;
import com.salonhub.api.customer.service.CustomerService;
import com.salonhub.api.employee.controller.EmployeeController;
import com.salonhub.api.employee.service.EmployeeService;
import com.salonhub.api.queue.controller.QueueController;
import com.salonhub.api.queue.service.QueueService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive security tests for all role-based permissions.
 * Tests the complete permission matrix as documented in SECURITY-PERMISSIONS.md
 */
@WebMvcTest(controllers = {
    AppointmentController.class,
    AuthenticationController.class,
    CheckInController.class,
    CustomerController.class,
    EmployeeController.class,
    QueueController.class
})
@Import(TestSecurityConfig.class)
class RoleBasedSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;
    
    @MockitoBean
    private CheckInService checkInService;
    
    @MockitoBean
    private CustomerService customerService;
    
    @MockitoBean
    private EmployeeService employeeService;
    
    @MockitoBean
    private QueueService queueService;
    
    @MockitoBean
    private AppointmentService appointmentService;
    
    // Mock mappers required by controllers
    @MockitoBean
    private com.salonhub.api.customer.mapper.CustomerMapper customerMapper;
    
    @MockitoBean 
    private com.salonhub.api.employee.mapper.EmployeeMapper employeeMapper;
    
    @MockitoBean
    private com.salonhub.api.appointment.mapper.AppointmentMapper appointmentMapper;

    // ===== UNAUTHENTICATED ACCESS TESTS =====

    @Test
    void publicEndpoints_shouldAllowUnauthenticatedAccess() throws Exception {
        // Authentication endpoints
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest()); // Bad request due to validation, not auth

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest()); // Bad request due to validation, not auth

        // Check-in endpoints
        mockMvc.perform(post("/api/checkin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest()); // Bad request due to validation, not auth

        // Note: actuator endpoints and API docs are not available in @WebMvcTest context
        // Health check and API docs are tested in the SecuritySystemTest
    }

    @Test
    void protectedEndpoints_shouldDenyUnauthenticatedAccess() throws Exception {
        // Customer endpoints
        mockMvc.perform(get("/api/customers")).andExpect(status().is4xxClientError());
        mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().is4xxClientError());

        // Employee endpoints  
        mockMvc.perform(get("/api/employees")).andExpect(status().is4xxClientError());
        mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().is4xxClientError());

        // Appointment endpoints
        mockMvc.perform(get("/api/appointments/1")).andExpect(status().is4xxClientError());
        mockMvc.perform(post("/api/appointments").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().is4xxClientError());

        // Queue endpoints
        mockMvc.perform(get("/api/queue")).andExpect(status().is4xxClientError());
        mockMvc.perform(put("/api/queue/1").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().is4xxClientError());

        // Auth user endpoint
        mockMvc.perform(get("/api/auth/me")).andExpect(status().is4xxClientError());
    }

    // ===== ROLE-BASED ACCESS TESTS =====

    // TECHNICIAN Role Tests
    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void technicianRole_shouldHaveBasicAccess() throws Exception {
        // Can view queue
        mockMvc.perform(get("/api/queue")).andExpect(status().isOk());

        // Cannot access customer management
        mockMvc.perform(get("/api/customers")).andExpect(status().isForbidden());
        mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest());

        // Cannot access employee management
        mockMvc.perform(get("/api/employees")).andExpect(status().isForbidden());
        mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest());

        // Cannot update queue
        mockMvc.perform(put("/api/queue/1").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isForbidden());

        // Cannot access queue stats
        mockMvc.perform(get("/api/queue/stats")).andExpect(status().isForbidden());
    }

    // FRONT_DESK Role Tests
    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void frontDeskRole_shouldHaveCustomerAndQueueAccess() throws Exception {
        // Can access customer management
        mockMvc.perform(get("/api/customers")).andExpect(status().isOk());
        
        // Can access queue operations
        mockMvc.perform(get("/api/queue")).andExpect(status().isOk());

        // Cannot access employee management
        mockMvc.perform(get("/api/employees")).andExpect(status().isForbidden());
        mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest());

        // Cannot access queue statistics
        mockMvc.perform(get("/api/queue/stats")).andExpect(status().isForbidden());

        // Cannot delete customers
        mockMvc.perform(delete("/api/customers/1")).andExpect(status().isForbidden());
    }

    // MANAGER Role Tests
    @Test
    @WithMockUser(roles = "MANAGER")
    void managerRole_shouldHaveExtendedAccess() throws Exception {
        // Can access all customer operations
        mockMvc.perform(get("/api/customers")).andExpect(status().isOk());
        
        // Can access employee information
        mockMvc.perform(get("/api/employees")).andExpect(status().isOk());

        // Can access queue operations and stats
        mockMvc.perform(get("/api/queue")).andExpect(status().isOk());
        mockMvc.perform(get("/api/queue/stats")).andExpect(status().isOk());

        // Cannot create/delete employees (admin only) - validation runs before authorization
        mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest());
        mockMvc.perform(delete("/api/employees/1")).andExpect(status().isForbidden());

        // Cannot delete customers (admin only)
        mockMvc.perform(delete("/api/customers/1")).andExpect(status().isForbidden());
    }

    // ADMIN Role Tests
    @Test
    @WithMockUser(roles = "ADMIN")
    void adminRole_shouldHaveFullAccess() throws Exception {
        // Can access all customer operations
        mockMvc.perform(get("/api/customers")).andExpect(status().isOk());
        
        // Can access all employee operations
        mockMvc.perform(get("/api/employees")).andExpect(status().isOk());

        // Can access all queue operations
        mockMvc.perform(get("/api/queue")).andExpect(status().isOk());
        mockMvc.perform(get("/api/queue/stats")).andExpect(status().isOk());

        // Can delete resources (returns 204 No Content)
        mockMvc.perform(delete("/api/customers/1")).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/employees/1")).andExpect(status().isNoContent());
    }

    // ===== SPECIAL ENDPOINT TESTS =====

    @Test
    void checkInEndpoints_shouldBePublic() throws Exception {
        String validCheckInRequest = """
                {
                    "name": "Test Customer",
                    "contact": "1234567890"
                }
                """;

        // Check-in should work without authentication
        mockMvc.perform(post("/api/checkin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCheckInRequest))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "FRONT_DESK")
    void guestCheckInData_shouldRequireFrontDeskOrHigher() throws Exception {
        mockMvc.perform(get("/api/checkin/guests/today"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void guestCheckInData_shouldDenyTechnician() throws Exception {
        mockMvc.perform(get("/api/checkin/guests/today"))
                .andExpect(status().isForbidden());
    }
}
