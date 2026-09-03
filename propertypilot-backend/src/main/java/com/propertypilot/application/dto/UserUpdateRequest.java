package com.propertypilot.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(

        @NotBlank(message = "Full name is required")
        @Size(max = 150)
        String fullName,

        @NotBlank(message = "Email is required")
        @Email
        @Size(max = 255)
        String email,

        @NotBlank(message = "Mobile number is required")
        @Size(max = 20)
        String mobileNumber
) {
}