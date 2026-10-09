package com.propertypilot.application.dto;

import java.math.BigDecimal;

public record ServiceRequestRevenueSummary(
        String currencyCode,
        BigDecimal invoicedRevenue,
        BigDecimal collectedRevenue) {
}
