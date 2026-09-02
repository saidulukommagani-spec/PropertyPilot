package com.propertypilot.application.dto.auth;

public record LoginResponse(

        String accessToken,
        String tokenType,
        long expiresIn
) {
}