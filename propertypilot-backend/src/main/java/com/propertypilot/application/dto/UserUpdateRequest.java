package com.propertypilot.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(

        @Schema(
                description = "Full name",
                example = "John Doe"
        )
        @NotBlank(message = "Full name is required")
        @Size(max = 150)
        String fullName,

        @Schema(
                description = "Email address",
                example = "john@example.com"
        )
        @NotBlank(message = "Email is required")
        @Email
        @Size(max = 255)
        String email,

        @Schema(
                description = "Mobile number",
                example = "9876543210"
        )
        @NotBlank(message = "Mobile number is required")
        @Size(max = 20)
        String mobileNumber
) {
}