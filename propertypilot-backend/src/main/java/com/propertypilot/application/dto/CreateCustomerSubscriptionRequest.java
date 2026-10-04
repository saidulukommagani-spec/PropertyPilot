package com.propertypilot.application.dto;

import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Create customer subscription request")
public class CreateCustomerSubscriptionRequest {

    @Schema(description = "Plan Version Id")
    @NotNull(message = "Plan Version Id is required")
    private UUID planVersionId;

    @Schema(description = "Subscription Start Date")
    @NotNull(message = "Start Date is required")
    private LocalDate startDate;

    @Schema(description = "Subscription End Date")
    @NotNull(message = "End Date is required")
    private LocalDate endDate;

    @Schema(description = "Auto Renewal Enabled")
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