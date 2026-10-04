package com.propertypilot.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Subscription renewal request")
public class CreateSubscriptionRenewalRequest {

    @Schema(
            description = "Customer subscription identifier",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    @NotNull(message =
            "Customer Subscription Id is required")
    private UUID customerSubscriptionId;

    @Schema(
            description = "Payment identifier",
            example = "550e8400-e29b-41d4-a716-446655440001"
    )
    private UUID paymentId;

    @Schema(
            description = "Subscription period start date",
            example = "2026-01-01"
    )
    @NotNull(message =
            "Period Start is required")
    private LocalDate periodStart;

    @Schema(
            description = "Subscription period end date",
            example = "2026-12-31"
    )
    @NotNull(message =
            "Period End is required")
    private LocalDate periodEnd;

    @Schema(
            description = "Renewal type",
            example = "MANUAL"
    )
    private String renewalType;

    @Schema(
            description = "Renewal amount",
            example = "9999.99"
    )
    @PositiveOrZero(
            message = "Renewal amount cannot be negative")
    private BigDecimal renewalAmount;

    @Schema(
            description = "Renewal remarks",
            example = "Annual renewal completed"
    )
    @Size(
            max = 500,
            message = "Remarks cannot exceed 500 characters")
    private String remarks;

    public UUID getCustomerSubscriptionId() {
        return customerSubscriptionId;
    }

    public void setCustomerSubscriptionId(
            UUID customerSubscriptionId) {
        this.customerSubscriptionId =
                customerSubscriptionId;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(
            UUID paymentId) {
        this.paymentId = paymentId;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(
            LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(
            LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    public String getRenewalType() {
        return renewalType;
    }

    public void setRenewalType(
            String renewalType) {
        this.renewalType = renewalType;
    }

    public BigDecimal getRenewalAmount() {
        return renewalAmount;
    }

    public void setRenewalAmount(
            BigDecimal renewalAmount) {
        this.renewalAmount = renewalAmount;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(
            String remarks) {
        this.remarks = remarks;
    }
}