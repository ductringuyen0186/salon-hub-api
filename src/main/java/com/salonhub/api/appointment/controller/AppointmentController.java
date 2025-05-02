package com.salonhub.api.appointment.controller;

import com.salonhub.api.appointment.dto.AppointmentRequestDTO;
import com.salonhub.api.appointment.dto.AppointmentResponseDTO;
import com.salonhub.api.appointment.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@Validated
public class AppointmentController {
    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> create(@Valid @RequestBody AppointmentRequestDTO dto) {
        return ResponseEntity.ok(service.book(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(service.listByCustomer(customerId));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(service.listByEmployee(employeeId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> update(@PathVariable Long id,
                                                          @Valid @RequestBody AppointmentRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        service.cancel(id);
        return ResponseEntity.noContent().build();
    }
}
