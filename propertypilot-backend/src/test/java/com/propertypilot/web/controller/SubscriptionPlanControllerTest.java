package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.CreateSubscriptionPlanRequest;
import com.propertypilot.application.dto.SubscriptionPlanResponse;
import com.propertypilot.application.dto.UpdateSubscriptionPlanRequest;
import com.propertypilot.application.service.SubscriptionPlanService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SubscriptionPlanController.class)
@AutoConfigureMockMvc(addFilters = false)
class SubscriptionPlanControllerTest
        extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SubscriptionPlanService
            subscriptionPlanService;

    @Test
    void createPlan()
            throws Exception {

        CreateSubscriptionPlanRequest request =
                new CreateSubscriptionPlanRequest();

        request.setPlanCode(
                "PREMIUM");

        request.setPlanName(
                "Premium Plan");

        request.setDescription(
                "Premium subscription");

        request.setStatus(
                "ACTIVE");

        when(subscriptionPlanService
                .createPlan(any(
                        CreateSubscriptionPlanRequest.class)))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        post("/api/subscription-plans")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.planCode")
                        .value("PREMIUM"));

        verify(subscriptionPlanService)
                .createPlan(any(
                        CreateSubscriptionPlanRequest.class));
    }

    @Test
    void getPlan()
            throws Exception {

        UUID planId =
                UUID.randomUUID();

        when(subscriptionPlanService
                .getPlan(planId))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        get("/api/subscription-plans/{planId}",
                                planId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planCode")
                        .value("PREMIUM"));
    }

    @Test
    void getAllPlans()
            throws Exception {

        when(subscriptionPlanService
                .getAllPlans())
                .thenReturn(
                        List.of(buildResponse()));

        mockMvc.perform(
                        get("/api/subscription-plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].planCode")
                        .value("PREMIUM"));
    }

    @Test
    void updatePlan()
            throws Exception {

        UUID planId =
                UUID.randomUUID();

        UpdateSubscriptionPlanRequest request =
                new UpdateSubscriptionPlanRequest();

        request.setPlanName(
                "Premium Updated");

        request.setDescription(
                "Updated Description");

        request.setStatus(
                "ACTIVE");

        when(subscriptionPlanService
                .updatePlan(
                        eq(planId),
                        any(UpdateSubscriptionPlanRequest.class)))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        put("/api/subscription-plans/{planId}",
                                planId)
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planCode")
                        .value("PREMIUM"));

        verify(subscriptionPlanService)
                .updatePlan(
                        eq(planId),
                        any(UpdateSubscriptionPlanRequest.class));
    }

    private SubscriptionPlanResponse
    buildResponse() {

        SubscriptionPlanResponse response =
                new SubscriptionPlanResponse();

        response.setSubscriptionPlanId(
                UUID.randomUUID());

        response.setPlanCode(
                "PREMIUM");

        response.setPlanName(
                "Premium Plan");

        response.setDescription(
                "Premium subscription");

        response.setStatus(
                "ACTIVE");

        return response;
    }
}