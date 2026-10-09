package com.propertypilot.application.dto;

import java.math.BigDecimal;

public record QuoteLineItemResponse(String lineType, String description, BigDecimal amount) { }
