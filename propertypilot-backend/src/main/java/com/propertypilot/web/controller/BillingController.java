package com.propertypilot.web.controller;

import com.propertypilot.application.dto.InvoiceResponse;
import com.propertypilot.application.service.BillingService;
import com.propertypilot.domain.enums.BillingEntityType;

import jakarta.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
@Validated
public class BillingController {

    private final BillingService billingService;

    /**
     * Get invoice by id
     */
    @GetMapping("/invoices/{invoiceId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<InvoiceResponse>
    getInvoice(
            @PathVariable
            @NotNull
            UUID invoiceId) {

        return ResponseEntity.ok(
                billingService.getInvoice(
                        invoiceId));
    }

    /**
     * Get all invoices
     */
    @GetMapping("/invoices")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<InvoiceResponse>>
    getAllInvoices() {

        return ResponseEntity.ok(
                billingService.getAllInvoices());
    }

    /**
     * Get customer invoices
     */
    @GetMapping(
            "/customers/{customerId}/invoices")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<InvoiceResponse>>
    getCustomerInvoices(
            @PathVariable
            @NotNull
            UUID customerId) {

        return ResponseEntity.ok(
                billingService
                        .getInvoicesByCustomer(
                                customerId));
    }

    /**
     * Get invoices by entity
     * Examples:
     * DEAL
     * SERVICE_REQUEST
     * SUBSCRIPTION
     */
    @GetMapping(
            "/invoices/entity/{entityType}/{entityId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<InvoiceResponse>>
    getEntityInvoices(
            @PathVariable
            BillingEntityType entityType,

            @PathVariable
            @NotNull
            UUID entityId) {

        return ResponseEntity.ok(
                billingService
                        .getInvoicesByEntity(
                                entityType,
                                entityId));
    }
}