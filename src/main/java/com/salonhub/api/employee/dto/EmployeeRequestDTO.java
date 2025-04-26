package com.salonhub.api.employee.dto;

import com.salonhub.api.employee.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EmployeeRequestDTO {
  
  @NotBlank(message = "Name must not be blank")
  private String name;

  @NotNull(message = "Availability flag is required")
  private Boolean available;

  @NotNull(message = "Role is required")
  private Role role;

  // ─── Constructors, getters & setters ─────────────────────────────────

  public EmployeeRequestDTO() {}

  public EmployeeRequestDTO(String name, Boolean available, Role role) {
      this.name = name;
      this.available = available;
      this.role = role;
  }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public Boolean getAvailable() { return available; }
  public void setAvailable(Boolean available) { this.available = available; }

  public Role getRole() { return role; }
  public void setRole(Role role) { this.role = role; }
}
