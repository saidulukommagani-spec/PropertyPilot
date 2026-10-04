package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateCustomerSubscriptionRequest;
import com.propertypilot.application.dto.CustomerSubscriptionResponse;
import com.propertypilot.application.dto.UpdateCustomerSubscriptionRequest;
import com.propertypilot.application.service.CustomerSubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@Tag(
        name = "Customer Subscription Management",
        description = "APIs for managing customer subscriptions"
)
public class CustomerSubscriptionController {

    private final CustomerSubscriptionService
            customerSubscriptionService;

    public CustomerSubscriptionController(
            CustomerSubscriptionService customerSubscriptionService) {

        this.customerSubscriptionService =
                customerSubscriptionService;
    }

    @Operation(
            summary = "Create customer subscription",
            description = "Creates a new subscription for a customer"
    )
    @PostMapping("/{customerId}/subscriptions")
    public ResponseEntity<CustomerSubscriptionResponse>
    createSubscription(
            @Parameter(description = "Customer Id")
            @PathVariable UUID customerId,

            @Valid
            @RequestBody
            CreateCustomerSubscriptionRequest request) {

        return ResponseEntity.ok(
                customerSubscriptionService
                        .createSubscription(
                                customerId,
                                request));
    }

    @Operation(
            summary = "Get customer subscriptions",
            description = "Returns all subscriptions for a customer"
    )
    @GetMapping("/{customerId}/subscriptions")
    public ResponseEntity<List<CustomerSubscriptionResponse>>
    getCustomerSubscriptions(
            @Parameter(description = "Customer Id")
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                customerSubscriptionService
                        .getCustomerSubscriptions(
                                customerId));
    }

    @Operation(
            summary = "Get subscription",
            description = "Returns subscription details"
    )
    @GetMapping("/subscriptions/{subscriptionId}")
    public ResponseEntity<CustomerSubscriptionResponse>
    getSubscription(
            @Parameter(description = "Subscription Id")
            @PathVariable UUID subscriptionId) {

        return ResponseEntity.ok(
                customerSubscriptionService
                        .getSubscription(
                                subscriptionId));
    }

    @Operation(
            summary = "Update subscription",
            description = "Updates subscription details"
    )
    @PutMapping("/subscriptions/{subscriptionId}")
    public ResponseEntity<CustomerSubscriptionResponse>
    updateSubscription(
            @Parameter(description = "Subscription Id")
            @PathVariable UUID subscriptionId,

            @RequestBody
            UpdateCustomerSubscriptionRequest request) {

        return ResponseEntity.ok(
                customerSubscriptionService
                        .updateSubscription(
                                subscriptionId,
                                request));
    }

    @Operation(
            summary = "Get expiring subscriptions",
            description = "Returns subscriptions expiring within given days"
    )
    @GetMapping("/subscriptions/expiring")
    public ResponseEntity<
            List<CustomerSubscriptionResponse>>
    getExpiringSubscriptions(

            @Parameter(
                    description = "Days before expiration",
                    example = "30")
            @RequestParam(defaultValue = "30")
            Integer days) {

        return ResponseEntity.ok(
                customerSubscriptionService
                        .getExpiringSubscriptions(
                                days));
    }
}