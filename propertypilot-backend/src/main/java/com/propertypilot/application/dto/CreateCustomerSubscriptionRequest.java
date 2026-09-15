package com.propertypilot.application.dto;

import java.time.LocalDate;
import java.util.UUID;

public class CreateCustomerSubscriptionRequest {

    private UUID planVersionId;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean autoRenew;

    public UUID getPlanVersionId() {
        return planVersionId;
    }

    public void setPlanVersionId(UUID planVersionId) {
        this.planVersionId = planVersionId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Boolean getAutoRenew() {
        return autoRenew;
    }

    public void setAutoRenew(Boolean autoRenew) {
        this.autoRenew = autoRenew;
    }
}