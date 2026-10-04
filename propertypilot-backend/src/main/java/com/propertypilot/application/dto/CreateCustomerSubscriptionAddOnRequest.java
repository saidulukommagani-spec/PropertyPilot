package com.propertypilot.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(
        description = "Request to create a customer subscription add-on")
public class CreateCustomerSubscriptionAddOnRequest {

    @NotNull
    @Schema(
            description = "Subscription add-on identifier",
            example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID subscriptionAddOnId;

    @Schema(
            description = "Pricing estimate identifier")
    private UUID pricingEstimateId;

    @NotNull
    @Min(1)
    @Schema(
            description = "Quantity of add-ons",
            example = "1")
    private Integer quantity;

    @Schema(
            description = "Add-on start date")
    private OffsetDateTime startsAt;

    @Schema(
            description = "Add-on end date")
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