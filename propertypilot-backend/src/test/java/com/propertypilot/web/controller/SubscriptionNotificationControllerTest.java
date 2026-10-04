package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.propertypilot.application.dto.SubscriptionNotificationResponse;
import com.propertypilot.application.service.SubscriptionNotificationService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        SubscriptionNotificationController.class
)
@AutoConfigureMockMvc(
        addFilters = false
)
class SubscriptionNotificationControllerTest
        extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SubscriptionNotificationService
            notificationService;

    @Test
    void getNotifications()
            throws Exception {

        UUID subscriptionId =
                UUID.randomUUID();

        when(notificationService
                .getNotifications(
                        eq(subscriptionId)))
                .thenReturn(
                        List.of(
                                buildResponse()));

        mockMvc.perform(
                        get(
                                "/api/subscription-notifications/{customerSubscriptionId}",
                                subscriptionId))
                .andExpect(
                        status().isOk())
                .andExpect(
                        jsonPath("$[0].notificationType")
                                .value(
                                        "RENEWAL_REMINDER"))
                .andExpect(
                        jsonPath("$[0].status")
                                .value(
                                        "SENT"));
    }

    private SubscriptionNotificationResponse
    buildResponse() {

        SubscriptionNotificationResponse response =
                new SubscriptionNotificationResponse();

        response.setNotificationId(
                UUID.randomUUID());

        response.setCustomerSubscriptionId(
                UUID.randomUUID());

        response.setNotificationType(
                "RENEWAL_REMINDER");

        response.setMessage(
                "Subscription renewal due in 7 days");

        response.setStatus(
                "SENT");

        response.setSentDate(
                LocalDateTime.now());

        return response;
    }
}