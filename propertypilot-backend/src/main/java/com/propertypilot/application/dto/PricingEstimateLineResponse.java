package com.propertypilot.application.dto;

import java.math.BigDecimal;

public record PricingEstimateLineResponse(String lineType, String description,
                                          BigDecimal quantity, BigDecimal unitAmount,
                                          BigDecimal lineAmount) {
}
