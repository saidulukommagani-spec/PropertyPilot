package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateSubscriptionPlanVersionRequest;
import com.propertypilot.application.dto.SubscriptionPlanVersionResponse;
import com.propertypilot.application.dto.UpdateSubscriptionPlanVersionRequest;
import com.propertypilot.application.service.SubscriptionPlanVersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/subscription-plan-versions")
@Tag(
        name = "Subscription Plan Version",
        description =
                "Subscription Plan Version Management APIs")
public class SubscriptionPlanVersionController {

    private final SubscriptionPlanVersionService
            subscriptionPlanVersionService;

    public SubscriptionPlanVersionController(
            SubscriptionPlanVersionService
                    subscriptionPlanVersionService) {

        this.subscriptionPlanVersionService =
                subscriptionPlanVersionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary =
                    "Create Subscription Plan Version")
    public ResponseEntity<SubscriptionPlanVersionResponse>
    createPlanVersion(
            @Valid
            @RequestBody
            CreateSubscriptionPlanVersionRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        subscriptionPlanVersionService
                                .createPlanVersion(
                                        request));
    }

    @GetMapping("/{planVersionId}")
    @Operation(
            summary =
                    "Get Subscription Plan Version")
    public ResponseEntity<SubscriptionPlanVersionResponse>
    getPlanVersion(
            @PathVariable UUID planVersionId) {

        return ResponseEntity.ok(
                subscriptionPlanVersionService
                        .getPlanVersion(
                                planVersionId));
    }

    @GetMapping
    @Operation(
            summary =
                    "Get All Subscription Plan Versions")
    public ResponseEntity<
            List<SubscriptionPlanVersionResponse>>
    getAllPlanVersions() {

        return ResponseEntity.ok(
                subscriptionPlanVersionService
                        .getAllPlanVersions());
    }

    @PutMapping("/{planVersionId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary =
                    "Update Subscription Plan Version")
    public ResponseEntity<SubscriptionPlanVersionResponse>
    updatePlanVersion(
            @PathVariable UUID planVersionId,
            @Valid
            @RequestBody
            UpdateSubscriptionPlanVersionRequest request) {

        return ResponseEntity.ok(
                subscriptionPlanVersionService
                        .updatePlanVersion(
                                planVersionId,
                                request));
    }
}
