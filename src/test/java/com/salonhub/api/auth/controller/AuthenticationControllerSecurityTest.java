package com.salonhub.api.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salonhub.api.auth.dto.AuthenticationRequest;
import com.salonhub.api.auth.dto.AuthenticationResponse;
import com.salonhub.api.auth.dto.RegisterRequest;
import com.salonhub.api.auth.model.User;
import com.salonhub.api.auth.service.AuthenticationService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Security tests for AuthenticationController.
 * Tests that authentication endpoints are properly configured as public.
 */
@WebMvcTest(AuthenticationController.class)
@Import(TestSecurityConfig.class)
class AuthenticationControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationService authenticationService;

    private RegisterRequest createValidRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setName("Test User");
        request.setPhoneNumber("1234567890");
        request.setRole(User.Role.FRONT_DESK);
        return request;
    }

    private AuthenticationRequest createValidAuthRequest() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        return request;
    }

    private AuthenticationResponse createValidAuthResponse() {
        AuthenticationResponse response = new AuthenticationResponse();
        response.setAccessToken("jwt-token-here");
        response.setTokenType("Bearer");
        response.setName("Test User");
        response.setEmail("test@example.com");
        response.setPhoneNumber("1234567890");
        response.setRole(User.Role.FRONT_DESK);
        response.setLastVisit(LocalDateTime.now());
        return response;
    }

    private User createValidUser() {
        return User.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .phoneNumber("1234567890")
                .role(User.Role.FRONT_DESK)
                .enabled(true)
                .build();
    }

    // ===== POST /api/auth/register - Public endpoint =====

    @Test
    void register_withoutAuth_shouldReturn200() throws Exception {
        RegisterRequest request = createValidRegisterRequest();
        AuthenticationResponse response = createValidAuthResponse();
        when(authenticationService.register(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists());
    }

    @Test
    void register_withInvalidData_shouldReturn400() throws Exception {
        RegisterRequest request = new RegisterRequest();
        // Missing required fields

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ===== POST /api/auth/login - Public endpoint =====

    @Test
    void login_withoutAuth_shouldReturn200() throws Exception {
        AuthenticationRequest request = createValidAuthRequest();
        AuthenticationResponse response = createValidAuthResponse();
        when(authenticationService.authenticate(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists());
    }

    @Test
    void login_withInvalidData_shouldReturn400() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest();
        // Missing required fields

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ===== GET /api/auth/me - Requires authentication =====

    @Test
    void getCurrentUser_withoutAuth_shouldReturn400() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "EMPLOYEE")
    void getCurrentUser_withAuth_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void getCurrentUser_withAdminRole_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void getCurrentUser_withCustomerRole_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk());
    }
}
