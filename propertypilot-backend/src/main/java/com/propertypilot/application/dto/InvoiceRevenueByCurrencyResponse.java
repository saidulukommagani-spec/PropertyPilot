package com.propertypilot.application.dto;

import java.math.BigDecimal;

/** Financial totals for invoices denominated in one currency. */
public class InvoiceRevenueByCurrencyResponse {
    private String currencyCode;
    private BigDecimal totalInvoiceValue = BigDecimal.ZERO;
    private BigDecimal collectedAmount = BigDecimal.ZERO;
    private BigDecimal outstandingAmount = BigDecimal.ZERO;
    private BigDecimal dealRevenue = BigDecimal.ZERO;

    public InvoiceRevenueByCurrencyResponse() {}

    public InvoiceRevenueByCurrencyResponse(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    public BigDecimal getTotalInvoiceValue() { return totalInvoiceValue; }
    public void setTotalInvoiceValue(BigDecimal totalInvoiceValue) { this.totalInvoiceValue = totalInvoiceValue; }
    public BigDecimal getCollectedAmount() { return collectedAmount; }
    public void setCollectedAmount(BigDecimal collectedAmount) { this.collectedAmount = collectedAmount; }
    public BigDecimal getOutstandingAmount() { return outstandingAmount; }
    public void setOutstandingAmount(BigDecimal outstandingAmount) { this.outstandingAmount = outstandingAmount; }
    public BigDecimal getDealRevenue() { return dealRevenue; }
    public void setDealRevenue(BigDecimal dealRevenue) { this.dealRevenue = dealRevenue; }
}
