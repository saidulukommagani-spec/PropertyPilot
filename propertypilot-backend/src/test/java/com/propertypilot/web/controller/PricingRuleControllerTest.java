package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.CreatePricingRuleRequest;
import com.propertypilot.application.dto.PricingRuleResponse;
import com.propertypilot.application.dto.UpdatePricingRuleRequest;
import com.propertypilot.application.service.PricingRuleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PricingRuleController.class)
@AutoConfigureMockMvc(addFilters = false)
class PricingRuleControllerTest
        extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PricingRuleService pricingRuleService;

    @Test
void createRule()
        throws Exception {

    CreatePricingRuleRequest request =
            new CreatePricingRuleRequest();

    request.setServiceId(
            UUID.randomUUID());

    request.setRuleName(
            "Standard Inspection Fee");

    request.setBaseAmount(
            new BigDecimal("1500.00"));

    request.setCurrencyCode(
            "INR");

    request.setRuleDefinition(
            "Flat pricing rule");

    request.setEffectiveFrom(
            OffsetDateTime.now());

    request.setStatus(
            "ACTIVE");

    PricingRuleResponse response =
            new PricingRuleResponse();

    when(pricingRuleService
            .createRule(any()))
            .thenReturn(response);

    mockMvc.perform(
                    post("/api/pricing-rules")
                            .contentType(
                                    MediaType.APPLICATION_JSON)
                            .content(
                                    objectMapper
                                            .writeValueAsString(
                                                    request)))
            .andExpect(status().isOk());

    verify(pricingRuleService)
            .createRule(any());
}

    @Test
    void getAllRules()
            throws Exception {

        when(pricingRuleService
                .getAllRules())
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/api/pricing-rules"))
                .andExpect(status().isOk());
    }

    @Test
    void getRule()
            throws Exception {

        UUID ruleId =
                UUID.randomUUID();

        when(pricingRuleService
                .getRule(ruleId))
                .thenReturn(
                        new PricingRuleResponse());

        mockMvc.perform(
                        get("/api/pricing-rules/{ruleId}",
                                ruleId))
                .andExpect(status().isOk());
    }

    @Test
    void getRulesByService()
            throws Exception {

        UUID serviceId =
                UUID.randomUUID();

        when(pricingRuleService
                .getRulesByService(serviceId))
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/api/pricing-rules/service/{serviceId}",
                                serviceId))
                .andExpect(status().isOk());
    }

    @Test
    void updateRule()
            throws Exception {

        UUID ruleId =
                UUID.randomUUID();

        UpdatePricingRuleRequest request =
                new UpdatePricingRuleRequest();

        when(pricingRuleService
                .updateRule(
                        eq(ruleId),
                        any()))
                .thenReturn(
                        new PricingRuleResponse());

        mockMvc.perform(
                        put("/api/pricing-rules/{ruleId}",
                                ruleId)
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andExpect(status().isOk());

        verify(pricingRuleService)
                .updateRule(
                        eq(ruleId),
                        any());
    }
}