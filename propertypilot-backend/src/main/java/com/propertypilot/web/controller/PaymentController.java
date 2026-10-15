package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreatePaymentRequest;
import com.propertypilot.application.dto.PaymentResponse;
import com.propertypilot.application.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/payments")
@Tag(
        name = "Payment Management",
        description = "Payment APIs")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List payments for operations review")
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @PostMapping("/invoice/{invoiceId}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    @Operation(
            summary = "Initiate payment; settlement is confirmed separately by a signed provider event")
    public ResponseEntity<PaymentResponse>
    recordPayment(
            @PathVariable
            @NotNull
            UUID invoiceId,

            @Valid
            @RequestBody
            CreatePaymentRequest request) {

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                paymentService.recordPayment(
                        invoiceId,
                        request));
    }

    @PostMapping(value = "/settlement", consumes = "text/plain")
    @SecurityRequirements
    @Operation(summary = "Confirm payment settlement using a signed provider event", security = {})
    public ResponseEntity<PaymentResponse> confirmSettlement(
            @RequestBody String rawBody,
            @Parameter(name = "X-PropertyPilot-Signature", in = ParameterIn.HEADER, required = true,
                    description = "HMAC-SHA256 signature of the unmodified request body")
            @RequestHeader("X-PropertyPilot-Signature") String signature) {
        return ResponseEntity.ok(paymentService.confirmSettlement(rawBody, signature));
    }

    @GetMapping("/{paymentId}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    @Operation(
            summary = "Get payment")
    public ResponseEntity<PaymentResponse>
    getPayment(
            @PathVariable
            @NotNull
            UUID paymentId) {

        return ResponseEntity.ok(
                paymentService.getPayment(
                        paymentId));
    }

    @GetMapping("/invoice/{invoiceId}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    @Operation(
            summary = "Get payments by invoice")
    public ResponseEntity<
            List<PaymentResponse>>
    getPaymentsByInvoice(
            @PathVariable
            @NotNull
            UUID invoiceId) {

        return ResponseEntity.ok(
                paymentService
                        .getPaymentsByInvoice(
                                invoiceId));
    }
}
