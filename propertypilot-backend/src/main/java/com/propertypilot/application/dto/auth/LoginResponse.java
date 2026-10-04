package com.propertypilot.application.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(

        @Schema(
                description = "JWT Access Token"
        )
        String accessToken,

        @Schema(
                description = "Token type",
                example = "Bearer"
        )
        String tokenType,

        @Schema(
                description = "Token expiry in seconds",
                example = "3600"
        )
        long expiresIn
) {
}