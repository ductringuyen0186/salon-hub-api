package com.salonhub.api.customer.controller;

import com.salonhub.api.customer.model.Customer;
import com.salonhub.api.customer.service.CustomerService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

import org.springframework.http.ResponseEntity;
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

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping
    public List<Customer> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getById(@PathVariable Long id) {
        return service.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(params = "email")
    public ResponseEntity<Customer> getByEmail(@RequestParam String email) {
        return service.findByEmail(email)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Customer> create(@RequestBody Customer customer) {
        Customer created = service.create(customer);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> update(
        @PathVariable Long id,
        @RequestBody Customer customer
    ) {
        return service.update(id, customer)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
