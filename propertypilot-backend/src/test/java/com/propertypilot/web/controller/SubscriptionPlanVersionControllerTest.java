package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.CreateSubscriptionPlanVersionRequest;
import com.propertypilot.application.dto.SubscriptionPlanVersionResponse;
import com.propertypilot.application.dto.UpdateSubscriptionPlanVersionRequest;
import com.propertypilot.application.service.SubscriptionPlanVersionService;
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
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SubscriptionPlanVersionController.class)
@AutoConfigureMockMvc(addFilters = false)
class SubscriptionPlanVersionControllerTest
        extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SubscriptionPlanVersionService
            subscriptionPlanVersionService;

    @Test
    void createPlanVersion()
            throws Exception {

        SubscriptionPlanVersionResponse response =
                buildResponse();

        when(subscriptionPlanVersionService
                .createPlanVersion(any(
                        CreateSubscriptionPlanVersionRequest.class)))
                .thenReturn(response);

        CreateSubscriptionPlanVersionRequest request =
                new CreateSubscriptionPlanVersionRequest();

        request.setSubscriptionPlanId(
                UUID.randomUUID());

        request.setVersionNumber(1);

        request.setPrice(
                BigDecimal.valueOf(4999));

        request.setCurrencyCode("INR");

        request.setEffectiveFrom(
                OffsetDateTime.now());

        request.setStatus("ACTIVE");

        request.setBenefitsJson(
                Map.of("visits", 12));

        mockMvc.perform(
                        post("/api/subscription-plan-versions")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.versionNumber")
                        .value(1))
                .andExpect(jsonPath("$.currencyCode")
                        .value("INR"));
    }

    @Test
    void getPlanVersion()
            throws Exception {

        UUID planVersionId =
                UUID.randomUUID();

        when(subscriptionPlanVersionService
                .getPlanVersion(planVersionId))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        get("/api/subscription-plan-versions/{planVersionId}",
                                planVersionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.versionNumber")
                        .value(1));
    }

    @Test
    void getAllPlanVersions()
            throws Exception {

        when(subscriptionPlanVersionService
                .getAllPlanVersions())
                .thenReturn(
                        List.of(buildResponse()));

        mockMvc.perform(
                        get("/api/subscription-plan-versions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].versionNumber")
                        .value(1));
    }

    @Test
    void updatePlanVersion()
            throws Exception {

        UUID planVersionId =
                UUID.randomUUID();

        SubscriptionPlanVersionResponse response =
                buildResponse();

        when(subscriptionPlanVersionService
                .updatePlanVersion(
                        eq(planVersionId),
                        any(UpdateSubscriptionPlanVersionRequest.class)))
                .thenReturn(response);

        UpdateSubscriptionPlanVersionRequest request =
                new UpdateSubscriptionPlanVersionRequest();

        request.setVersionNumber(2);

        request.setPrice(
                BigDecimal.valueOf(5999));

        request.setCurrencyCode("INR");

        request.setStatus("ACTIVE");

        mockMvc.perform(
                        put("/api/subscription-plan-versions/{planVersionId}",
                                planVersionId)
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currencyCode")
                        .value("INR"));
    }

    private SubscriptionPlanVersionResponse
    buildResponse() {

        SubscriptionPlanVersionResponse response =
                new SubscriptionPlanVersionResponse();

        response.setPlanVersionId(
                UUID.randomUUID());

        response.setSubscriptionPlanId(
                UUID.randomUUID());

        response.setVersionNumber(1);

        response.setPrice(
                BigDecimal.valueOf(4999));

        response.setCurrencyCode("INR");

        response.setEffectiveFrom(
                OffsetDateTime.now());

        response.setEffectiveTo(
                OffsetDateTime.now().plusMonths(12));

        response.setStatus("ACTIVE");

        response.setBenefitsJson(
                Map.of(
                        "visits", 12,
                        "support", "PRIORITY"));

        return response;
    }
}