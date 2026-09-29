package com.propertypilot.application.dto;

import java.math.BigDecimal;

public class PricingCalculationResponse {

    private BigDecimal standardPrice;

    private BigDecimal customerPrice;

    private BigDecimal savings;

    private String savingsMessage;

    private PricingBreakdownResponse breakdown;

    public BigDecimal getStandardPrice() {
        return standardPrice;
    }

    public void setStandardPrice(
            BigDecimal standardPrice) {
        this.standardPrice = standardPrice;
    }

    public BigDecimal getCustomerPrice() {
        return customerPrice;
    }

    public void setCustomerPrice(
            BigDecimal customerPrice) {
        this.customerPrice = customerPrice;
    }

    public BigDecimal getSavings() {
        return savings;
    }

    public void setSavings(
            BigDecimal savings) {
        this.savings = savings;
    }

    public String getSavingsMessage() {
        return savingsMessage;
    }

    public void setSavingsMessage(
            String savingsMessage) {
        this.savingsMessage = savingsMessage;
    }

    public PricingBreakdownResponse getBreakdown() {
        return breakdown;
    }

    public void setBreakdown(
            PricingBreakdownResponse breakdown) {
        this.breakdown = breakdown;
    }

private PricingBreakdownResponse standardBreakdown;

private PricingBreakdownResponse customerBreakdown;

public PricingBreakdownResponse getStandardBreakdown() {
    return standardBreakdown;
}

public void setStandardBreakdown(PricingBreakdownResponse standardBreakdown) {
    this.standardBreakdown = standardBreakdown;
}

public PricingBreakdownResponse getCustomerBreakdown() {
    return customerBreakdown;
}

public void setCustomerBreakdown(PricingBreakdownResponse customerBreakdown) {
    this.customerBreakdown = customerBreakdown;
}
}