package com.propertypilot.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
public class UpdatePricingRuleRequest {

    private String ruleName;

    private BigDecimal baseAmount;

    private String currencyCode;

    private String ruleDefinition;

    private OffsetDateTime effectiveFrom;

    private OffsetDateTime effectiveTo;

    private String status;
}