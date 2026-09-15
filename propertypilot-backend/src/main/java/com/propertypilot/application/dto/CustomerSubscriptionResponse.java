package com.propertypilot.application.dto;

import java.time.LocalDate;
import java.util.UUID;

public class CustomerSubscriptionResponse {

    private UUID customerSubscriptionId;

    private UUID customerId;

    private UUID planVersionId;

    private String status;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean autoRenew;

    public UUID getCustomerSubscriptionId() {
        return customerSubscriptionId;
    }

    public void setCustomerSubscriptionId(
            UUID customerSubscriptionId) {
        this.customerSubscriptionId =
                customerSubscriptionId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(
            UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getPlanVersionId() {
        return planVersionId;
    }

    public void setPlanVersionId(
            UUID planVersionId) {
        this.planVersionId = planVersionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(
            LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(
            LocalDate endDate) {
        this.endDate = endDate;
    }

    public Boolean getAutoRenew() {
        return autoRenew;
    }

    public void setAutoRenew(
            Boolean autoRenew) {
        this.autoRenew = autoRenew;
    }
}