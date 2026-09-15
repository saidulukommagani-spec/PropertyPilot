package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateCustomerSubscriptionRequest;
import com.propertypilot.application.dto.CustomerSubscriptionResponse;
import com.propertypilot.application.dto.UpdateCustomerSubscriptionRequest;
import com.propertypilot.application.service.CustomerSubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerSubscriptionController {

    private final CustomerSubscriptionService
            customerSubscriptionService;

    public CustomerSubscriptionController(
            CustomerSubscriptionService customerSubscriptionService) {

        this.customerSubscriptionService =
                customerSubscriptionService;
    }

    @PostMapping("/{customerId}/subscriptions")
    public ResponseEntity<CustomerSubscriptionResponse>
    createSubscription(
            @PathVariable UUID customerId,
            @RequestBody
            CreateCustomerSubscriptionRequest request) {

        return ResponseEntity.ok(
                customerSubscriptionService
                        .createSubscription(
                                customerId,
                                request));
    }

    @GetMapping("/{customerId}/subscriptions")
    public ResponseEntity<List<CustomerSubscriptionResponse>>
    getCustomerSubscriptions(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                customerSubscriptionService
                        .getCustomerSubscriptions(
                                customerId));
    }

    @GetMapping("/subscriptions/{subscriptionId}")
    public ResponseEntity<CustomerSubscriptionResponse>
    getSubscription(
            @PathVariable UUID subscriptionId) {

        return ResponseEntity.ok(
                customerSubscriptionService
                        .getSubscription(
                                subscriptionId));
    }

    @PutMapping("/subscriptions/{subscriptionId}")
    public ResponseEntity<CustomerSubscriptionResponse>
    updateSubscription(
            @PathVariable UUID subscriptionId,
            @RequestBody
            UpdateCustomerSubscriptionRequest request) {

        return ResponseEntity.ok(
                customerSubscriptionService
                        .updateSubscription(
                                subscriptionId,
                                request));
    }
}