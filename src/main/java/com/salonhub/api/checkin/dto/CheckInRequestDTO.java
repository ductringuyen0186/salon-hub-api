package com.salonhub.api.checkin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CheckInRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Size(max = 20, message = "Phone number must be less than 20 characters")
    private String phoneNumber;

    private String email;

    private String note;

    // Flag to indicate if this is a guest check-in (true) or existing customer lookup (false)
    private boolean isGuest = false;
}
