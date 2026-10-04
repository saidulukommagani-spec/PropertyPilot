package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateGpsVerificationRequest;
import com.propertypilot.application.dto.GpsVerificationResponse;
import com.propertypilot.application.service.GpsVerificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/gps-verifications")
@Tag(
        name = "GPS Verification",
        description = "GPS verification APIs")
@SecurityRequirement(name = "bearerAuth")
public class GpsVerificationController {

    private final GpsVerificationService
            gpsVerificationService;

    public GpsVerificationController(
            GpsVerificationService
                    gpsVerificationService) {

        this.gpsVerificationService =
                gpsVerificationService;
    }

    @PostMapping
    @Operation(
            summary = "Create GPS verification")
    public GpsVerificationResponse
    createVerification(
            @Valid
            @RequestBody
            CreateGpsVerificationRequest request) {

        return gpsVerificationService
                .createVerification(request);
    }

    @GetMapping("/{gpsVerificationId}")
    @Operation(
            summary = "Get GPS verification by id")
    public GpsVerificationResponse
    getVerification(
            @PathVariable
            @NotNull
            UUID gpsVerificationId) {

        return gpsVerificationService
                .getVerification(
                        gpsVerificationId);
    }
}