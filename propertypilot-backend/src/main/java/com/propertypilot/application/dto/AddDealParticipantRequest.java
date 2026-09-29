package com.propertypilot.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class AddDealParticipantRequest {

    private UUID customerId;

    private String participantRole;

    private String contactVisibility;

    private BigDecimal offeredAmount;

    private String remarks;

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(
            UUID customerId) {
        this.customerId = customerId;
    }

    public String getParticipantRole() {
        return participantRole;
    }

    public void setParticipantRole(
            String participantRole) {
        this.participantRole =
                participantRole;
    }

    public String getContactVisibility() {
        return contactVisibility;
    }

    public void setContactVisibility(
            String contactVisibility) {
        this.contactVisibility =
                contactVisibility;
    }

    public BigDecimal getOfferedAmount() {
        return offeredAmount;
    }

    public void setOfferedAmount(
            BigDecimal offeredAmount) {
        this.offeredAmount =
                offeredAmount;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(
            String remarks) {
        this.remarks = remarks;
    }
}