package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateCustomerVerificationRequest;
import com.propertypilot.application.dto.CustomerVerificationResponse;
import com.propertypilot.application.dto.VerificationDecisionRequest;
import com.propertypilot.application.service.CustomerVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerVerificationController {

    private final CustomerVerificationService
            customerVerificationService;

    public CustomerVerificationController(
            CustomerVerificationService customerVerificationService) {

        this.customerVerificationService =
                customerVerificationService;
    }

    @PostMapping("/{customerId}/verifications")
    public ResponseEntity<CustomerVerificationResponse>
    createVerification(
            @PathVariable UUID customerId,
            @RequestBody
            CreateCustomerVerificationRequest request) {

        return ResponseEntity.ok(
                customerVerificationService
                        .createVerification(
                                customerId,
                                request));
    }

    @GetMapping("/{customerId}/verifications")
    public ResponseEntity<List<CustomerVerificationResponse>>
    getCustomerVerifications(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                customerVerificationService
                        .getCustomerVerifications(
                                customerId));
    }

    @GetMapping("/verifications/{verificationId}")
    public ResponseEntity<CustomerVerificationResponse>
    getVerification(
            @PathVariable UUID verificationId) {

        return ResponseEntity.ok(
                customerVerificationService
                        .getVerification(
                                verificationId));
    }

    @PutMapping("/verifications/{verificationId}/approve")
    public ResponseEntity<CustomerVerificationResponse>
    approveVerification(
            @PathVariable UUID verificationId,
            @RequestBody
            VerificationDecisionRequest request) {

        return ResponseEntity.ok(
                customerVerificationService
                        .approveVerification(
                                verificationId,
                                request));
    }

    @PutMapping("/verifications/{verificationId}/reject")
    public ResponseEntity<CustomerVerificationResponse>
    rejectVerification(
            @PathVariable UUID verificationId,
            @RequestBody
            VerificationDecisionRequest request) {

        return ResponseEntity.ok(
                customerVerificationService
                        .rejectVerification(
                                verificationId,
                                request));
    }
}