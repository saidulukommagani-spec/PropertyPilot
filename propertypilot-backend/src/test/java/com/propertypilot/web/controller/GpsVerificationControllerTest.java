package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.propertypilot.application.dto.CreateGpsVerificationRequest;
import com.propertypilot.application.dto.GpsVerificationResponse;
import com.propertypilot.application.service.GpsVerificationService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GpsVerificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class GpsVerificationControllerTest
        extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GpsVerificationService
            gpsVerificationService;

    @Test
    void createVerification()
            throws Exception {

        CreateGpsVerificationRequest request =
                new CreateGpsVerificationRequest();

        request.setVisitId(
                UUID.randomUUID());

        request.setLatitude(
                new BigDecimal("17.385044"));

        request.setLongitude(
                new BigDecimal("78.486671"));

        when(gpsVerificationService
                .createVerification(any()))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        post("/api/gps-verifications")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.validationStatus")
                        .value("VALID"));

        verify(gpsVerificationService)
                .createVerification(any());
    }

    @Test
    void getVerification()
            throws Exception {

        UUID verificationId =
                UUID.randomUUID();

        when(gpsVerificationService
                .getVerification(
                        verificationId))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        get("/api/gps-verifications/{id}",
                                verificationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath(
                        "$.validationStatus")
                        .value("VALID"));
    }

    private GpsVerificationResponse
    buildResponse() {

        GpsVerificationResponse response =
                new GpsVerificationResponse();

        response.setGpsVerificationId(
                UUID.randomUUID());

        response.setVisitId(
                UUID.randomUUID());

        response.setValidationStatus(
                "VALID");

        response.setLatitude(
                new BigDecimal("17.385044"));

        response.setLongitude(
                new BigDecimal("78.486671"));

        response.setCapturedAt(
                OffsetDateTime.now());

        return response;
    }
}