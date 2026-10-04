package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.CreateVisitRequest;
import com.propertypilot.application.dto.VisitResponse;
import com.propertypilot.application.service.VisitService;
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

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VisitController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class VisitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VisitService visitService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Create Visit")
    void createVisit_success() throws Exception {

        CreateVisitRequest request =
                new CreateVisitRequest();

        request.setAssignmentId(
                UUID.randomUUID());

        request.setScheduledAt(
                OffsetDateTime.now());

        request.setNotes(
                "Initial visit");

        when(visitService.createVisit(
                any(CreateVisitRequest.class)))
                .thenReturn(buildVisitResponse());

        mockMvc.perform(
                        post("/api/v1/visits")
                                .contentType(
                                        "application/json")
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status")
                        .value("SCHEDULED"));
    }

    @Test
    @DisplayName(
            "Create Visit Validation Failure")
    void createVisit_validationFailure()
            throws Exception {

        mockMvc.perform(
                        post("/api/v1/visits")
                                .contentType(
                                        "application/json")
                                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"));
    }

    @Test
    @DisplayName("Get Visit")
    void getVisit_success()
            throws Exception {

        UUID visitId =
                UUID.randomUUID();

        when(visitService.getVisit(
                visitId))
                .thenReturn(
                        buildVisitResponse());

        mockMvc.perform(
                        get("/api/v1/visits/{visitId}",
                                visitId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("SCHEDULED"));
    }

    @Test
    @DisplayName("Start Visit")
    void startVisit_success()
            throws Exception {

        UUID visitId =
                UUID.randomUUID();

        VisitResponse response =
                buildVisitResponse();

        response.setStatus(
                "IN_PROGRESS");

        when(visitService.startVisit(
                visitId))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/visits/{visitId}/start",
                                visitId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("IN_PROGRESS"));
    }

    @Test
    @DisplayName("Complete Visit")
    void completeVisit_success()
            throws Exception {

        UUID visitId =
                UUID.randomUUID();

        VisitResponse response =
                buildVisitResponse();

        response.setStatus(
                "COMPLETED");

        when(visitService.completeVisit(
                visitId))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/visits/{visitId}/complete",
                                visitId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("COMPLETED"));
    }

    @Test
    @DisplayName("Cancel Visit")
    void cancelVisit_success()
            throws Exception {

        UUID visitId =
                UUID.randomUUID();

        VisitResponse response =
                buildVisitResponse();

        response.setStatus(
                "CANCELLED");

        when(visitService.cancelVisit(
                visitId,
                "Customer unavailable"))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/visits/{visitId}/cancel",
                                visitId)
                                .param(
                                        "reason",
                                        "Customer unavailable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("CANCELLED"));
    }

    private VisitResponse buildVisitResponse() {

        VisitResponse response =
                new VisitResponse();

        response.setVisitId(
                UUID.randomUUID());

        response.setAssignmentId(
                UUID.randomUUID());

        response.setStatus(
                "SCHEDULED");

        response.setScheduledAt(
                OffsetDateTime.now());

        response.setNotes(
                "Test Visit");

        return response;
    }
}