package com.propertypilot.application.dto;

import com.propertypilot.domain.enums.BillingEntityType;
import com.propertypilot.domain.enums.InvoiceStatus;
import com.propertypilot.domain.enums.InvoiceType;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.util.UUID;

@Schema(
        name = "InvoiceResponse",
        description = "Invoice response payload"
)
public class InvoiceResponse {

    @Schema(description = "Invoice identifier")
    private UUID invoiceId;

    @Schema(description = "Customer identifier")
    private UUID customerId;

    private String customerName;

    @Schema(
            description = "Invoice number",
            example = "INV-2026-00001"
    )
    private String invoiceNumber;

    @Schema(description = "Billing entity type")
    private BillingEntityType entityType;

    @Schema(description = "Entity identifier")
    private UUID entityId;

    @Schema(description = "Invoice type")
    private InvoiceType invoiceType;

    @Schema(
            description = "Invoice amount",
            example = "1500.00"
    )
    private BigDecimal invoiceAmount;

    @Schema(
            description = "Tax amount",
            example = "270.00"
    )
    private BigDecimal taxAmount;

    private String currencyCode;

    private LocalDate renewalPeriodStart;
    private LocalDate renewalPeriodEnd;

    @Schema(description = "Invoice status")
    private InvoiceStatus status;

    @Schema(description = "Invoice issue date")
    private OffsetDateTime issuedAt;

    @Schema(description = "Invoice due date")
    private OffsetDateTime dueAt;

    @Schema(description = "Invoice paid date")
    private OffsetDateTime paidAt;

    @Schema(
            description = "Invoice description",
            example = "Property inspection service invoice"
    )
    private String description;

    public UUID getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(UUID invoiceId) {
        this.invoiceId = invoiceId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public BillingEntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(
            BillingEntityType entityType) {
        this.entityType = entityType;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(
            InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(
            BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(
            BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public LocalDate getRenewalPeriodStart() { return renewalPeriodStart; }
    public void setRenewalPeriodStart(LocalDate renewalPeriodStart) { this.renewalPeriodStart = renewalPeriodStart; }
    public LocalDate getRenewalPeriodEnd() { return renewalPeriodEnd; }
    public void setRenewalPeriodEnd(LocalDate renewalPeriodEnd) { this.renewalPeriodEnd = renewalPeriodEnd; }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(
            InvoiceStatus status) {
        this.status = status;
    }

    public OffsetDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(
            OffsetDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public OffsetDateTime getDueAt() {
        return dueAt;
    }

    public void setDueAt(
            OffsetDateTime dueAt) {
        this.dueAt = dueAt;
    }

    public OffsetDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(
            OffsetDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {
        this.description = description;
    }
}
