package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.PropertyCreateRequest;
import com.propertypilot.application.dto.PropertyResponse;
import com.propertypilot.application.dto.PropertyUpdateRequest;
import com.propertypilot.application.service.PropertyService;
import com.propertypilot.security.CustomUserDetailsService;
import com.propertypilot.security.JwtService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PropertyController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class PropertyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PropertyService propertyService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Create Property - Success")
    void createProperty_success() throws Exception {

        PropertyResponse response = buildPropertyResponse();

        when(propertyService.createProperty(any()))
                .thenReturn(response);

        PropertyCreateRequest request =
                new PropertyCreateRequest();

        request.setTitle("Luxury Villa");
        request.setPropertyType("VILLA");
        request.setPrice(BigDecimal.valueOf(5000000));

        mockMvc.perform(
                        post("/api/v1/properties")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title")
                        .value("Luxury Villa"))
                .andExpect(jsonPath("$.propertyType")
                        .value("VILLA"));
    }

    @Test
    @DisplayName("Create Property - Validation Failure")
    void createProperty_validationFailure() throws Exception {

        String request = """
            {
              "title":"",
              "propertyType":""
            }
            """;

        mockMvc.perform(
                        post("/api/v1/properties")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"));
    }

    @Test
    @DisplayName("Get Property By Id")
    void getPropertyById_success() throws Exception {

        UUID propertyId = UUID.randomUUID();

        when(propertyService.getPropertyById(propertyId))
                .thenReturn(buildPropertyResponse());

        mockMvc.perform(
                        get("/api/v1/properties/{propertyId}",
                                propertyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title")
                        .value("Luxury Villa"));
    }

    @Test
    @DisplayName("Get All Properties")
    void getAllProperties_success() throws Exception {

        when(propertyService.getAllProperties())
                .thenReturn(
                        List.of(buildPropertyResponse()));

        mockMvc.perform(
                        get("/api/v1/properties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title")
                        .value("Luxury Villa"));
    }

    @Test
    @DisplayName("Get Archived Properties")
    void getArchivedProperties_success() throws Exception {

        when(propertyService.getArchivedProperties())
                .thenReturn(
                        List.of(buildPropertyResponse()));

        mockMvc.perform(
                        get("/api/v1/properties/archived"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title")
                        .value("Luxury Villa"));
    }

    @Test
    @DisplayName("Update Property")
    void updateProperty_success() throws Exception {

        UUID propertyId = UUID.randomUUID();

        when(propertyService.updateProperty(
                any(UUID.class),
                any(PropertyUpdateRequest.class)))
                .thenReturn(buildPropertyResponse());

        PropertyUpdateRequest request =
                new PropertyUpdateRequest();

        request.setCustomerId(UUID.randomUUID());
        request.setTitle("Luxury Villa");
        request.setPropertyType("VILLA");

        mockMvc.perform(
                        put("/api/v1/properties/{propertyId}",
                                propertyId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title")
                        .value("Luxury Villa"));
    }

    @Test
    @DisplayName("Archive Property")
    void archiveProperty_success() throws Exception {

        UUID propertyId = UUID.randomUUID();

        when(propertyService.archiveProperty(propertyId))
                .thenReturn(buildPropertyResponse());

        mockMvc.perform(
                        patch("/api/v1/properties/{propertyId}/archive",
                                propertyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title")
                        .value("Luxury Villa"));
    }

    @Test
    @DisplayName("Restore Property")
    void restoreProperty_success() throws Exception {

        UUID propertyId = UUID.randomUUID();

        when(propertyService.restoreProperty(propertyId))
                .thenReturn(buildPropertyResponse());

        mockMvc.perform(
                        patch("/api/v1/properties/{propertyId}/restore",
                                propertyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title")
                        .value("Luxury Villa"));
    }

    private PropertyResponse buildPropertyResponse() {

        PropertyResponse response =
                new PropertyResponse();

        response.setPropertyId(UUID.randomUUID());
        response.setCustomerId(UUID.randomUUID());
        response.setTitle("Luxury Villa");
        response.setPropertyType("VILLA");
        response.setListingStatus("FOR_SALE");
        response.setPrice(BigDecimal.valueOf(5000000));
        response.setStatus("ACTIVE");

        return response;
    }
}