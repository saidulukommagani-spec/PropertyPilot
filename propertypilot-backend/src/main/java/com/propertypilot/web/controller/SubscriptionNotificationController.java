package com.propertypilot.web.controller;

import com.propertypilot.application.dto.SubscriptionNotificationResponse;
import com.propertypilot.application.service.SubscriptionNotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscription-notifications")
@Validated
@Tag(
        name = "Subscription Notifications",
        description =
                "Subscription notification retrieval APIs"
)
public class SubscriptionNotificationController {

    private final SubscriptionNotificationService
            notificationService;

    public SubscriptionNotificationController(
            SubscriptionNotificationService notificationService) {

        this.notificationService =
                notificationService;
    }

    @GetMapping("/{customerSubscriptionId}")
    @Operation(
            summary = "Get Subscription Notifications",
            description =
                    "Returns all notifications generated for a customer subscription."
    )
    public ResponseEntity<
            List<SubscriptionNotificationResponse>>
    getNotifications(

            @Parameter(
                    description =
                            "Customer Subscription Id",
                    required = true
            )
            @PathVariable
            @NotNull
            UUID customerSubscriptionId) {

        return ResponseEntity.ok(
                notificationService.getNotifications(
                        customerSubscriptionId));
    }
}