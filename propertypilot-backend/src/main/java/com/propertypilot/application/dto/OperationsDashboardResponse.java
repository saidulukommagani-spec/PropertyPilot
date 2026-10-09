package com.propertypilot.application.dto;

public record OperationsDashboardResponse(
        long totalRequests,
        long unassignedRequests,
        long activeRequests,
        long completedRequests,
        long failedRequests,
        long overdueInvoices,
        long failedPayments) {
}
