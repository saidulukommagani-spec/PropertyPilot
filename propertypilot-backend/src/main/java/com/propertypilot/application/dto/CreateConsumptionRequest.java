package com.propertypilot.application.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateConsumptionRequest {

   @NotNull
    private UUID customerSubscriptionId;

    @NotNull
    private UUID serviceId;

    private UUID serviceRequestId;

    @Positive
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