package com.propertypilot.application.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MonitoringScheduleResponse(
        UUID scheduleId,
        UUID customerSubscriptionId,
        UUID sourceServiceRequestId,
        UUID propertyId,
        UUID serviceId,
        String frequency,
        OffsetDateTime nextDueAt,
        OffsetDateTime lastGeneratedAt,
        String status) {
}
