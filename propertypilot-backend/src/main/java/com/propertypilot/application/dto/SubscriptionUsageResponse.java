package com.propertypilot.application.dto;

import java.util.UUID;

public class SubscriptionUsageResponse {

    private UUID serviceId;

    private Integer entitledQuantity;

    private Integer consumedQuantity;

    private Integer remainingQuantity;

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(
            UUID serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getEntitledQuantity() {
        return entitledQuantity;
    }

    public void setEntitledQuantity(
            Integer entitledQuantity) {
        this.entitledQuantity =
                entitledQuantity;
    }

    public Integer getConsumedQuantity() {
        return consumedQuantity;
    }

    public void setConsumedQuantity(
            Integer consumedQuantity) {
        this.consumedQuantity =
                consumedQuantity;
    }

    public Integer getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(
            Integer remainingQuantity) {
        this.remainingQuantity =
                remainingQuantity;
    }
}