package com.propertypilot.application.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

/** Typed configuration for the supported service pricing models. */
record ServicePricingRuleDefinition(
        String pricingModel,
        String amountBasis,
        BigDecimal minimumFee,
        BigDecimal maximumFee) {

    static final String FIXED = "FIXED";
    static final String PERCENT_OF_LOAN_AMOUNT = "PERCENT_OF_LOAN_AMOUNT";
    static final String REQUESTED_LOAN_AMOUNT = "REQUESTED_LOAN_AMOUNT";
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    static ServicePricingRuleDefinition parse(String definition) {
        try {
            JsonNode root = JSON.readTree(definition == null || definition.isBlank() ? "{}" : definition);
            String model = root.path("pricingModel").asText(FIXED).trim().toUpperCase();
            if (FIXED.equals(model)) {
                return new ServicePricingRuleDefinition(FIXED, null, ZERO, null);
            }
            if (!PERCENT_OF_LOAN_AMOUNT.equals(model)) {
                throw new IllegalArgumentException("Unsupported pricing model: " + model);
            }
            String basis = root.path("amountBasis").asText(REQUESTED_LOAN_AMOUNT).trim().toUpperCase();
            if (!REQUESTED_LOAN_AMOUNT.equals(basis)) {
                throw new IllegalArgumentException("Loan pricing must use the requested loan amount basis");
            }
            BigDecimal minimum = decimal(root, "minimumFee", ZERO);
            BigDecimal maximum = root.path("maximumFee").isNumber()
                    ? root.path("maximumFee").decimalValue() : null;
            if (minimum.signum() < 0 || (maximum != null && maximum.signum() <= 0)) {
                throw new IllegalArgumentException("Pricing fee limits must be positive amounts");
            }
            if (maximum != null && maximum.compareTo(minimum) < 0) {
                throw new IllegalArgumentException("Maximum fee cannot be less than minimum fee");
            }
            return new ServicePricingRuleDefinition(model, basis, minimum, maximum);
        } catch (IllegalArgumentException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalArgumentException("Pricing rule definition must be valid JSON", exception);
        }
    }

    static void validate(String definition, BigDecimal amountOrRate) {
        ServicePricingRuleDefinition parsed = parse(definition);
        if (FIXED.equals(parsed.pricingModel())) {
            if (amountOrRate == null || amountOrRate.signum() <= 0) {
                throw new IllegalArgumentException("Fixed service price must be greater than zero");
            }
        } else if (amountOrRate == null || amountOrRate.signum() <= 0
                || amountOrRate.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Percentage rate must be greater than zero and no more than 100%");
        }
    }

    private static BigDecimal decimal(JsonNode root, String field, BigDecimal fallback) {
        JsonNode value = root.path(field);
        if (value.isMissingNode() || value.isNull()) return fallback;
        if (!value.isNumber()) throw new IllegalArgumentException(field + " must be a number");
        return value.decimalValue();
    }
}
