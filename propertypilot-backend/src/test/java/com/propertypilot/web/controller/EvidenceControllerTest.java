package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.CreateEvidenceRequest;
import com.propertypilot.application.dto.EvidenceResponse;
import com.propertypilot.application.service.EvidenceService;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EvidenceController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class EvidenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EvidenceService evidenceService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Create Evidence")
    void createEvidence_success()
            throws Exception {

        when(evidenceService.createEvidence(
                any(CreateEvidenceRequest.class)))
                .thenReturn(buildResponse());

        CreateEvidenceRequest request =
                new CreateEvidenceRequest();

        request.setVisitId(
                UUID.randomUUID());

        request.setEvidenceTypeId(
                UUID.randomUUID());

        request.setFileUrl(
                "https://files/sample.jpg");

        request.setMediaType(
                "image/jpeg");

        mockMvc.perform(
                        post("/api/evidence")
                                .contentType(
                                        "application/json")
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andExpect(
                        status().isCreated())
                .andExpect(
                        jsonPath(
                                "$.evidenceTypeCode")
                                .value("PHOTO"));
    }

    @Test
    @DisplayName("Get Evidence")
    void getEvidence_success()
            throws Exception {

        UUID evidenceId =
                UUID.randomUUID();

        when(evidenceService.getEvidence(
                evidenceId))
                .thenReturn(
                        buildResponse());

        mockMvc.perform(
                        get(
                                "/api/evidence/{id}",
                                evidenceId))
                .andExpect(
                        status().isOk())
                .andExpect(
                        jsonPath(
                                "$.evidenceTypeCode")
                                .value("PHOTO"));
    }

    private EvidenceResponse buildResponse() {

        EvidenceResponse response =
                new EvidenceResponse();

        response.setEvidenceId(
                UUID.randomUUID());

        response.setVisitId(
                UUID.randomUUID());

        response.setEvidenceTypeId(
                UUID.randomUUID());

        response.setEvidenceTypeCode(
                "PHOTO");

        response.setEvidenceTypeName(
                "Property Photo");

        response.setFileUrl(
                "https://files/sample.jpg");

        response.setMediaType(
                "image/jpeg");

        response.setChecksumSha256(
                "abc123");

        response.setCapturedAt(
                OffsetDateTime.now());

        return response;
    }
}