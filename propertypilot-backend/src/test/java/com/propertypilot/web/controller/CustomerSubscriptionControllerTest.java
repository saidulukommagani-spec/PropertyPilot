package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.CreateCustomerSubscriptionRequest;
import com.propertypilot.application.dto.CustomerSubscriptionResponse;
import com.propertypilot.application.dto.UpdateCustomerSubscriptionRequest;
import com.propertypilot.application.service.CustomerSubscriptionService;
import com.propertypilot.web.exception.GlobalExceptionHandler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerSubscriptionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class CustomerSubscriptionControllerTest
        extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerSubscriptionService
            customerSubscriptionService;

    @Test
    @DisplayName(
            "Should create customer subscription successfully")
    void createSubscription()
            throws Exception {

        UUID customerId =
                UUID.randomUUID();

        CreateCustomerSubscriptionRequest request =
                new CreateCustomerSubscriptionRequest();

        request.setPlanVersionId(
                UUID.randomUUID());

        request.setStartDate(
                LocalDate.now());

        request.setEndDate(
                LocalDate.now().plusMonths(1));

        request.setAutoRenew(true);

        CustomerSubscriptionResponse response =
                buildResponse(customerId);

        when(customerSubscriptionService
                .createSubscription(
                        eq(customerId),
                        any()))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/customers/{customerId}/subscriptions",
                                customerId)
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.status")
                                .value("ACTIVE"));

        verify(customerSubscriptionService)
                .createSubscription(
                        eq(customerId),
                        any());
    }

    @Test
    @DisplayName(
            "Should return customer subscriptions")
    void getCustomerSubscriptions()
            throws Exception {

        UUID customerId =
                UUID.randomUUID();

        when(customerSubscriptionService
                .getCustomerSubscriptions(
                        customerId))
                .thenReturn(
                        List.of(
                                buildResponse(customerId)));

        mockMvc.perform(
                        get("/api/customers/{customerId}/subscriptions",
                                customerId))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].status")
                                .value("ACTIVE"));
    }

    @Test
    @DisplayName(
            "Should return subscription by id")
    void getSubscription()
            throws Exception {

        UUID subscriptionId =
                UUID.randomUUID();

        CustomerSubscriptionResponse response =
                buildResponse(
                        UUID.randomUUID());

        response.setCustomerSubscriptionId(
                subscriptionId);

        when(customerSubscriptionService
                .getSubscription(
                        subscriptionId))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/customers/subscriptions/{subscriptionId}",
                                subscriptionId))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.status")
                                .value("ACTIVE"));
    }

    @Test
    @DisplayName(
            "Should update subscription successfully")
    void updateSubscription()
            throws Exception {

        UUID subscriptionId =
                UUID.randomUUID();

        UpdateCustomerSubscriptionRequest request =
                new UpdateCustomerSubscriptionRequest();

        request.setStatus("ACTIVE");
        request.setAutoRenew(true);
        request.setEndDate(
                LocalDate.now().plusMonths(6));

        when(customerSubscriptionService
                .updateSubscription(
                        eq(subscriptionId),
                        any()))
                .thenReturn(
                        buildResponse(
                                UUID.randomUUID()));

        mockMvc.perform(
                        put("/api/customers/subscriptions/{subscriptionId}",
                                subscriptionId)
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.status")
                                .value("ACTIVE"));
    }

    @Test
    @DisplayName(
            "Should return expiring subscriptions")
    void getExpiringSubscriptions()
            throws Exception {

        when(customerSubscriptionService
                .getExpiringSubscriptions(
                        30))
                .thenReturn(
                        List.of(
                                buildResponse(
                                        UUID.randomUUID())));

        mockMvc.perform(
                        get("/api/customers/subscriptions/expiring")
                                .param(
                                        "days",
                                        "30"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].status")
                                .value("ACTIVE"));
    }

    private CustomerSubscriptionResponse
    buildResponse(
            UUID customerId) {

        CustomerSubscriptionResponse response =
                new CustomerSubscriptionResponse();

        response.setCustomerSubscriptionId(
                UUID.randomUUID());

        response.setCustomerId(
                customerId);

        response.setPlanVersionId(
                UUID.randomUUID());

        response.setStatus(
                "ACTIVE");

        response.setStartDate(
                LocalDate.now());

        response.setEndDate(
                LocalDate.now().plusMonths(1));

        response.setAutoRenew(
                true);

        return response;
    }
}