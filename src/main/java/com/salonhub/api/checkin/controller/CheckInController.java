package com.salonhub.api.checkin.controller;

import com.salonhub.api.checkin.dto.CheckInRequestDTO;
import com.salonhub.api.checkin.dto.CheckInResponseDTO;
import com.salonhub.api.customer.model.Customer;
import com.salonhub.api.checkin.service.CheckInService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checkin")
public class CheckInController {

    @Autowired
    private CheckInService checkInService;

    /**
     * Unified check-in endpoint that handles both guest and existing customer check-ins
     */
    @PostMapping
    public ResponseEntity<CheckInResponseDTO> checkIn(@RequestBody @Valid CheckInRequestDTO request) {
        try {
            CheckInResponseDTO response = checkInService.checkIn(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                new CheckInResponseDTO(null, null, null, null, null, false, null, e.getMessage())
            );
        }
    }

    /**
     * Legacy endpoint for existing customer check-in (backward compatibility)
     */
    @PostMapping("/existing")
    public ResponseEntity<Customer> checkInExisting(@RequestParam String phoneOrEmail) {
        try {
            Customer customer = checkInService.checkInExistingCustomer(phoneOrEmail);
            return ResponseEntity.ok(customer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Legacy endpoint for guest check-in (backward compatibility)
     */
    @PostMapping("/guest")
    public ResponseEntity<Customer> checkInGuest(@RequestParam String name, @RequestParam String phoneNumber) {
        try {
            Customer guest = checkInService.checkInGuest(name, phoneNumber);
            return ResponseEntity.ok(guest);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/guests/today")
    public List<Customer> getTodayCheckedInGuests() {
        return checkInService.getTodayCheckedInGuests();
    }
}