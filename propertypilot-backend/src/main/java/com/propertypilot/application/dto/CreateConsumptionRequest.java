package com.propertypilot.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

@Schema(
        name = "CreateConsumptionRequest",
        description = "Request used to consume subscription entitlement"
)
public class CreateConsumptionRequest {

    @Schema(
            description = "Customer Subscription Id",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message =
            "Customer Subscription Id is required")
    private UUID customerSubscriptionId;

    @Schema(
            description = "Service Id",
            example = "550e8400-e29b-41d4-a716-446655440001",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message =
            "Service Id is required")
    private UUID serviceId;

    @Schema(
            description = "Service Request Id",
            example = "550e8400-e29b-41d4-a716-446655440002")
    private UUID serviceRequestId;

    @Schema(
            description = "Quantity to consume",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message =
            "Quantity is required")
    @Positive(message =
            "Quantity must be greater than zero")
    private Integer quantity;

    public UUID getCustomerSubscriptionId() {
        return customerSubscriptionId;
    }

    public void setCustomerSubscriptionId(
            UUID customerSubscriptionId) {
        this.customerSubscriptionId =
                customerSubscriptionId;
    }

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(
            UUID serviceId) {
        this.serviceId = serviceId;
    }

    public UUID getServiceRequestId() {
        return serviceRequestId;
    }

    public void setServiceRequestId(
            UUID serviceRequestId) {
        this.serviceRequestId =
                serviceRequestId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(
            Integer quantity) {
        this.quantity = quantity;
    }
}