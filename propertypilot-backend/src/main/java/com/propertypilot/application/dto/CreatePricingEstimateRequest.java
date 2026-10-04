package com.propertypilot.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public class CreatePricingEstimateRequest {

    @NotNull(
            message = "Customer Id is required")
    private UUID customerId;

    @NotNull(
            message = "Property Id is required")
    private UUID propertyId;

    @NotNull(
            message = "Service Id is required")
    private UUID serviceId;

    @NotNull(
            message = "One way distance is required")
    @Positive(
            message = "Distance must be greater than zero")
    private Double oneWayDistanceKm;

    @NotNull(
            message = "Vendor cost is required")
    @Positive(
            message = "Vendor cost must be greater than zero")
    private BigDecimal vendorCost;

    @Min(
            value = 0,
            message = "ETA days cannot be negative")
    private Integer etaDays;

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(UUID propertyId) {
        this.propertyId = propertyId;
    }

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }

    public Double getOneWayDistanceKm() {
        return oneWayDistanceKm;
    }

    public void setOneWayDistanceKm(
            Double oneWayDistanceKm) {
        this.oneWayDistanceKm = oneWayDistanceKm;
    }

    public BigDecimal getVendorCost() {
        return vendorCost;
    }

    public void setVendorCost(
            BigDecimal vendorCost) {
        this.vendorCost = vendorCost;
    }

    public Integer getEtaDays() {
        return etaDays;
    }

    public void setEtaDays(
            Integer etaDays) {
        this.etaDays = etaDays;
    }
}