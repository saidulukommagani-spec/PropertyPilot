package com.propertypilot.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateServiceRequestRequest {

    private UUID customerId;

    private UUID propertyId;

    private UUID serviceId;

    private String requestType;

    private String priority;

    private BigDecimal amount;

    private String description;

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(
            UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(
            UUID propertyId) {
        this.propertyId = propertyId;
    }

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(
            UUID serviceId) {
        this.serviceId = serviceId;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(
            String requestType) {
        this.requestType = requestType;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(
            String priority) {
        this.priority = priority;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(
            BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {
        this.description = description;
    }
}