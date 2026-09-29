package com.propertypilot.application.dto;

public class CustomerDashboardResponse {

    private long totalProperties;

    private long totalServiceRequests;

    private long activeServiceRequests;

    private long completedServiceRequests;

    private long activeSubscriptions;

    private long pendingInvoices;

    private long paidInvoices;

    private long totalPayments;

    public long getTotalProperties() {
        return totalProperties;
    }

    public void setTotalProperties(
            long totalProperties) {
        this.totalProperties = totalProperties;
    }

    public long getTotalServiceRequests() {
        return totalServiceRequests;
    }

    public void setTotalServiceRequests(
            long totalServiceRequests) {
        this.totalServiceRequests = totalServiceRequests;
    }

    public long getActiveServiceRequests() {
        return activeServiceRequests;
    }

    public void setActiveServiceRequests(
            long activeServiceRequests) {
        this.activeServiceRequests = activeServiceRequests;
    }

    public long getCompletedServiceRequests() {
        return completedServiceRequests;
    }

    public void setCompletedServiceRequests(
            long completedServiceRequests) {
        this.completedServiceRequests =
                completedServiceRequests;
    }

    public long getActiveSubscriptions() {
        return activeSubscriptions;
    }

    public void setActiveSubscriptions(
            long activeSubscriptions) {
        this.activeSubscriptions = activeSubscriptions;
    }

    public long getPendingInvoices() {
        return pendingInvoices;
    }

    public void setPendingInvoices(
            long pendingInvoices) {
        this.pendingInvoices = pendingInvoices;
    }

    public long getPaidInvoices() {
        return paidInvoices;
    }

    public void setPaidInvoices(
            long paidInvoices) {
        this.paidInvoices = paidInvoices;
    }

    public long getTotalPayments() {
        return totalPayments;
    }

    public void setTotalPayments(
            long totalPayments) {
        this.totalPayments = totalPayments;
    }
}