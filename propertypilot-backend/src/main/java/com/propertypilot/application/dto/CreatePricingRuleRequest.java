package com.propertypilot.application.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreatePricingRuleRequest {

    @NotNull
    private UUID serviceId;

    @NotBlank
    @Size(max = 150)
    private String ruleName;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal baseAmount;

    @NotBlank
    @Size(min = 3, max = 3)
    private String currencyCode;

    @NotBlank
    private String ruleDefinition;

    @NotNull
    private OffsetDateTime effectiveFrom;

    private OffsetDateTime effectiveTo;

    @NotBlank
    private String status;
}