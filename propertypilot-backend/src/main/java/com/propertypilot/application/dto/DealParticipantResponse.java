package com.propertypilot.application.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class DealParticipantResponse {

    private UUID participantId;

    private UUID dealId;

    private UUID customerId;

    private String customerName;

    private String participantRole;

    private String participantStatus;

    private String contactVisibility;

    private BigDecimal offeredAmount;

    private OffsetDateTime offerDate;

    private Boolean selectedFlag;

    private String rejectedReason;

    private String remarks;

    public UUID getParticipantId() {
        return participantId;
    }

    public void setParticipantId(
            UUID participantId) {
        this.participantId = participantId;
    }

    public UUID getDealId() {
        return dealId;
    }

    public void setDealId(
            UUID dealId) {
        this.dealId = dealId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(
            UUID customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(
            String customerName) {
        this.customerName = customerName;
    }

    public String getParticipantRole() {
        return participantRole;
    }

    public void setParticipantRole(
            String participantRole) {
        this.participantRole = participantRole;
    }

    public String getParticipantStatus() {
        return participantStatus;
    }

    public void setParticipantStatus(
            String participantStatus) {
        this.participantStatus = participantStatus;
    }

    public String getContactVisibility() {
        return contactVisibility;
    }

    public void setContactVisibility(
            String contactVisibility) {
        this.contactVisibility = contactVisibility;
    }

    public BigDecimal getOfferedAmount() {
        return offeredAmount;
    }

    public void setOfferedAmount(
            BigDecimal offeredAmount) {
        this.offeredAmount = offeredAmount;
    }

    public OffsetDateTime getOfferDate() {
        return offerDate;
    }

    public void setOfferDate(
            OffsetDateTime offerDate) {
        this.offerDate = offerDate;
    }

    public Boolean getSelectedFlag() {
        return selectedFlag;
    }

    public void setSelectedFlag(
            Boolean selectedFlag) {
        this.selectedFlag = selectedFlag;
    }

    public String getRejectedReason() {
        return rejectedReason;
    }

    public void setRejectedReason(
            String rejectedReason) {
        this.rejectedReason = rejectedReason;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(
            String remarks) {
        this.remarks = remarks;
    }
}