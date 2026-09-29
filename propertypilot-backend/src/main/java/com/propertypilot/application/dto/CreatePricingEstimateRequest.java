package com.propertypilot.application.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class CreatePricingEstimateRequest {

    @NotNull
    private UUID customerId;

    @NotNull
    private UUID propertyId;

    @NotNull
    private UUID serviceId;

    @NotNull
    private Double oneWayDistanceKm;

    @NotNull
    private BigDecimal vendorCost;

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

/*
 * TODO:
 * Vendor cost should eventually be derived from
 * Service Pricing Catalog based on:
 * - Service
 * - Property Type
 * - Property Size
 * - Locality
 */
}