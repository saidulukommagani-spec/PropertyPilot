package com.propertypilot.application.dto;

public class UpdateSubscriptionPlanRequest {

    private String planName;

    private String description;

    private String status;

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(
            String planName) {
        this.planName = planName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status) {
        this.status = status;
    }
}