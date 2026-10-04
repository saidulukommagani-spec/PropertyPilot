package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.CreateCustomerPreferenceRequest;
import com.propertypilot.application.dto.CustomerPreferenceResponse;
import com.propertypilot.application.service.CustomerPreferenceService;
import com.propertypilot.security.CustomUserDetailsService;
import com.propertypilot.security.JwtService;
import com.propertypilot.web.exception.GlobalExceptionHandler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerPreferenceController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class CustomerPreferenceControllerTest {

     @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerPreferenceService customerPreferenceService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;
    @Test
    @DisplayName("Should save customer preference")
    void savePreference_success()
            throws Exception {

        UUID customerId = UUID.randomUUID();

        CreateCustomerPreferenceRequest request =
                new CreateCustomerPreferenceRequest();

        request.setPreferredLanguage("EN");
        request.setPreferredTimezone("Asia/Kolkata");
        request.setCommunicationChannels("EMAIL");
        request.setPreferenceData("{\"theme\":\"dark\"}");
        request.setMarketingConsent(true);

        CustomerPreferenceResponse response =
                new CustomerPreferenceResponse();

        response.setCustomerPreferenceId(
                UUID.randomUUID());

        response.setCustomerId(customerId);

        response.setPreferredLanguage("EN");

        when(customerPreferenceService.savePreference(
                eq(customerId),
                any(CreateCustomerPreferenceRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        put("/api/customers/{customerId}/preferences",
                                customerId)
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.customerId")
                        .value(customerId.toString()))
                .andExpect(jsonPath(
                        "$.preferredLanguage")
                        .value("EN"));
    }

    @Test
    @DisplayName("Should get customer preference")
    void getPreference_success()
            throws Exception {

        UUID customerId = UUID.randomUUID();

        CustomerPreferenceResponse response =
                new CustomerPreferenceResponse();

        response.setCustomerPreferenceId(
                UUID.randomUUID());

        response.setCustomerId(customerId);

        response.setPreferredLanguage("EN");

        when(customerPreferenceService.getPreference(
                customerId))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/customers/{customerId}/preferences",
                                customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.customerId")
                        .value(customerId.toString()))
                .andExpect(jsonPath(
                        "$.preferredLanguage")
                        .value("EN"));
    }
}