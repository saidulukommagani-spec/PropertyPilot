package com.propertypilot.web.controller;


import com.propertypilot.application.dto.PropertyResponse;
import com.propertypilot.application.service.PropertyService;
import com.propertypilot.security.JwtAuthenticationFilter;
import com.propertypilot.security.JwtService;
import com.propertypilot.security.SecurityService;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(PropertyController.class)
@AutoConfigureMockMvc(addFilters = false)
class PropertyControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockitoBean
private PropertyService propertyService;

@MockitoBean
private JwtAuthenticationFilter jwtAuthenticationFilter;

@MockitoBean
private JwtService jwtService;

@MockitoBean
private SecurityService securityService;

    @Test
    void getPropertyById_ShouldReturnProperty()
            throws Exception {

        UUID propertyId = UUID.randomUUID();

        PropertyResponse response =
                propertyResponse(propertyId);

        when(propertyService.getPropertyById(
                propertyId))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/v1/properties/{propertyId}", propertyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.propertyId")
                        .value(propertyId.toString()))
                .andExpect(jsonPath(
                        "$.title")
                        .value("Test Property"));
    }

    @Test
    void getAllProperties_ShouldReturnList()
            throws Exception {

        PropertyResponse response =
                propertyResponse(
                        UUID.randomUUID());

        when(propertyService.getAllProperties())
                .thenReturn(
                        List.of(response));

        mockMvc.perform(
                        get("/api/v1/properties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.length()")
                        .value(1));
    }

    private PropertyResponse propertyResponse(
            UUID propertyId) {

        PropertyResponse response =
                new PropertyResponse();

        response.setPropertyId(
                propertyId);

        response.setCustomerId(
                UUID.randomUUID());

        response.setTitle(
                "Test Property");

        response.setPropertyType(
                "PLOT");

        response.setListingStatus(
                "FOR_SALE");

        response.setPrice(
                BigDecimal.valueOf(
                        1000000));

        response.setStatus(
                "AVAILABLE");

        return response;
    }
}