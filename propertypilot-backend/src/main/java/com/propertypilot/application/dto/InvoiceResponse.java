package com.propertypilot.application.dto;

import com.propertypilot.domain.enums.BillingEntityType;
import com.propertypilot.domain.enums.InvoiceStatus;
import com.propertypilot.domain.enums.InvoiceType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class InvoiceResponse {

    private UUID invoiceId;

    private UUID customerId;

    private String invoiceNumber;

    private BillingEntityType entityType;

    private UUID entityId;

    private InvoiceType invoiceType;

    private BigDecimal invoiceAmount;

    private BigDecimal taxAmount;

    private InvoiceStatus status;

    private OffsetDateTime issuedAt;

    private OffsetDateTime dueAt;

    private OffsetDateTime paidAt;

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