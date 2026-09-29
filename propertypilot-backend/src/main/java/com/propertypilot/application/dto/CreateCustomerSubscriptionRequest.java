package com.propertypilot.application.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public class CreateCustomerSubscriptionRequest {

    @NotNull(message = "Plan Version Id is required")
    private UUID planVersionId;

    @NotNull(message = "Start Date is required")
    private LocalDate startDate;

    @NotNull(message = "End Date is required")
    private LocalDate endDate;

    private Boolean autoRenew;

    public UUID getPlanVersionId() {
        return planVersionId;
    }

    public void setPlanVersionId(
            UUID planVersionId) {
        this.planVersionId = planVersionId;
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