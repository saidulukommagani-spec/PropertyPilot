package com.propertypilot.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record LeadResponse(
        UUID leadId,
        String source,
        String status,
        BigDecimal budgetMin,
        BigDecimal budgetMax,
        String notes
) {
}