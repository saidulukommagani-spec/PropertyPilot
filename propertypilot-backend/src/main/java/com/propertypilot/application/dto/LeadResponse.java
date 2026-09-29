package com.propertypilot.application.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record LeadResponse(

        UUID leadId,

        String source,

        String propertyType,

        String location,

        String status,

        BigDecimal budgetMin,

        BigDecimal budgetMax,

        String notes,

        OffsetDateTime contactedAt,

        OffsetDateTime convertedAt,

        UUID assignedAgentId,

        String assignedAgentCode
        

) {
}