package com.propertypilot.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Subscription renewal response")
public class SubscriptionRenewalResponse {

    @Schema(description = "Renewal identifier")
    private UUID subscriptionRenewalId;

    @Schema(description = "Customer subscription identifier")
    private UUID customerSubscriptionId;

    @Schema(description = "New customer subscription identifier")
    private UUID newCustomerSubscriptionId;

    @Schema(description = "Payment identifier")
    private UUID paymentId;

    @Schema(description = "Renewal date")
    private LocalDate renewalDate;

    @Schema(description = "Period start date")
    private LocalDate periodStart;

    @Schema(description = "Period end date")
    private LocalDate periodEnd;

    @Schema(description = "Renewal type")
    private String renewalType;

    @Schema(description = "Renewal amount")
    private BigDecimal renewalAmount;

    @Schema(description = "Remarks")
    private String remarks;

    @Schema(description = "Renewal status")
    private String status;

    public UUID getSubscriptionRenewalId() {
        return subscriptionRenewalId;
    }

    public void setSubscriptionRenewalId(
            UUID subscriptionRenewalId) {
        this.subscriptionRenewalId =
                subscriptionRenewalId;
    }

    public UUID getCustomerSubscriptionId() {
        return customerSubscriptionId;
    }

    public void setCustomerSubscriptionId(
            UUID customerSubscriptionId) {
        this.customerSubscriptionId =
                customerSubscriptionId;
    }

    public UUID getNewCustomerSubscriptionId() {
        return newCustomerSubscriptionId;
    }

    public void setNewCustomerSubscriptionId(
            UUID newCustomerSubscriptionId) {
        this.newCustomerSubscriptionId =
                newCustomerSubscriptionId;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(
            UUID paymentId) {
        this.paymentId = paymentId;
    }

    public LocalDate getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(
            LocalDate renewalDate) {
        this.renewalDate = renewalDate;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status) {
        this.status = status;
    }
}