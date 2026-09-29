package com.propertypilot.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class ServiceRequestResponse {

    private UUID serviceRequestId;

    private UUID customerId;

    private UUID propertyId;

    private UUID serviceId;

    private String requestType;

    private String priority;

    private String status;

    private BigDecimal amount;

    private String description;

    private Instant requestedAt;

    private Instant scheduledAt;

    private Instant completedAt;

    private UUID assignedTo;

    public UUID getServiceRequestId() {
        return serviceRequestId;
    }

    public void setServiceRequestId(
            UUID serviceRequestId) {
        this.serviceRequestId =
                serviceRequestId;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status) {
        this.status = status;
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

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(
            Instant requestedAt) {
        this.requestedAt = requestedAt;
    }

    public Instant getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(
            Instant scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(
            Instant completedAt) {
        this.completedAt = completedAt;
    }

    public UUID getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(
            UUID assignedTo) {
        this.assignedTo = assignedTo;
    }
}