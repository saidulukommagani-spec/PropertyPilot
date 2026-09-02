package com.propertypilot.application.dto;

import java.util.UUID;

public record UserResponse(
        UUID userId,
        String fullName,
        String email,
        String mobileNumber,
        String status
) {
}