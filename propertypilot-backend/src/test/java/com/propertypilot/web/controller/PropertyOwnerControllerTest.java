package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.CreatePropertyOwnerRequest;
import com.propertypilot.application.dto.PropertyOwnerResponse;
import com.propertypilot.application.dto.UpdatePropertyOwnerRequest;
import com.propertypilot.application.service.PropertyOwnerService;
import com.propertypilot.security.CustomUserDetailsService;
import com.propertypilot.security.JwtService;
import com.propertypilot.web.exception.GlobalExceptionHandler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PropertyOwnerController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class PropertyOwnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PropertyOwnerService propertyOwnerService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Create Property Owner")
    void createOwner_success()
            throws Exception {

        when(propertyOwnerService.createOwner(
                any(CreatePropertyOwnerRequest.class)))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        post("/api/v1/property-owners")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(
                                        buildCreateRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ownerName")
                        .value("John Owner"));
    }

    @Test
    @DisplayName("Get Property Owner")
    void getOwner_success()
            throws Exception {

        UUID ownerId = UUID.randomUUID();

        when(propertyOwnerService.getOwner(ownerId))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        get("/api/v1/property-owners/{id}",
                                ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerName")
                        .value("John Owner"));
    }

    @Test
    @DisplayName("Get Owners By Property")
    void getOwnersByProperty_success()
            throws Exception {

        UUID propertyId = UUID.randomUUID();

        when(propertyOwnerService
                .getOwnersByProperty(propertyId))
                .thenReturn(
                        List.of(buildResponse()));

        mockMvc.perform(
                        get("/api/v1/properties/{id}/owners",
                                propertyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ownerName")
                        .value("John Owner"));
    }

    @Test
    @DisplayName("Update Property Owner")
    void updateOwner_success()
            throws Exception {

        UUID ownerId = UUID.randomUUID();

        when(propertyOwnerService.updateOwner(
                eq(ownerId),
                any(UpdatePropertyOwnerRequest.class)))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        put("/api/v1/property-owners/{id}",
                                ownerId)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(
                                        buildUpdateRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerName")
                        .value("John Owner"));
    }

    @Test
    @DisplayName("Delete Property Owner")
    void deleteOwner_success()
            throws Exception {

        UUID ownerId = UUID.randomUUID();

        doNothing().when(propertyOwnerService)
                .deleteOwner(ownerId);

        mockMvc.perform(
                        delete("/api/v1/property-owners/{id}",
                                ownerId))
                .andExpect(status().isNoContent());
    }

    private CreatePropertyOwnerRequest
    buildCreateRequest() {

        CreatePropertyOwnerRequest request =
                new CreatePropertyOwnerRequest();

        request.setPropertyId(
                UUID.randomUUID());

        request.setCustomerId(
                UUID.randomUUID());

        request.setOwnerName(
                "John Owner");

        request.setOwnershipPercentage(
                BigDecimal.valueOf(100));

        request.setOwnershipType(
                "SELF");

        request.setValidFrom(
                LocalDate.now());

        request.setIsPrimary(
                true);

        return request;
    }

    private UpdatePropertyOwnerRequest
    buildUpdateRequest() {

        UpdatePropertyOwnerRequest request =
                new UpdatePropertyOwnerRequest();

        request.setCustomerId(
                UUID.randomUUID());

        request.setOwnerName(
                "John Owner");

        request.setOwnershipPercentage(
                BigDecimal.valueOf(100));

        request.setOwnershipType(
                "SELF");

        request.setValidFrom(
                LocalDate.now());

        request.setIsPrimary(
                true);

        return request;
    }

    private PropertyOwnerResponse
    buildResponse() {

        PropertyOwnerResponse response =
                new PropertyOwnerResponse();

        response.setPropertyOwnerId(
                UUID.randomUUID());

        response.setPropertyId(
                UUID.randomUUID());

        response.setCustomerId(
                UUID.randomUUID());

        response.setOwnerName(
                "John Owner");

        response.setOwnershipPercentage(
                BigDecimal.valueOf(100));

        response.setOwnershipType(
                "SELF");

        response.setValidFrom(
                LocalDate.now());

        response.setIsPrimary(
                true);

        return response;
    }
}