package com.propertypilot.application.dto;

public record CustomerCreateRequest(
        String customerType,
        String preferredLocation
) {
}