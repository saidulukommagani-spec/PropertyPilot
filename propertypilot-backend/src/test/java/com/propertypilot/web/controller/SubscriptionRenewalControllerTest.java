package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.CreateSubscriptionRenewalRequest;
import com.propertypilot.application.dto.SubscriptionRenewalResponse;
import com.propertypilot.application.service.SubscriptionRenewalService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
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

@WebMvcTest(SubscriptionRenewalController.class)
@AutoConfigureMockMvc(addFilters = false)
class SubscriptionRenewalControllerTest
        extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SubscriptionRenewalService
            renewalService;

    @Test
    void renewSubscription()
            throws Exception {

        CreateSubscriptionRenewalRequest request =
                new CreateSubscriptionRenewalRequest();

        request.setCustomerSubscriptionId(
                UUID.randomUUID());

        request.setPeriodStart(
                LocalDate.now());

        request.setPeriodEnd(
                LocalDate.now().plusYears(1));

        request.setRenewalAmount(
                new BigDecimal("9999"));

        when(renewalService
                .renewSubscription(any()))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        post("/api/subscription-renewals")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status")
                        .value("SUCCESS"));

        verify(renewalService)
                .renewSubscription(any());
    }

    @Test
    void getRenewal()
            throws Exception {

        UUID renewalId =
                UUID.randomUUID();

        when(renewalService
                .getRenewal(
                        renewalId))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        get("/api/subscription-renewals/{id}",
                                renewalId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("SUCCESS"));
    }

    @Test
    void getRenewalsBySubscription()
            throws Exception {

        UUID subscriptionId =
                UUID.randomUUID();

        when(renewalService
                .getRenewalsBySubscription(
                        eq(subscriptionId)))
                .thenReturn(
                        List.of(buildResponse()));

        mockMvc.perform(
                        get("/api/subscription-renewals/subscription/{id}",
                                subscriptionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status")
                        .value("SUCCESS"));
    }

    private SubscriptionRenewalResponse
    buildResponse() {

        SubscriptionRenewalResponse response =
                new SubscriptionRenewalResponse();

        response.setSubscriptionRenewalId(
                UUID.randomUUID());

        response.setStatus(
                "SUCCESS");

        response.setRenewalAmount(
                new BigDecimal("9999"));

        return response;
    }
}