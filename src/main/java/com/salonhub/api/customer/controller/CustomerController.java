package com.salonhub.api.customer.controller;

import com.salonhub.api.customer.dto.CustomerRequestDTO;
import com.salonhub.api.customer.dto.CustomerResponseDTO;
import com.salonhub.api.customer.mapper.CustomerMapper;
import com.salonhub.api.customer.model.Customer;
import com.salonhub.api.customer.service.CustomerService;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService service;
    private final CustomerMapper mapper;

    public CustomerController(CustomerService service, CustomerMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<CustomerResponseDTO> list() {
        return service.findAll()
                      .stream()
                      .map(mapper::toResponse)
                      .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getById(
        @PathVariable @Positive(message = "ID must be a positive number") Long id
    ) {
        return service.findById(id)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(params = "email")
    public ResponseEntity<CustomerResponseDTO> getByEmail(
        @RequestParam String email
    ) {
        return service.findByEmail(email)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> create(
        @RequestBody @Valid CustomerRequestDTO dto
    ) {
        Customer customer = mapper.toEntity(dto);
        Customer created = service.create(customer);
        return ResponseEntity.ok(mapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> update(
        @PathVariable @Positive Long id,
        @RequestBody @Valid CustomerRequestDTO dto
    ) {
        return service.update(id, mapper.toEntity(dto))
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @PathVariable @Positive Long id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}