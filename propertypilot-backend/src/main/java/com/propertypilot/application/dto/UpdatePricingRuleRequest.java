package com.propertypilot.application.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
public class UpdatePricingRuleRequest {

    @Size(min = 1, max = 150)
    private String ruleName;

    @DecimalMin(value = "0.01")
    private BigDecimal baseAmount;

    @Pattern(regexp = "[A-Za-z]{3}")
    private String currencyCode;

    private String ruleDefinition;

    private OffsetDateTime effectiveFrom;

    private OffsetDateTime effectiveTo;

    private String status;
}
