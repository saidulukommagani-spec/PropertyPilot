package com.propertypilot.web.controller;

import com.propertypilot.application.dto.*;
import com.propertypilot.application.service.PricingRuleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pricing-rules")
public class PricingRuleController {

    private final PricingRuleService
            pricingRuleService;

    public PricingRuleController(
            PricingRuleService pricingRuleService) {

        this.pricingRuleService =
                pricingRuleService;
    }

    @PostMapping
    public ResponseEntity<PricingRuleResponse>
    createRule(
            @Valid
            @RequestBody
            CreatePricingRuleRequest request) {

        return ResponseEntity.ok(
                pricingRuleService
                        .createRule(request));
    }

    @GetMapping
    public ResponseEntity<
            List<PricingRuleResponse>>
    getAllRules() {

        return ResponseEntity.ok(
                pricingRuleService
                        .getAllRules());
    }

    @GetMapping("/{ruleId}")
    public ResponseEntity<PricingRuleResponse>
    getRule(
            @PathVariable UUID ruleId) {

        return ResponseEntity.ok(
                pricingRuleService
                        .getRule(ruleId));
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<
            List<PricingRuleResponse>>
    getRulesByService(
            @PathVariable UUID serviceId) {

        return ResponseEntity.ok(
                pricingRuleService
                        .getRulesByService(
                                serviceId));
    }

    @PutMapping("/{ruleId}")
    public ResponseEntity<PricingRuleResponse>
    updateRule(
            @PathVariable UUID ruleId,
            @RequestBody
            UpdatePricingRuleRequest request) {

        return ResponseEntity.ok(
                pricingRuleService
                        .updateRule(
                                ruleId,
                                request));
    }
}