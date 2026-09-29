package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreatePaymentRequest;
import com.propertypilot.application.dto.PaymentResponse;
import com.propertypilot.application.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/invoice/{invoiceId}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<PaymentResponse> recordPayment(
            @PathVariable UUID invoiceId,
            @RequestBody CreatePaymentRequest request) {

        return ResponseEntity.ok(
                paymentService.recordPayment(
                        invoiceId,
                        request));
    }

    @GetMapping("/{paymentId}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<PaymentResponse> getPayment(
            @PathVariable UUID paymentId) {

        return ResponseEntity.ok(
                paymentService.getPayment(
                        paymentId));
    }

    @GetMapping("/invoice/{invoiceId}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByInvoice(
            @PathVariable UUID invoiceId) {

        return ResponseEntity.ok(
                paymentService.getPaymentsByInvoice(
                        invoiceId));
    }
}