package com.propertypilot.application.dto;

import java.time.OffsetDateTime;

public record LeadActivityCreateRequest(

        String activityType,

        String subject,

        String details,

        String outcome,

        OffsetDateTime nextActionAt

) {
}