package com.propertypilot.application.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class CreateCustomerSubscriptionAddOnRequest {

    private UUID subscriptionAddOnId;

    private UUID pricingEstimateId;

    private Integer quantity;

    private OffsetDateTime startsAt;

    private OffsetDateTime endsAt;

    public UUID getSubscriptionAddOnId() {
        return subscriptionAddOnId;
    }

    public void setSubscriptionAddOnId(
            UUID subscriptionAddOnId) {
        this.subscriptionAddOnId =
                subscriptionAddOnId;
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