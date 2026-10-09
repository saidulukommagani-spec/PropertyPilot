package com.propertypilot.application.dto;

import java.time.Instant;
import java.util.UUID;

public record SlaReportRow(
        UUID serviceRequestId,
        String serviceName,
        String requestStatus,
        String slaState,
        Instant acknowledgementDueAt,
        Instant completionDueAt,
        long minutesOverdue) {
}
