package com.propertypilot.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.List;

public record LeadServiceRequestResponse(
        UUID serviceRequestId,
        UUID leadId,
        UUID propertyId,
        UUID serviceId,
        UUID quoteId,
        String serviceName,
        String status,
        String priority,
        String assignedAgent,
        Instant requestedAt,
        Instant scheduledAt,
        Instant completedAt,
        Instant slaDueAt,
        String slaStatus,
        long visitCount,
        long evidenceCount,
        BigDecimal requestValue,
        String currencyCode,
        List<ServiceRequestRevenueSummary> revenueByCurrency,
        List<ServiceRequestStatusHistoryResponse> statusHistory,
        List<ServiceRequestAssignmentHistoryResponse> assignmentHistory) {
}
