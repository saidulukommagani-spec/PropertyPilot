package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.CreatePricingEstimateRequest;
import com.propertypilot.application.dto.PricingEstimateResponse;
import com.propertypilot.application.service.PricingEngineService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PricingController.class)
@AutoConfigureMockMvc(addFilters = false)
class PricingControllerTest
        extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PricingEngineService pricingEngineService;

    @Test
    void calculatePricing()
            throws Exception {

        CreatePricingEstimateRequest request =
                new CreatePricingEstimateRequest();

        request.setCustomerId(
                UUID.randomUUID());

        request.setPropertyId(
                UUID.randomUUID());

        request.setServiceId(
                UUID.randomUUID());

        request.setOneWayDistanceKm(
                12.5);

        request.setVendorCost(
                new BigDecimal("1500"));

        request.setEtaDays(
                2);

        PricingEstimateResponse response =
                new PricingEstimateResponse();

        response.setStandardPrice(
                new BigDecimal("2500"));

        response.setCustomerPrice(
                new BigDecimal("2200"));

        response.setSavings(
                new BigDecimal("300"));

        when(pricingEngineService
                .calculateEstimate(any()))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/pricing/calculate")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request)))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.customerPrice")
                                .value(2200))
                .andExpect(
                        jsonPath("$.standardPrice")
                                .value(2500));

        verify(pricingEngineService)
                .calculateEstimate(any());
    }

    @Test
    void calculatePricingValidationFailure()
            throws Exception {

        CreatePricingEstimateRequest request =
                new CreatePricingEstimateRequest();

        mockMvc.perform(
                        post("/api/v1/pricing/calculate")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request)))
                .andExpect(status().isBadRequest());
    }
}
