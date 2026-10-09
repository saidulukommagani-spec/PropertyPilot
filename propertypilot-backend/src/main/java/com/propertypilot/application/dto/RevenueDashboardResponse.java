package com.propertypilot.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

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

    @Schema(description = "Legacy scalar total; null when invoice currencies differ. Use revenueByCurrency in that case.", example = "2500000.00", nullable = true)
    private BigDecimal totalRevenue;

    @Schema(description = "Legacy scalar collection total; null when invoice currencies differ. Use revenueByCurrency in that case.", example = "2200000.00", nullable = true)
    private BigDecimal collectedRevenue;

    @Schema(description = "Legacy scalar outstanding total; null when invoice currencies differ. Use revenueByCurrency in that case.", example = "300000.00", nullable = true)
    private BigDecimal outstandingRevenue;

    @Schema(description = "Legacy scalar service revenue; null when invoice currencies differ. Use revenueByCurrency in that case.", example = "1500000.00", nullable = true)
    private BigDecimal serviceRevenue;

    @Schema(description = "Legacy scalar subscription revenue; null when invoice currencies differ. Use revenueByCurrency in that case.", example = "700000.00", nullable = true)
    private BigDecimal subscriptionRevenue;

    @Schema(description = "Legacy scalar deal revenue; null when invoice currencies differ. Use revenueByCurrency in that case.", example = "300000.00", nullable = true)
    private BigDecimal dealRevenue;

    /** Currency-separated totals; financial amounts must not be added across currencies. */
    private List<InvoiceRevenueByCurrencyResponse> revenueByCurrency = List.of();

    public List<InvoiceRevenueByCurrencyResponse> getRevenueByCurrency() { return revenueByCurrency; }
    public void setRevenueByCurrency(List<InvoiceRevenueByCurrencyResponse> revenueByCurrency) {
        this.revenueByCurrency = revenueByCurrency == null ? List.of() : List.copyOf(revenueByCurrency);
    }

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
