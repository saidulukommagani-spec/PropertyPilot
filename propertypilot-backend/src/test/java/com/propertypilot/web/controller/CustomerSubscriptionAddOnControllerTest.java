package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.CreateCustomerSubscriptionAddOnRequest;
import com.propertypilot.application.dto.CustomerSubscriptionAddOnResponse;
import com.propertypilot.application.service.CustomerSubscriptionAddOnService;
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

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerSubscriptionAddOnController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class CustomerSubscriptionAddOnControllerTest
        extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerSubscriptionAddOnService
            customerSubscriptionAddOnService;

    @Test
    @DisplayName(
            "Should create add-on successfully")
    void createAddOn()
            throws Exception {

        UUID subscriptionId =
                UUID.randomUUID();

        CreateCustomerSubscriptionAddOnRequest request =
                new CreateCustomerSubscriptionAddOnRequest();

        request.setSubscriptionAddOnId(
                UUID.randomUUID());

        request.setQuantity(1);

        CustomerSubscriptionAddOnResponse response =
                buildResponse(subscriptionId);

        when(customerSubscriptionAddOnService
                .createAddOn(
                        eq(subscriptionId),
                        any()))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/customer-subscriptions/{id}/add-ons",
                                subscriptionId)
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request)))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.status")
                                .value("ACTIVE"));
    }

    @Test
    @DisplayName(
            "Should return add-ons")
    void getAddOns()
            throws Exception {

        UUID subscriptionId =
                UUID.randomUUID();

        when(customerSubscriptionAddOnService
                .getSubscriptionAddOns(
                        subscriptionId))
                .thenReturn(
                        List.of(
                                buildResponse(
                                        subscriptionId)));

        mockMvc.perform(
                        get("/api/customer-subscriptions/{id}/add-ons",
                                subscriptionId))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].status")
                                .value("ACTIVE"));
    }

    @Test
    @DisplayName(
            "Should return add-on by id")
    void getAddOn()
            throws Exception {

        UUID addOnId =
                UUID.randomUUID();

        when(customerSubscriptionAddOnService
                .getAddOn(addOnId))
                .thenReturn(
                        buildResponse(
                                UUID.randomUUID()));

        mockMvc.perform(
                        get("/api/customer-subscriptions/add-ons/{id}",
                                addOnId))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.status")
                                .value("ACTIVE"));
    }

    @Test
    @DisplayName(
            "Should delete add-on")
    void deleteAddOn()
            throws Exception {

        UUID addOnId =
                UUID.randomUUID();

        doNothing()
                .when(customerSubscriptionAddOnService)
                .deleteAddOn(addOnId);

        mockMvc.perform(
                        delete("/api/customer-subscriptions/add-ons/{id}",
                                addOnId))
                .andExpect(status().isNoContent());

        verify(customerSubscriptionAddOnService)
                .deleteAddOn(addOnId);
    }

    private CustomerSubscriptionAddOnResponse
    buildResponse(
            UUID subscriptionId) {

        CustomerSubscriptionAddOnResponse response =
                new CustomerSubscriptionAddOnResponse();

        response.setCustomerSubscriptionAddOnId(
                UUID.randomUUID());

        response.setCustomerSubscriptionId(
                subscriptionId);

        response.setSubscriptionAddOnId(
                UUID.randomUUID());

        response.setAddOnName(
                "Premium Support");

        response.setQuantity(1);

        response.setStatus(
                "ACTIVE");

        response.setStartsAt(
                OffsetDateTime.now());

        response.setEndsAt(
                OffsetDateTime.now()
                        .plusMonths(1));

        return response;
    }
}