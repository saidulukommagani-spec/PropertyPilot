package com.propertypilot.web.controller;

import com.propertypilot.application.dto.InvoiceResponse;
import com.propertypilot.application.service.BillingService;
import com.propertypilot.domain.enums.BillingEntityType;
import com.propertypilot.domain.enums.InvoiceStatus;
import com.propertypilot.domain.enums.InvoiceType;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BillingController.class)
@AutoConfigureMockMvc(addFilters = false)
class BillingControllerTest
        extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BillingService billingService;

    @Test
    void getInvoice()
            throws Exception {

        UUID invoiceId =
                UUID.randomUUID();

        when(billingService.getInvoice(invoiceId))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        get("/api/v1/billing/invoices/{invoiceId}",
                                invoiceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invoiceNumber")
                        .value("INV-001"));
    }

    @Test
    void getAllInvoices()
            throws Exception {

        when(billingService.getAllInvoices())
                .thenReturn(
                        List.of(buildResponse()));

        mockMvc.perform(
                        get("/api/v1/billing/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].invoiceNumber")
                        .value("INV-001"));
    }

    @Test
    void getCustomerInvoices()
            throws Exception {

        UUID customerId =
                UUID.randomUUID();

        when(billingService
                .getInvoicesByCustomer(customerId))
                .thenReturn(
                        List.of(buildResponse()));

        mockMvc.perform(
                        get("/api/v1/billing/customers/{customerId}/invoices",
                                customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].invoiceNumber")
                        .value("INV-001"));
    }

    @Test
    void getEntityInvoices()
            throws Exception {

        UUID entityId =
                UUID.randomUUID();

        when(billingService
                .getInvoicesByEntity(
                        BillingEntityType.SUBSCRIPTION,
                        entityId))
                .thenReturn(
                        List.of(buildResponse()));

        mockMvc.perform(
                        get("/api/v1/billing/invoices/entity/{entityType}/{entityId}",
                                BillingEntityType.SUBSCRIPTION,
                                entityId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].invoiceNumber")
                        .value("INV-001"));
    }

    private InvoiceResponse
    buildResponse() {

        InvoiceResponse response =
                new InvoiceResponse();

        response.setInvoiceId(
                UUID.randomUUID());

        response.setCustomerId(
                UUID.randomUUID());

        response.setInvoiceNumber(
                "INV-001");

        response.setEntityType(
                BillingEntityType.SUBSCRIPTION);

        response.setInvoiceType(
                InvoiceType.SUBSCRIPTION_FEE);

        response.setInvoiceAmount(
                new BigDecimal("1000"));

        response.setTaxAmount(
                new BigDecimal("180"));

        response.setStatus(
                InvoiceStatus.PAID);

        response.setIssuedAt(
                OffsetDateTime.now());

        return response;
    }
}