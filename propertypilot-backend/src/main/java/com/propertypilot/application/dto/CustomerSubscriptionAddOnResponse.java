package com.propertypilot.application.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class CustomerSubscriptionAddOnResponse {

    private UUID customerSubscriptionAddOnId;

    private UUID customerSubscriptionId;

    private UUID subscriptionAddOnId;

    private String addOnName;

    private UUID pricingEstimateId;

    private Integer quantity;

    private String status;

    private OffsetDateTime startsAt;

    private OffsetDateTime endsAt;

    public UUID getCustomerSubscriptionAddOnId() {
        return customerSubscriptionAddOnId;
    }

    public void setCustomerSubscriptionAddOnId(
            UUID customerSubscriptionAddOnId) {
        this.customerSubscriptionAddOnId =
                customerSubscriptionAddOnId;
    }

    public UUID getCustomerSubscriptionId() {
        return customerSubscriptionId;
    }

    public void setCustomerSubscriptionId(
            UUID customerSubscriptionId) {
        this.customerSubscriptionId =
                customerSubscriptionId;
    }

    public UUID getSubscriptionAddOnId() {
        return subscriptionAddOnId;
    }

    public void setSubscriptionAddOnId(
            UUID subscriptionAddOnId) {
        this.subscriptionAddOnId =
                subscriptionAddOnId;
    }

    public String getAddOnName() {
        return addOnName;
    }

    public void setAddOnName(
            String addOnName) {
        this.addOnName = addOnName;
    }

    public UUID getPricingEstimateId() {
        return pricingEstimateId;
    }

    public void setPricingEstimateId(
            UUID pricingEstimateId) {
        this.pricingEstimateId =
                pricingEstimateId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(
            Integer quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status) {
        this.status = status;
    }

    public OffsetDateTime getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(
            OffsetDateTime startsAt) {
        this.startsAt = startsAt;
    }

    public OffsetDateTime getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(
            OffsetDateTime endsAt) {
        this.endsAt = endsAt;
    }
}