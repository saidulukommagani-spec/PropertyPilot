package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.CreateSubscriptionBenefitRequest;
import com.propertypilot.application.dto.SubscriptionBenefitResponse;
import com.propertypilot.application.dto.UpdateSubscriptionBenefitRequest;
import com.propertypilot.application.service.SubscriptionBenefitService;

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

@WebMvcTest(SubscriptionBenefitController.class)
@AutoConfigureMockMvc(addFilters = false)
class SubscriptionBenefitControllerTest
        extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SubscriptionBenefitService
            subscriptionBenefitService;

    @Test
    void createBenefit()
            throws Exception {

        CreateSubscriptionBenefitRequest request =
                new CreateSubscriptionBenefitRequest();

        request.setPlanId(
                UUID.randomUUID());

        request.setBenefitType(
                "FREE_VISIT");

        request.setBenefitValue(
                "2");

        request.setStatus(
                "ACTIVE");

        when(subscriptionBenefitService
                .createBenefit(any(
                        CreateSubscriptionBenefitRequest.class)))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        post("/api/subscription-benefits")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.benefitType")
                        .value("FREE_VISIT"));

        verify(subscriptionBenefitService)
                .createBenefit(any(
                        CreateSubscriptionBenefitRequest.class));
    }

    @Test
    void getBenefit()
            throws Exception {

        UUID benefitId =
                UUID.randomUUID();

        when(subscriptionBenefitService
                .getBenefit(benefitId))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        get("/api/subscription-benefits/{benefitId}",
                                benefitId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.benefitType")
                        .value("FREE_VISIT"));
    }

    @Test
    void getAllBenefits()
            throws Exception {

        when(subscriptionBenefitService
                .getAllBenefits())
                .thenReturn(
                        List.of(buildResponse()));

        mockMvc.perform(
                        get("/api/subscription-benefits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].benefitType")
                        .value("FREE_VISIT"));
    }

    @Test
    void updateBenefit()
            throws Exception {

        UUID benefitId =
                UUID.randomUUID();

        UpdateSubscriptionBenefitRequest request =
                new UpdateSubscriptionBenefitRequest();

        request.setBenefitType(
                "FREE_VISIT");

        request.setBenefitValue(
                "5");

        request.setStatus(
                "ACTIVE");

        when(subscriptionBenefitService
                .updateBenefit(
                        eq(benefitId),
                        any(UpdateSubscriptionBenefitRequest.class)))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        put("/api/subscription-benefits/{benefitId}",
                                benefitId)
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.benefitType")
                        .value("FREE_VISIT"));

        verify(subscriptionBenefitService)
                .updateBenefit(
                        eq(benefitId),
                        any(UpdateSubscriptionBenefitRequest.class));
    }

    private SubscriptionBenefitResponse
    buildResponse() {

        SubscriptionBenefitResponse response =
                new SubscriptionBenefitResponse();

        response.setBenefitId(
                UUID.randomUUID());

        response.setPlanId(
                UUID.randomUUID());

        response.setBenefitType(
                "FREE_VISIT");

        response.setBenefitValue(
                "2");

        response.setStatus(
                "ACTIVE");

        return response;
    }
}