package com.propertypilot.application.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MonitoringRunResponse(
        UUID monitoringRunId,
        UUID scheduleId,
        Integer runNumber,
        String cycleKey,
        UUID serviceRequestId,
        OffsetDateTime scheduledFor,
        OffsetDateTime startedAt,
        String status) {
}
