package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateCustomerVerificationRequest;
import com.propertypilot.application.dto.CustomerVerificationResponse;
import com.propertypilot.application.dto.VerificationDecisionRequest;
import com.propertypilot.application.service.CustomerVerificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@Tag(
        name = "Customer Verification",
        description =
                "APIs for customer verification workflow management"
)
public class CustomerVerificationController {

    private final CustomerVerificationService
            customerVerificationService;

    public CustomerVerificationController(
            CustomerVerificationService customerVerificationService) {

        this.customerVerificationService =
                customerVerificationService;
    }

    @Operation(
            summary = "Create verification",
            description =
                    "Creates a new customer verification request"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Verification created successfully"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request")
    })
    @PostMapping("/{customerId}/verifications")
    public ResponseEntity<CustomerVerificationResponse>
    createVerification(

            @Parameter(
                    description = "Customer ID",
                    required = true)
            @PathVariable UUID customerId,

            @RequestBody
            CreateCustomerVerificationRequest request) {

        return ResponseEntity.ok(
                customerVerificationService
                        .createVerification(
                                customerId,
                                request));
    }

    @Operation(
            summary = "Get customer verifications",
            description =
                    "Returns all verification records for a customer"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Verification records retrieved")
    })
    @GetMapping("/{customerId}/verifications")
    public ResponseEntity<List<CustomerVerificationResponse>>
    getCustomerVerifications(

            @Parameter(
                    description = "Customer ID",
                    required = true)
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                customerVerificationService
                        .getCustomerVerifications(
                                customerId));
    }

    @Operation(
            summary = "Get verification",
            description =
                    "Returns verification details by verification ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Verification found"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Verification not found")
    })
    @GetMapping("/verifications/{verificationId}")
    public ResponseEntity<CustomerVerificationResponse>
    getVerification(

            @Parameter(
                    description = "Verification ID",
                    required = true)
            @PathVariable UUID verificationId) {

        return ResponseEntity.ok(
                customerVerificationService
                        .getVerification(
                                verificationId));
    }

    @Operation(
            summary = "Approve verification",
            description =
                    "Approves a customer verification request"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Verification approved"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Verification not found")
    })
    @PutMapping("/verifications/{verificationId}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS')")
    public ResponseEntity<CustomerVerificationResponse>
    approveVerification(

            @Parameter(
                    description = "Verification ID",
                    required = true)
            @PathVariable UUID verificationId,

            @RequestBody
            VerificationDecisionRequest request) {

        return ResponseEntity.ok(
                customerVerificationService
                        .approveVerification(
                                verificationId,
                                request));
    }

    @Operation(
            summary = "Reject verification",
            description =
                    "Rejects a customer verification request"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Verification rejected"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Verification not found")
    })
    @PutMapping("/verifications/{verificationId}/reject")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS')")
    public ResponseEntity<CustomerVerificationResponse>
    rejectVerification(

            @Parameter(
                    description = "Verification ID",
                    required = true)
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
