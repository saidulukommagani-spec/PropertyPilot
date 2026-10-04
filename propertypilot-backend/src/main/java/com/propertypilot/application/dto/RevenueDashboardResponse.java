package com.propertypilot.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(
        description =
                "Revenue dashboard analytics response"
)
public class RevenueDashboardResponse {

    @Schema(example = "250")
    private long totalInvoices;

    @Schema(example = "200")
    private long paidInvoices;

    @Schema(example = "50")
    private long pendingInvoices;

    @Schema(example = "2500000.00")
    private BigDecimal totalRevenue;

    @Schema(example = "2200000.00")
    private BigDecimal collectedRevenue;

    @Schema(example = "300000.00")
    private BigDecimal outstandingRevenue;

    @Schema(example = "1500000.00")
    private BigDecimal serviceRevenue;

    @Schema(example = "700000.00")
    private BigDecimal subscriptionRevenue;

    @Schema(example = "300000.00")
    private BigDecimal dealRevenue;

    public long getTotalInvoices() {
        return totalInvoices;
    }

    public void setTotalInvoices(long totalInvoices) {
        this.totalInvoices = totalInvoices;
    }

    public long getPaidInvoices() {
        return paidInvoices;
    }

    public void setPaidInvoices(long paidInvoices) {
        this.paidInvoices = paidInvoices;
    }

    public long getPendingInvoices() {
        return pendingInvoices;
    }

    public void setPendingInvoices(long pendingInvoices) {
        this.pendingInvoices = pendingInvoices;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
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
        this.outstandingRevenue =
                outstandingRevenue;
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
        this.subscriptionRevenue =
                subscriptionRevenue;
    }

    public BigDecimal getDealRevenue() {
        return dealRevenue;
    }

    public void setDealRevenue(
            BigDecimal dealRevenue) {
        this.dealRevenue = dealRevenue;
    }
}