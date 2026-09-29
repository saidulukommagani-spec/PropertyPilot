package com.propertypilot.application.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record LeadActivityResponse(

        UUID leadActivityId,

        UUID leadId,

        String activityType,

        String subject,

        String details,

        String outcome,

        String performedBy,

        OffsetDateTime occurredAt,

        OffsetDateTime nextActionAt

) {
}