package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.PricingParameterResponse;
import com.propertypilot.application.dto.UpdatePricingParameterRequest;
import com.propertypilot.application.service.PricingConfigurationService;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PricingConfigurationController.class)
@AutoConfigureMockMvc(addFilters = false)
class PricingConfigurationControllerTest
        extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PricingConfigurationService
            pricingConfigurationService;

    @Test
    void getProfileParameters()
            throws Exception {

        when(pricingConfigurationService
                .getProfileParameters(
                        "STANDARD"))
                .thenReturn(
                        List.of(
                                buildResponse()));

        mockMvc.perform(
                        get("/api/admin/pricing/profiles/STANDARD/parameters"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].parameterCode")
                                .value(
                                        "GST_PERCENT"));

        verify(pricingConfigurationService)
                .getProfileParameters(
                        "STANDARD");
    }

    @Test
    void updateParameter()
            throws Exception {

        UUID parameterId =
                UUID.randomUUID();

        UpdatePricingParameterRequest request =
                new UpdatePricingParameterRequest();

        request.setParameterValue(
                "18");

        request.setRemarks(
                "Updated GST");

        when(pricingConfigurationService
                .updateParameter(
                        eq(parameterId),
                        any(
                                UpdatePricingParameterRequest.class)))
                .thenReturn(
                        buildResponse());

        mockMvc.perform(
                        put(
                                "/api/admin/pricing/parameters/{parameterId}",
                                parameterId)
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.parameterCode")
                                .value(
                                        "GST_PERCENT"));

        verify(pricingConfigurationService)
                .updateParameter(
                        eq(parameterId),
                        any(
                                UpdatePricingParameterRequest.class));
    }

    private PricingParameterResponse
    buildResponse() {

        PricingParameterResponse response =
                new PricingParameterResponse();

        response.setPricingParameterId(
                UUID.randomUUID());

        response.setProfileCode(
                "STANDARD");

        response.setParameterCode(
                "GST_PERCENT");

        response.setParameterName(
                "GST Percentage");

        response.setParameterValue(
                "18");

        response.setParameterType(
                "DECIMAL");

        response.setActiveFlag(
                true);

        response.setVersion(
                1L);

        return response;
    }
}