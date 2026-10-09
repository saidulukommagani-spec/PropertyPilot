package com.propertypilot.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

public record UserResponse(

        @Schema(
                description = "User identifier",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID userId,

        @Schema(
                description = "Full name",
                example = "John Doe"
        )
        String fullName,

        @Schema(
                description = "Email address",
                example = "john@example.com"
        )
        String email,

        @Schema(
                description = "Mobile number",
                example = "9876543210"
        )
        String mobileNumber,

        @Schema(
                description = "User status",
                example = "ACTIVE"
        )
        String status,

        @Schema(
                description = "Assigned roles",
                example = "[\"ADMIN\",\"OPERATIONS\"]"
        )
        List<String> roles
) {
}