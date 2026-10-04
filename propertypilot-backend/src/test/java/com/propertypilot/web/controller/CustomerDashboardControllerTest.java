package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.CustomerDashboardResponse;
import com.propertypilot.application.service.CustomerDashboardService;
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

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerDashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class CustomerDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerDashboardService customerDashboardService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Get Customer Dashboard")
    void getDashboard_success() throws Exception {

        UUID customerId =
                UUID.randomUUID();

        when(customerDashboardService
                .getDashboard(customerId))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        get("/api/customer-dashboard/{customerId}",
                                customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProperties")
                        .value(5))
                .andExpect(jsonPath("$.activeSubscriptions")
                        .value(2))
                .andExpect(jsonPath("$.totalPayments")
                        .value(25));
    }

    private CustomerDashboardResponse buildResponse() {

        CustomerDashboardResponse response =
                new CustomerDashboardResponse();

        response.setTotalProperties(5);
        response.setTotalServiceRequests(10);
        response.setActiveServiceRequests(3);
        response.setCompletedServiceRequests(7);
        response.setActiveSubscriptions(2);
        response.setPendingInvoices(1);
        response.setPaidInvoices(12);
        response.setTotalPayments(25);

        return response;
    }
}