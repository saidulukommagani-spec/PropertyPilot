package com.propertypilot.application.dto;

import java.math.BigDecimal;

public class PricingBreakdownResponse {

    private BigDecimal vendorCost;

    private BigDecimal travelCost;

    private BigDecimal agentDa;

    private BigDecimal overheadAmount;

    private BigDecimal marginAmount;

    private BigDecimal clusterDiscount;

    private BigDecimal etaDiscount;

    private BigDecimal subtotal;

    private BigDecimal gstAmount;

    private BigDecimal totalAmount;

    public BigDecimal getVendorCost() {
        return vendorCost;
    }

    public void setVendorCost(
            BigDecimal vendorCost) {
        this.vendorCost = vendorCost;
    }

    public BigDecimal getTravelCost() {
        return travelCost;
    }

    public void setTravelCost(
            BigDecimal travelCost) {
        this.travelCost = travelCost;
    }

    public BigDecimal getAgentDa() {
        return agentDa;
    }

    public void setAgentDa(
            BigDecimal agentDa) {
        this.agentDa = agentDa;
    }

    public BigDecimal getOverheadAmount() {
        return overheadAmount;
    }

    public void setOverheadAmount(
            BigDecimal overheadAmount) {
        this.overheadAmount = overheadAmount;
    }

    public BigDecimal getMarginAmount() {
        return marginAmount;
    }

    public void setMarginAmount(
            BigDecimal marginAmount) {
        this.marginAmount = marginAmount;
    }

    public BigDecimal getClusterDiscount() {
        return clusterDiscount;
    }

    public void setClusterDiscount(
            BigDecimal clusterDiscount) {
        this.clusterDiscount = clusterDiscount;
    }

    public BigDecimal getEtaDiscount() {
        return etaDiscount;
    }

    public void setEtaDiscount(
            BigDecimal etaDiscount) {
        this.etaDiscount = etaDiscount;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(
            BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getGstAmount() {
        return gstAmount;
    }

    public void setGstAmount(
            BigDecimal gstAmount) {
        this.gstAmount = gstAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(
            BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}