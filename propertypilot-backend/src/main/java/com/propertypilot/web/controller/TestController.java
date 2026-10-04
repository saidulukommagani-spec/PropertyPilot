package com.propertypilot.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/test")
@Tag(
        name = "Test APIs",
        description = "Security and authentication validation APIs"
)
public class TestController {

    @Operation(
            summary = "Get Current Logged In User",
            description =
                    "Returns currently authenticated username"
    )
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>>
    currentUser(
            Authentication authentication) {

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "username",
                authentication.getName());

        response.put(
                "authenticated",
                true);

        return ResponseEntity.ok(
                response);
    }
}