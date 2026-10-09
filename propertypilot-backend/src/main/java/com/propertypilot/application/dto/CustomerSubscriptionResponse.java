package com.propertypilot.application.dto;

import java.time.LocalDate;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Customer Subscription Response")
public class CustomerSubscriptionResponse {

    @Schema(description = "Customer Subscription Id")
    private UUID customerSubscriptionId;

    @Schema(description = "Customer Id")
    private UUID customerId;

    @Schema(description = "Customer display name")
    private String customerName;

    @Schema(description = "Plan Version Id")
    private UUID planVersionId;

    @Schema(description = "Subscription plan display name")
    private String planName;

    @Schema(description = "Subscription Status")
    private String status;

    @Schema(description = "Start Date")
    private LocalDate startDate;

    @Schema(description = "End Date")
    private LocalDate endDate;

    @Schema(description = "Auto Renewal")
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public UUID getPlanVersionId() {
        return planVersionId;
    }

    public void setPlanVersionId(
            UUID planVersionId) {
        this.planVersionId = planVersionId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
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
