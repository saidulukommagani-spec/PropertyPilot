package com.propertypilot.application.dto;

import java.util.UUID;

public class SubscriptionPlanResponse {

    private UUID subscriptionPlanId;

    private String planCode;

    private String planName;

    private String description;

    private String status;

    public UUID getSubscriptionPlanId() {
        return subscriptionPlanId;
    }

    public void setSubscriptionPlanId(
            UUID subscriptionPlanId) {
        this.subscriptionPlanId =
                subscriptionPlanId;
    }

    public String getPlanCode() {
        return planCode;
    }

    public void setPlanCode(
            String planCode) {
        this.planCode = planCode;
    }

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