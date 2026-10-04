package com.propertypilot.web.controller;

import com.propertypilot.application.dto.RevenueDashboardResponse;
import com.propertypilot.application.service.RevenueDashboardService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;

import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RevenueDashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class RevenueDashboardControllerTest
        extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RevenueDashboardService
            revenueDashboardService;

    @Test
    @WithMockUser(
            roles = "ADMIN"
    )
    void getRevenueDashboard()
            throws Exception {

        RevenueDashboardResponse response =
                buildResponse();

        when(
                revenueDashboardService
                        .getRevenueDashboard()
        ).thenReturn(response);

        mockMvc.perform(
                        get("/api/admin/revenue")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.totalInvoices")
                                .value(250)
                )
                .andExpect(
                        jsonPath("$.paidInvoices")
                                .value(200)
                )
                .andExpect(
                        jsonPath("$.pendingInvoices")
                                .value(50)
                );
    }

    private RevenueDashboardResponse
    buildResponse() {

        RevenueDashboardResponse response =
                new RevenueDashboardResponse();

        response.setTotalInvoices(250);
        response.setPaidInvoices(200);
        response.setPendingInvoices(50);

        response.setTotalRevenue(
                new BigDecimal("2500000.00"));

        response.setCollectedRevenue(
                new BigDecimal("2200000.00"));

        response.setOutstandingRevenue(
                new BigDecimal("300000.00"));

        response.setServiceRevenue(
                new BigDecimal("1500000.00"));

        response.setSubscriptionRevenue(
                new BigDecimal("700000.00"));

        response.setDealRevenue(
                new BigDecimal("300000.00"));

        return response;
    }
}