// src/main/java/com/salonhub/api/employee/dto/EmployeeResponse.java
package com.salonhub.api.employee.dto;

import com.salonhub.api.employee.model.Role;

public class EmployeeResponseDTO {
    private Long id;
    private String name;
    private boolean available;
    private Role role;

    public EmployeeResponseDTO() { }

    public EmployeeResponseDTO(Long id, String name, boolean available, Role role) {
        this.id = id;
        this.name = name;
        this.available = available;
        this.role = role;
    }

    // ─── Getters & Setters ─────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
