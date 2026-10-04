package com.propertypilot.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Update customer subscription request")
public class UpdateCustomerSubscriptionRequest {

    @Schema(description = "Subscription Status")
    private String status;

    @Schema(description = "End Date")
    private LocalDate endDate;

    @Schema(description = "Auto Renewal")
    private Boolean autoRenew;



    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status) {
        this.status = status;
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