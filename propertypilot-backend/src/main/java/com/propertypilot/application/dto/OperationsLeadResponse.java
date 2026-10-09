package com.propertypilot.application.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Safe, human-readable lead row for the secured Operations lead queue. */
public record OperationsLeadResponse(
        UUID leadId,
        String source,
        String propertyType,
        String location,
        String status,
        String customerName,
        String customerEmail,
        String customerPhone,
        String assignedAgentCode,
        OffsetDateTime createdAt) {
}
