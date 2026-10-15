package com.propertypilot.web.controller;

import com.propertypilot.application.dto.InvoiceResponse;
import com.propertypilot.application.service.BillingService;
import com.propertypilot.domain.enums.BillingEntityType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.constraints.NotNull;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/billing")
@Validated
@Tag(
        name = "Billing Management",
        description = "Billing and Invoice APIs"
)
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping("/invoices/{invoiceId}/send")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceResponse> sendInvoice(@PathVariable UUID invoiceId) {
        return ResponseEntity.ok(billingService.sendInvoice(invoiceId));
    }

    @PostMapping("/invoices/{invoiceId}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceResponse> cancelInvoice(@PathVariable UUID invoiceId) {
        return ResponseEntity.ok(billingService.cancelInvoice(invoiceId));
    }

    @Operation(
            summary = "Get invoice by id",
            description = "Returns invoice details for a given invoice id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Invoice retrieved successfully"),
            @ApiResponse(responseCode = "404",
                    description = "Invoice not found")
    })
    @GetMapping("/invoices/{invoiceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InvoiceResponse> getInvoice(

            @Parameter(
                    description = "Invoice identifier",
                    required = true
            )
            @PathVariable
            @NotNull
            UUID invoiceId) {

        return ResponseEntity.ok(
                billingService.getInvoice(
                        invoiceId));
    }

    @Operation(
            summary = "Get all invoices",
            description = "Returns all invoices available in the system"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Invoices retrieved successfully"
    )
    @GetMapping("/invoices")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InvoiceResponse>>
    getAllInvoices() {

        return ResponseEntity.ok(
                billingService.getAllInvoices());
    }

    @Operation(
            summary = "Get customer invoices",
            description =
                    "Returns all invoices belonging to a customer"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Customer invoices retrieved successfully"
    )
    @GetMapping("/customers/{customerId}/invoices")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<List<InvoiceResponse>>
    getCustomerInvoices(

            @Parameter(
                    description = "Customer identifier",
                    required = true
            )
            @PathVariable
            @NotNull
            UUID customerId) {

        return ResponseEntity.ok(
                billingService.getInvoicesByCustomer(
                        customerId));
    }

    @Operation(
            summary = "Get invoices by entity",
            description =
                    "Returns invoices generated for a business entity"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Entity invoices retrieved successfully"
    )
    @GetMapping(
            "/invoices/entity/{entityType}/{entityId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InvoiceResponse>>
    getEntityInvoices(

            @Parameter(
                    description =
                            "Entity Type. Supported values: DEAL, SERVICE_REQUEST, SUBSCRIPTION"
            )
            @PathVariable
            BillingEntityType entityType,

            @Parameter(
                    description = "Entity identifier",
                    required = true
            )
            @PathVariable
            @NotNull
            UUID entityId) {

        return ResponseEntity.ok(
                billingService.getInvoicesByEntity(
                        entityType,
                        entityId));
    }
}
