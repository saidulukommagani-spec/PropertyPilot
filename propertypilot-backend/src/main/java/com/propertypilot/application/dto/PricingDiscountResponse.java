package com.propertypilot.application.dto;

import java.math.BigDecimal;

public record PricingDiscountResponse(String discountType, String description, BigDecimal amount) {
}
