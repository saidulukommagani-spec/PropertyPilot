package com.propertypilot.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class PropertyOwnerResponse {

    private UUID propertyOwnerId;

    private UUID propertyId;

    private UUID customerId;

    private String ownerName;

    private BigDecimal ownershipPercentage;

    private String ownershipType;

    private LocalDate validFrom;

    private LocalDate validTo;

    private Boolean isPrimary;

    public UUID getPropertyOwnerId() {
        return propertyOwnerId;
    }

    public void setPropertyOwnerId(
            UUID propertyOwnerId) {
        this.propertyOwnerId =
                propertyOwnerId;
    }

    public UUID getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(
            UUID propertyId) {
        this.propertyId = propertyId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(
            UUID customerId) {
        this.customerId = customerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(
            String ownerName) {
        this.ownerName = ownerName;
    }

    public BigDecimal getOwnershipPercentage() {
        return ownershipPercentage;
    }

    public void setOwnershipPercentage(
            BigDecimal ownershipPercentage) {
        this.ownershipPercentage =
                ownershipPercentage;
    }

    public String getOwnershipType() {
        return ownershipType;
    }

    public void setOwnershipType(
            String ownershipType) {
        this.ownershipType = ownershipType;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(
            LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public void setValidTo(
            LocalDate validTo) {
        this.validTo = validTo;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(
            Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }
}