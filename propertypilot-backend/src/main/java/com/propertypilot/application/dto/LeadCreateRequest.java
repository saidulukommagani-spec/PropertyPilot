package com.propertypilot.application.dto;

import java.math.BigDecimal;

public record LeadCreateRequest(
        String source,
        BigDecimal budgetMin,
        BigDecimal budgetMax,
        String notes
) {
}