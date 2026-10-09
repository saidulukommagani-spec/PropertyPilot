package com.propertypilot.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

/** Commercial information required to create a deal from a qualified lead. */
public record LeadDealConversionRequest(
        UUID propertyId,
        @NotBlank String dealType,
        @NotNull @DecimalMin(value = "0.01") @Digits(integer = 15, fraction = 2) BigDecimal expectedAmount,
        @DecimalMin("0") @Digits(integer = 3, fraction = 2) BigDecimal sellerCommissionPercent,
        @DecimalMin("0") @Digits(integer = 3, fraction = 2) BigDecimal buyerCommissionPercent,
        @Size(max = 2000) String remarks,
        @Size(max = 1000) String duplicateOpportunityReason) {
}
