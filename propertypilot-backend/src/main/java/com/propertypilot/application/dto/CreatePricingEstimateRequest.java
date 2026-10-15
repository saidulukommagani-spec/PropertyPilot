package com.propertypilot.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

public class CreatePricingEstimateRequest {

    private UUID customerId;

    @NotNull(
            message = "Property Id is required")
    private UUID propertyId;

    @NotNull(
            message = "Service Id is required")
    private UUID serviceId;

    @PositiveOrZero(message = "Distance cannot be negative")
    private Double oneWayDistanceKm;

    // Retained for old clients; the pricing engine ignores this untrusted value.
    private BigDecimal vendorCost;

    @PositiveOrZero(message = "Requested loan amount cannot be negative")
    private BigDecimal requestedLoanAmount;

    @Min(
            value = 0,
            message = "ETA days cannot be negative")
    private Integer etaDays;

    private String couponCode;

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

    public BigDecimal getRequestedLoanAmount() { return requestedLoanAmount; }
    public void setRequestedLoanAmount(BigDecimal requestedLoanAmount) { this.requestedLoanAmount = requestedLoanAmount; }

    public Integer getEtaDays() {
        return etaDays;
    }

    public void setEtaDays(
            Integer etaDays) {
        this.etaDays = etaDays;
    }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
}
