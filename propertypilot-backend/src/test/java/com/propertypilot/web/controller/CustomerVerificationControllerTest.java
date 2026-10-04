package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.CreateCustomerVerificationRequest;
import com.propertypilot.application.dto.CustomerVerificationResponse;
import com.propertypilot.application.dto.VerificationDecisionRequest;
import com.propertypilot.application.service.CustomerVerificationService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerVerificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class CustomerVerificationControllerTest
        extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerVerificationService
            customerVerificationService;

    @Test
    @DisplayName(
            "Should create verification successfully")
    void createVerification_success()
            throws Exception {

        UUID customerId =
                UUID.randomUUID();

        UUID documentId =
                UUID.randomUUID();

        CreateCustomerVerificationRequest
                request =
                new CreateCustomerVerificationRequest();

        request.setDocumentId(documentId);
        request.setVerificationType(
                "AADHAR");
        request.setReferenceNumberHash(
                "HASH123");
        request.setProviderReference(
                "PROVIDER123");
        request.setExpiresAt(
                OffsetDateTime.now()
                        .plusYears(1));

        CustomerVerificationResponse
                response =
                new CustomerVerificationResponse();

        response.setCustomerVerificationId(
                UUID.randomUUID());
        response.setCustomerId(customerId);
        response.setDocumentId(documentId);
        response.setVerificationType(
                "AADHAR");
        response.setStatus(
                "PENDING");

        Mockito.when(
                        customerVerificationService
                                .createVerification(
                                        eq(customerId),
                                        any()))
                .thenReturn(response);

        mockMvc.perform(
                        post(
                                "/api/customers/{customerId}/verifications",
                                customerId)
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andExpect(
                        status().isOk())
                .andExpect(
                        jsonPath(
                                "$.verificationType")
                                .value("AADHAR"))
                .andExpect(
                        jsonPath(
                                "$.status")
                                .value("PENDING"));
    }

    @Test
    @DisplayName(
            "Should return customer verifications")
    void getCustomerVerifications_success()
            throws Exception {

        UUID customerId =
                UUID.randomUUID();

        CustomerVerificationResponse
                response =
                new CustomerVerificationResponse();

        response.setCustomerVerificationId(
                UUID.randomUUID());
        response.setCustomerId(customerId);
        response.setVerificationType(
                "AADHAR");
        response.setStatus(
                "APPROVED");

        Mockito.when(
                        customerVerificationService
                                .getCustomerVerifications(
                                        customerId))
                .thenReturn(
                        List.of(response));

        mockMvc.perform(
                        get(
                                "/api/customers/{customerId}/verifications",
                                customerId))
                .andExpect(
                        status().isOk())
                .andExpect(
                        jsonPath(
                                "$[0].status")
                                .value("APPROVED"));
    }

    @Test
    @DisplayName(
            "Should return verification by id")
    void getVerification_success()
            throws Exception {

        UUID verificationId =
                UUID.randomUUID();

        CustomerVerificationResponse
                response =
                new CustomerVerificationResponse();

        response.setCustomerVerificationId(
                verificationId);
        response.setVerificationType(
                "AADHAR");
        response.setStatus(
                "APPROVED");

        Mockito.when(
                        customerVerificationService
                                .getVerification(
                                        verificationId))
                .thenReturn(response);

        mockMvc.perform(
                        get(
                                "/api/customers/verifications/{verificationId}",
                                verificationId))
                .andExpect(
                        status().isOk())
                .andExpect(
                        jsonPath(
                                "$.status")
                                .value("APPROVED"));
    }

    @Test
    @DisplayName(
            "Should approve verification")
    void approveVerification_success()
            throws Exception {

        UUID verificationId =
                UUID.randomUUID();

        VerificationDecisionRequest
                request =
                new VerificationDecisionRequest();

        request.setVerifiedByUserId(
                UUID.randomUUID());

        CustomerVerificationResponse
                response =
                new CustomerVerificationResponse();

        response.setCustomerVerificationId(
                verificationId);
        response.setStatus(
                "APPROVED");

        Mockito.when(
                        customerVerificationService
                                .approveVerification(
                                        eq(verificationId),
                                        any()))
                .thenReturn(response);

        mockMvc.perform(
                        put(
                                "/api/customers/verifications/{verificationId}/approve",
                                verificationId)
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andExpect(
                        status().isOk())
                .andExpect(
                        jsonPath(
                                "$.status")
                                .value("APPROVED"));
    }

    @Test
    @DisplayName(
            "Should reject verification")
    void rejectVerification_success()
            throws Exception {

        UUID verificationId =
                UUID.randomUUID();

        VerificationDecisionRequest
                request =
                new VerificationDecisionRequest();

        request.setVerifiedByUserId(
                UUID.randomUUID());

        request.setRejectionReason(
                "Document mismatch");

        CustomerVerificationResponse
                response =
                new CustomerVerificationResponse();

        response.setCustomerVerificationId(
                verificationId);

        response.setStatus(
                "REJECTED");

        response.setRejectionReason(
                "Document mismatch");

        Mockito.when(
                        customerVerificationService
                                .rejectVerification(
                                        eq(verificationId),
                                        any()))
                .thenReturn(response);

        mockMvc.perform(
                        put(
                                "/api/customers/verifications/{verificationId}/reject",
                                verificationId)
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andExpect(
                        status().isOk())
                .andExpect(
                        jsonPath(
                                "$.status")
                                .value("REJECTED"));
    }
}