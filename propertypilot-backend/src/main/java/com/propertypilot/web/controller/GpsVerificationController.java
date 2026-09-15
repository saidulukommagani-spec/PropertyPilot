package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateGpsVerificationRequest;
import com.propertypilot.application.dto.GpsVerificationResponse;
import com.propertypilot.application.service.GpsVerificationService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/gps-verifications")
public class GpsVerificationController {

    private final GpsVerificationService gpsVerificationService;

    public GpsVerificationController(
            GpsVerificationService gpsVerificationService) {

        this.gpsVerificationService =
                gpsVerificationService;
    }

    @PostMapping
    public GpsVerificationResponse createVerification(
            @RequestBody
            CreateGpsVerificationRequest request) {

        return gpsVerificationService
                .createVerification(request);
    }

    @GetMapping("/{gpsVerificationId}")
    public GpsVerificationResponse getVerification(
            @PathVariable
            UUID gpsVerificationId) {

        return gpsVerificationService
                .getVerification(gpsVerificationId);
    }
}