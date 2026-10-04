package com.propertypilot.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiConfigTest {

    @Test
    void customOpenAPI_ShouldCreateOpenApi() {

        OpenApiConfig config =
                new OpenApiConfig();

        OpenAPI api =
                config.customOpenAPI();

        assertNotNull(api);

        assertFalse(
                api.getSecurity().isEmpty());
    }
}