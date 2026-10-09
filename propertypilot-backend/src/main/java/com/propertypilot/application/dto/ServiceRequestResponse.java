package com.propertypilot.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.List;

public class ServiceRequestResponse {

    private UUID serviceRequestId;

    private UUID customerId;

    private UUID propertyId;

    private UUID serviceId;

    private UUID quoteId;

    private UUID leadId;

    private String requestType;

    private String priority;

    private String status;

    private BigDecimal amount;

    private String description;

    private Instant requestedAt;

    private Instant scheduledAt;

    private Instant completedAt;

    private UUID assignedTo;
    private UUID assignedAgentId;
    private String customerName;
    private String propertyTitle;
    private String serviceName;
    private List<ServiceRequestStatusHistoryResponse> statusHistory;
    private String assignedToName;
    private String assignedAgentPhone;
    private String assignedAgentCode;
    private String assignedByName;
    private UUID assignedByUserId;
    private Instant assignedAt;
    private String assignmentNotes;

    private String assignedAgentName;

    private Instant slaDueAt;

    private String slaStatus;

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

    public UUID getQuoteId() { return quoteId; }
    public void setQuoteId(UUID quoteId) { this.quoteId = quoteId; }
    public UUID getLeadId() { return leadId; }
    public void setLeadId(UUID leadId) { this.leadId = leadId; }
    public String getAssignedAgentName() { return assignedAgentName; }
    public void setAssignedAgentName(String assignedAgentName) { this.assignedAgentName = assignedAgentName; }
    public Instant getSlaDueAt() { return slaDueAt; }
    public void setSlaDueAt(Instant slaDueAt) { this.slaDueAt = slaDueAt; }
    public String getSlaStatus() { return slaStatus; }
    public void setSlaStatus(String slaStatus) { this.slaStatus = slaStatus; }

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

    public UUID getAssignedAgentId() { return assignedAgentId; }
    public void setAssignedAgentId(UUID assignedAgentId) { this.assignedAgentId = assignedAgentId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getPropertyTitle() { return propertyTitle; }
    public void setPropertyTitle(String propertyTitle) { this.propertyTitle = propertyTitle; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public List<ServiceRequestStatusHistoryResponse> getStatusHistory() { return statusHistory; }
    public void setStatusHistory(List<ServiceRequestStatusHistoryResponse> statusHistory) { this.statusHistory = statusHistory; }
    public String getAssignedToName() { return assignedToName; }
    public void setAssignedToName(String assignedToName) { this.assignedToName = assignedToName; }
    public String getAssignedAgentPhone() { return assignedAgentPhone; }
    public void setAssignedAgentPhone(String assignedAgentPhone) { this.assignedAgentPhone = assignedAgentPhone; }
    public String getAssignedAgentCode() { return assignedAgentCode; }
    public void setAssignedAgentCode(String assignedAgentCode) { this.assignedAgentCode = assignedAgentCode; }
    public String getAssignedByName() { return assignedByName; }
    public void setAssignedByName(String assignedByName) { this.assignedByName = assignedByName; }
    public UUID getAssignedByUserId() { return assignedByUserId; }
    public void setAssignedByUserId(UUID assignedByUserId) { this.assignedByUserId = assignedByUserId; }
    public Instant getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Instant assignedAt) { this.assignedAt = assignedAt; }
    public String getAssignmentNotes() { return assignmentNotes; }
    public void setAssignmentNotes(String assignmentNotes) { this.assignmentNotes = assignmentNotes; }
}
