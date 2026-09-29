package com.propertypilot.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class CreatePropertyOwnerRequest {

    @NotNull
    private UUID propertyId;

    private UUID customerId;

    @NotBlank
    private String ownerName;

    @NotNull
    private BigDecimal ownershipPercentage;

    @NotBlank
    private String ownershipType;

    @NotNull
    private LocalDate validFrom;

    private LocalDate validTo;

    @NotNull
    private Boolean isPrimary;

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