package com.propertypilot.application.dto;

import java.math.BigDecimal;

public class RevenueDashboardResponse {

    private long totalInvoices;

    private long paidInvoices;

    private long pendingInvoices;

    private BigDecimal totalRevenue;

    private BigDecimal collectedRevenue;

    private BigDecimal outstandingRevenue;

    private BigDecimal serviceRevenue;

    private BigDecimal subscriptionRevenue;

    private BigDecimal dealRevenue;

    public long getTotalInvoices() {
        return totalInvoices;
    }

    public void setTotalInvoices(
            long totalInvoices) {
        this.totalInvoices = totalInvoices;
    }

    public long getPaidInvoices() {
        return paidInvoices;
    }

    public void setPaidInvoices(
            long paidInvoices) {
        this.paidInvoices = paidInvoices;
    }

    public long getPendingInvoices() {
        return pendingInvoices;
    }

    public void setPendingInvoices(
            long pendingInvoices) {
        this.pendingInvoices = pendingInvoices;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(
            BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getCollectedRevenue() {
        return collectedRevenue;
    }

    public void setCollectedRevenue(
            BigDecimal collectedRevenue) {
        this.collectedRevenue = collectedRevenue;
    }

    public BigDecimal getOutstandingRevenue() {
        return outstandingRevenue;
    }

    public void setOutstandingRevenue(
            BigDecimal outstandingRevenue) {
        this.outstandingRevenue = outstandingRevenue;
    }

    public BigDecimal getServiceRevenue() {
        return serviceRevenue;
    }

    public void setServiceRevenue(
            BigDecimal serviceRevenue) {
        this.serviceRevenue = serviceRevenue;
    }

    public BigDecimal getSubscriptionRevenue() {
        return subscriptionRevenue;
    }

    public void setSubscriptionRevenue(
            BigDecimal subscriptionRevenue) {
        this.subscriptionRevenue = subscriptionRevenue;
    }

    public BigDecimal getDealRevenue() {
        return dealRevenue;
    }

    public void setDealRevenue(
            BigDecimal dealRevenue) {
        this.dealRevenue = dealRevenue;
    }
}