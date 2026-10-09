package com.propertypilot.application.dto;

import java.time.Instant;
import java.util.UUID;

public record AuditReportRow(
        UUID statusHistoryId,
        UUID serviceRequestId,
        UUID propertyId,
        String previousStatus,
        String newStatus,
        String reason,
        UUID actorUserId,
        Instant occurredAt) {
}
