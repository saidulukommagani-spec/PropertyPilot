package com.propertypilot.application.dto;

public class AnalyticsDashboardResponse {

    private long totalCustomers;

    private long totalProperties;

    private long totalLeads;

    private long totalDeals;

    private long totalServiceRequests;

    private long activeSubscriptions;

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(
            long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public long getTotalProperties() {
        return totalProperties;
    }

    public void setTotalProperties(
            long totalProperties) {
        this.totalProperties = totalProperties;
    }

    public long getTotalLeads() {
        return totalLeads;
    }

    public void setTotalLeads(
            long totalLeads) {
        this.totalLeads = totalLeads;
    }

    public long getTotalDeals() {
        return totalDeals;
    }

    public void setTotalDeals(
            long totalDeals) {
        this.totalDeals = totalDeals;
    }

    public long getTotalServiceRequests() {
        return totalServiceRequests;
    }

    public void setTotalServiceRequests(
            long totalServiceRequests) {
        this.totalServiceRequests = totalServiceRequests;
    }

    public long getActiveSubscriptions() {
        return activeSubscriptions;
    }

    public void setActiveSubscriptions(
            long activeSubscriptions) {
        this.activeSubscriptions =
                activeSubscriptions;
    }
}