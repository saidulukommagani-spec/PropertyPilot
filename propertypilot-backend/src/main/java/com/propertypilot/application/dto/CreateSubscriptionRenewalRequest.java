package com.propertypilot.application.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class CreateSubscriptionRenewalRequest {

    @NotNull(message = "Customer Subscription Id is required")
    private UUID customerSubscriptionId;

    private UUID paymentId;

    @NotNull(message = "Period Start is required")
    private LocalDate periodStart;

    @NotNull(message = "Period End is required")
    private LocalDate periodEnd;

    private String renewalType;

    private BigDecimal renewalAmount;

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