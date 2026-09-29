package com.propertypilot.application.dto;

import java.time.LocalDate;
import java.util.UUID;

public class ConsumptionResponse {

    private UUID entitlementConsumptionId;

    private UUID customerSubscriptionId;

    private UUID serviceId;

    private Integer entitledQuantity;

    private Integer consumedQuantity;

    private Integer remainingQuantity;

    private LocalDate periodStart;

    private LocalDate periodEnd;

    public UUID getEntitlementConsumptionId() {
        return entitlementConsumptionId;
    }

    public void setEntitlementConsumptionId(
            UUID entitlementConsumptionId) {
        this.entitlementConsumptionId =
                entitlementConsumptionId;
    }

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

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(
            LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(
            LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }
}