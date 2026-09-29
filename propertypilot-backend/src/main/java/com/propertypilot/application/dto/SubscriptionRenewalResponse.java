package com.propertypilot.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class SubscriptionRenewalResponse {

    private UUID subscriptionRenewalId;

    private UUID customerSubscriptionId;

    private UUID newCustomerSubscriptionId;

    private UUID paymentId;

    private LocalDate renewalDate;

    private LocalDate periodStart;

    private LocalDate periodEnd;

    private String renewalType;

    private BigDecimal renewalAmount;

    private String remarks;

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