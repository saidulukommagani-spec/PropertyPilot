package com.propertypilot.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
public class PricingRuleResponse {

    private UUID priceRuleId;

    private UUID serviceId;

    private String serviceName;

    private String ruleName;

    private BigDecimal baseAmount;

    private String currencyCode;

    private String ruleDefinition;

    private OffsetDateTime effectiveFrom;

    private OffsetDateTime effectiveTo;

    private String status;

    private Long version;
}