package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.propertypilot.application.dto.CreatePaymentRequest;
import com.propertypilot.application.dto.PaymentResponse;
import com.propertypilot.application.service.PaymentService;

import com.propertypilot.domain.enums.PaymentMethod;
import com.propertypilot.domain.enums.PaymentStatus;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;


import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest
        extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void recordPayment()
            throws Exception {

        UUID invoiceId =
                UUID.randomUUID();

        CreatePaymentRequest request =
                new CreatePaymentRequest();

        request.setAmount(
                new BigDecimal("2500"));

        request.setPaymentMethod(
                PaymentMethod.UPI);

        when(paymentService
                .recordPayment(
                        eq(invoiceId),
                        any()))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        post("/api/payments/invoice/{invoiceId}",
                                invoiceId)
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper
                                                .writeValueAsString(
                                                        request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentStatus")
                        .value("SUCCESS"));
    }

    @Test
    void getPayment()
            throws Exception {

        UUID paymentId =
                UUID.randomUUID();

        when(paymentService
                .getPayment(
                        paymentId))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        get("/api/payments/{paymentId}",
                                paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentStatus")
                        .value("SUCCESS"));
    }

    @Test
    void getPaymentsByInvoice()
            throws Exception {

        UUID invoiceId =
                UUID.randomUUID();

        when(paymentService
                .getPaymentsByInvoice(
                        invoiceId))
                .thenReturn(
                        List.of(buildResponse()));

        mockMvc.perform(
                        get("/api/payments/invoice/{invoiceId}",
                                invoiceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].paymentStatus")
                        .value("SUCCESS"));
    }

    private PaymentResponse
    buildResponse() {

        PaymentResponse response =
                new PaymentResponse();

        response.setPaymentId(
                UUID.randomUUID());

        response.setInvoiceId(
                UUID.randomUUID());

        response.setAmount(
                new BigDecimal("2500"));

        response.setPaymentMethod(
                PaymentMethod.UPI);

        response.setPaymentStatus(
                PaymentStatus.SUCCESS);

        response.setPaymentDate(
                OffsetDateTime.now());

        return response;
    }
}