package com.salonhub.api.auth.controller;

import com.salonhub.api.auth.dto.AuthenticationRequest;
import com.salonhub.api.auth.dto.AuthenticationResponse;
import com.salonhub.api.auth.dto.RegisterRequest;
import com.salonhub.api.auth.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody @Valid RegisterRequest request
    ) {
        try {
            return ResponseEntity.ok(service.register(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest request
    ) {
        try {
            return ResponseEntity.ok(service.authenticate(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<AuthenticationResponse> getCurrentUser(
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            // Extract email from JWT token or use SecurityContext
            // For now, this is a placeholder - you'd extract from the JWT
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
