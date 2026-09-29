package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreatePricingRuleRequest;
import com.propertypilot.application.dto.PricingRuleResponse;
import com.propertypilot.application.dto.UpdatePricingRuleRequest;

import java.util.List;
import java.util.UUID;

public interface PricingRuleService {

    PricingRuleResponse createRule(
            CreatePricingRuleRequest request);

    PricingRuleResponse getRule(
            UUID ruleId);

    List<PricingRuleResponse> getAllRules();

    List<PricingRuleResponse> getRulesByService(
            UUID serviceId);

    PricingRuleResponse updateRule(
            UUID ruleId,
            UpdatePricingRuleRequest request);
}