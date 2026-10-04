package com.propertypilot.web.controller;

import com.propertypilot.application.dto.AnalyticsDashboardResponse;
import com.propertypilot.application.service.AnalyticsDashboardService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        AnalyticsDashboardController.class
)
@AutoConfigureMockMvc(
        addFilters = false
)
class AnalyticsDashboardControllerTest
        extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnalyticsDashboardService
            analyticsDashboardService;

    @Test
    void getAnalyticsDashboard()
            throws Exception {

        AnalyticsDashboardResponse response =
                new AnalyticsDashboardResponse();

        response.setTotalCustomers(100);
        response.setTotalProperties(250);
        response.setTotalLeads(80);
        response.setTotalDeals(30);
        response.setTotalServiceRequests(150);
        response.setActiveSubscriptions(75);

        when(
                analyticsDashboardService
                        .getAnalyticsDashboard())
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/admin/analytics"))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.totalCustomers")
                                .value(100))
                .andExpect(
                        jsonPath("$.totalProperties")
                                .value(250))
                .andExpect(
                        jsonPath("$.totalLeads")
                                .value(80))
                .andExpect(
                        jsonPath("$.totalDeals")
                                .value(30))
                .andExpect(
                        jsonPath("$.totalServiceRequests")
                                .value(150))
                .andExpect(
                        jsonPath("$.activeSubscriptions")
                                .value(75));
    }
}