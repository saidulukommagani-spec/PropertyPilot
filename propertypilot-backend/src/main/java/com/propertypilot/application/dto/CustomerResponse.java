package com.propertypilot.application.dto;

import java.util.UUID;

public record CustomerResponse(
        UUID customerId,
        String customerType,
        String preferredLocation,
        String status
) {
}