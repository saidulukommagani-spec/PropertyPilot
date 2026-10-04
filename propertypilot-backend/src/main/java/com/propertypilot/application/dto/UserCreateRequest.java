package com.propertypilot.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(

        @Schema(
                description = "Full name of the user",
                example = "John Doe"
        )
        @NotBlank(message = "Full name is required")
        @Size(max = 150,
                message = "Full name must not exceed 150 characters")
        String fullName,

        @Schema(
                description = "Email address",
                example = "john@example.com"
        )
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 255,
                message = "Email must not exceed 255 characters")
        String email,

        @Schema(
                description = "Mobile number",
                example = "9876543210"
        )
        @NotBlank(message = "Mobile number is required")
        @Size(max = 20,
                message = "Mobile number must not exceed 20 characters")
        String mobileNumber,

        @Schema(
                description = "Account password",
                example = "Password123"
        )
        @NotBlank(message = "Password is required")
        @Size(
                min = 8,
                max = 100,
                message = "Password must be between 8 and 100 characters"
        )
        String password
) {
}