package com.propertypilot.application.dto;

import java.math.BigDecimal;
import java.util.UUID;
import java.time.OffsetDateTime;
import java.util.List;

public class PricingEstimateResponse {

    private UUID pricingEstimateId;

    private UUID customerId;

    private UUID serviceId;
    private UUID propertyId;

    private String serviceName;

    private BigDecimal subtotal;

    private BigDecimal discountAmount;

    private BigDecimal taxAmount;

    private BigDecimal totalAmount;

    private String currencyCode;

    private String status;
    private OffsetDateTime expiresAt;
    private BigDecimal standardPrice;
    private BigDecimal customerPrice;
    private BigDecimal savings;
    private PricingBreakdownResponse standardBreakdown;
    private PricingBreakdownResponse customerBreakdown;
    private List<PricingEstimateLineResponse> lineItems;
    private List<PricingDiscountResponse> discounts;

    public UUID getPricingEstimateId() {
        return pricingEstimateId;
    }

    public void setPricingEstimateId(UUID pricingEstimateId) {
        this.pricingEstimateId = pricingEstimateId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getServiceId() {
        return serviceId;
    }

    public void setServiceId(UUID serviceId) {
        this.serviceId = serviceId;
    }

    public UUID getPropertyId() { return propertyId; }
    public void setPropertyId(UUID propertyId) { this.propertyId = propertyId; }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OffsetDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(OffsetDateTime expiresAt) { this.expiresAt = expiresAt; }
    public BigDecimal getStandardPrice() { return standardPrice; }
    public void setStandardPrice(BigDecimal standardPrice) { this.standardPrice = standardPrice; }
    public BigDecimal getCustomerPrice() { return customerPrice; }
    public void setCustomerPrice(BigDecimal customerPrice) { this.customerPrice = customerPrice; }
    public BigDecimal getSavings() { return savings; }
    public void setSavings(BigDecimal savings) { this.savings = savings; }
    public PricingBreakdownResponse getStandardBreakdown() { return standardBreakdown; }
    public void setStandardBreakdown(PricingBreakdownResponse value) { standardBreakdown = value; }
    public PricingBreakdownResponse getCustomerBreakdown() { return customerBreakdown; }
    public void setCustomerBreakdown(PricingBreakdownResponse value) { customerBreakdown = value; }
    public List<PricingEstimateLineResponse> getLineItems() { return lineItems; }
    public void setLineItems(List<PricingEstimateLineResponse> value) { lineItems = value; }
    public List<PricingDiscountResponse> getDiscounts() { return discounts; }
    public void setDiscounts(List<PricingDiscountResponse> value) { discounts = value; }
}
