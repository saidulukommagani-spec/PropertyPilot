package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.ConsumptionResponse;
import com.propertypilot.application.dto.CreateConsumptionRequest;
import com.propertypilot.application.dto.SubscriptionUsageResponse;
import com.propertypilot.application.service.SubscriptionEntitlementConsumptionService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        SubscriptionEntitlementConsumptionController.class)
@AutoConfigureMockMvc(addFilters = false)
class SubscriptionEntitlementConsumptionControllerTest
        extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SubscriptionEntitlementConsumptionService
            consumptionService;

    @Test
    void consume()
            throws Exception {

        CreateConsumptionRequest request =
                new CreateConsumptionRequest();

        request.setCustomerSubscriptionId(
                UUID.randomUUID());

        request.setServiceId(
                UUID.randomUUID());

        request.setQuantity(
                1);

        when(consumptionService.consume(any()))
                .thenReturn(
                        buildConsumptionResponse());

        mockMvc.perform(
                        post(
                                "/api/subscription-consumptions")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.consumedQuantity")
                                .value(1));

        verify(consumptionService)
                .consume(any());
    }

    @Test
    void consumeValidationFailure()
            throws Exception {

        CreateConsumptionRequest request =
                new CreateConsumptionRequest();

        mockMvc.perform(
                        post(
                                "/api/subscription-consumptions")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andExpect(
                        status().isBadRequest());
    }

    @Test
    void getUsage()
            throws Exception {

        UUID subscriptionId =
                UUID.randomUUID();

        when(consumptionService.getUsage(
                eq(subscriptionId)))
                .thenReturn(
                        List.of(
                                buildUsageResponse()));

        mockMvc.perform(
                        get(
                                "/api/subscription-consumptions/{id}/usage",
                                subscriptionId))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$[0].remainingQuantity")
                                .value(4));
    }

    @Test
    void getRemaining()
            throws Exception {

        UUID subscriptionId =
                UUID.randomUUID();

        when(consumptionService.getRemaining(
                eq(subscriptionId)))
                .thenReturn(
                        List.of(
                                buildUsageResponse()));

        mockMvc.perform(
                        get(
                                "/api/subscription-consumptions/{id}/remaining",
                                subscriptionId))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$[0].remainingQuantity")
                                .value(4));
    }

    private ConsumptionResponse
    buildConsumptionResponse() {

        ConsumptionResponse response =
                new ConsumptionResponse();

        response.setEntitlementConsumptionId(
                UUID.randomUUID());

        response.setCustomerSubscriptionId(
                UUID.randomUUID());

        response.setServiceId(
                UUID.randomUUID());

        response.setEntitledQuantity(
                5);

        response.setConsumedQuantity(
                1);

        response.setRemainingQuantity(
                4);

        response.setPeriodStart(
                LocalDate.now());

        response.setPeriodEnd(
                LocalDate.now().plusMonths(1));

        return response;
    }

    private SubscriptionUsageResponse
    buildUsageResponse() {

        SubscriptionUsageResponse response =
                new SubscriptionUsageResponse();

        response.setServiceId(
                UUID.randomUUID());

        response.setEntitledQuantity(
                5);

        response.setConsumedQuantity(
                1);

        response.setRemainingQuantity(
                4);

        return response;
    }
}