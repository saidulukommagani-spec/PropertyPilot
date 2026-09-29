package com.propertypilot.application.dto;

import java.time.LocalDate;

public class UpdateCustomerSubscriptionRequest {

    private String status;

    private LocalDate endDate;

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