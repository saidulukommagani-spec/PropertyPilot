package com.propertypilot.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Analytics Dashboard Response"
)
public class AnalyticsDashboardResponse {

    @Schema(
            description = "Total Customers"
    )
    private long totalCustomers;

    @Schema(
            description = "Total Properties"
    )
    private long totalProperties;

    @Schema(
            description = "Total Leads"
    )
    private long totalLeads;

    @Schema(
            description = "Total Deals"
    )
    private long totalDeals;

    @Schema(
            description = "Total Service Requests"
    )
    private long totalServiceRequests;

    @Schema(
            description = "Active Subscriptions"
    )
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
        this.totalServiceRequests =
                totalServiceRequests;
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