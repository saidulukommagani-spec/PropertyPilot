package com.propertypilot.application.dto;

import java.math.BigDecimal;

public record PricingEstimateRequirementsResponse(
        String pricingModel,
        String amountBasis,
        BigDecimal ratePercent,
        BigDecimal minimumFee,
        BigDecimal maximumFee) {
}
