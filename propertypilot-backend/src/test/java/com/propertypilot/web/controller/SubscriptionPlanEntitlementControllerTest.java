package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.CreateSubscriptionPlanEntitlementRequest;
import com.propertypilot.application.dto.SubscriptionPlanEntitlementResponse;
import com.propertypilot.application.dto.UpdateSubscriptionPlanEntitlementRequest;
import com.propertypilot.application.service.SubscriptionPlanEntitlementService;
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
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SubscriptionPlanEntitlementController.class)
@AutoConfigureMockMvc(addFilters = false)
class SubscriptionPlanEntitlementControllerTest
        extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SubscriptionPlanEntitlementService
            entitlementService;

    @Test
    void createEntitlement()
            throws Exception {

        SubscriptionPlanEntitlementResponse response =
                buildResponse();

        when(entitlementService.createEntitlement(
                any(CreateSubscriptionPlanEntitlementRequest.class)))
                .thenReturn(response);

        CreateSubscriptionPlanEntitlementRequest request =
                new CreateSubscriptionPlanEntitlementRequest();

        request.setPlanVersionId(
                UUID.randomUUID());

        request.setServiceId(
                UUID.randomUUID());

        request.setQuantity(12);

        request.setPeriodType(
                "MONTHLY");

        request.setCarryForwardAllowed(
                true);

        request.setStatus(
                "ACTIVE");

        mockMvc.perform(
                        post("/api/subscription-plan-entitlements")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity")
                        .value(12))
                .andExpect(jsonPath("$.periodType")
                        .value("MONTHLY"));
    }

    @Test
    void getEntitlement()
            throws Exception {

        UUID entitlementId =
                UUID.randomUUID();

        when(entitlementService.getEntitlement(
                entitlementId))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        get("/api/subscription-plan-entitlements/{entitlementId}",
                                entitlementId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity")
                        .value(12));
    }

    @Test
    void getAllEntitlements()
            throws Exception {

        when(entitlementService.getAllEntitlements())
                .thenReturn(
                        List.of(buildResponse()));

        mockMvc.perform(
                        get("/api/subscription-plan-entitlements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quantity")
                        .value(12));
    }

    @Test
    void updateEntitlement()
            throws Exception {

        UUID entitlementId =
                UUID.randomUUID();

        SubscriptionPlanEntitlementResponse response =
                buildResponse();

        when(entitlementService.updateEntitlement(
                eq(entitlementId),
                any(UpdateSubscriptionPlanEntitlementRequest.class)))
                .thenReturn(response);

        UpdateSubscriptionPlanEntitlementRequest request =
                new UpdateSubscriptionPlanEntitlementRequest();

        request.setQuantity(24);

        request.setPeriodType(
                "YEARLY");

        request.setCarryForwardAllowed(
                false);

        request.setStatus(
                "ACTIVE");

        mockMvc.perform(
                        put("/api/subscription-plan-entitlements/{entitlementId}",
                                entitlementId)
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.periodType")
                        .value("MONTHLY"));
    }

    private SubscriptionPlanEntitlementResponse
    buildResponse() {

        SubscriptionPlanEntitlementResponse response =
                new SubscriptionPlanEntitlementResponse();

        response.setEntitlementId(
                UUID.randomUUID());

        response.setPlanVersionId(
                UUID.randomUUID());

        response.setServiceId(
                UUID.randomUUID());

        response.setQuantity(
                12);

        response.setPeriodType(
                "MONTHLY");

        response.setCarryForwardAllowed(
                true);

        response.setStatus(
                "ACTIVE");

        return response;
    }
}