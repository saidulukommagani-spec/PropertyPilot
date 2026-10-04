package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.CreateCustomerSearchHistoryRequest;
import com.propertypilot.application.dto.CustomerSearchHistoryResponse;
import com.propertypilot.application.service.CustomerSearchHistoryService;
import com.propertypilot.security.JwtAuthenticationFilter;
import com.propertypilot.security.JwtService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerSearchHistoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CustomerSearchHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

        @MockitoBean
    private CustomerSearchHistoryService
            customerSearchHistoryService;

        @MockitoBean
    private JwtService jwtService;

        @MockitoBean
    private JwtAuthenticationFilter
            jwtAuthenticationFilter;
    @Test
    @DisplayName(
            "Should create search history successfully")
    void createSearchHistory_success()
            throws Exception {

        UUID customerId = UUID.randomUUID();

        CreateCustomerSearchHistoryRequest
                request =
                new CreateCustomerSearchHistoryRequest();

        request.setSearchType(
                "PROPERTY");
        request.setSearchCriteria(
                "Hyderabad Villas");
        request.setResultCount(
                12);

        CustomerSearchHistoryResponse
                response =
                new CustomerSearchHistoryResponse();

        response.setCustomerSearchHistoryId(
                UUID.randomUUID());
        response.setCustomerId(
                customerId);
        response.setSearchType(
                "PROPERTY");
        response.setSearchCriteria(
                "Hyderabad Villas");
        response.setResultCount(
                12);
        response.setSearchedAt(
                OffsetDateTime.now());

        Mockito.when(
                        customerSearchHistoryService
                                .createSearchHistory(
                                        eq(customerId),
                                        any()))
                .thenReturn(
                        response);

        mockMvc.perform(
                        post(
                                "/api/customers/{customerId}/search-history",
                                customerId)
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request)))
                .andExpect(
                        status().isOk())
                .andExpect(
                        jsonPath(
                                "$.searchType")
                                .value(
                                        "PROPERTY"))
                .andExpect(
                        jsonPath(
                                "$.searchCriteria")
                                .value(
                                        "Hyderabad Villas"));
    }

    @Test
    @DisplayName(
            "Should return customer search history")
    void getCustomerSearchHistory_success()
            throws Exception {

        UUID customerId =
                UUID.randomUUID();

        CustomerSearchHistoryResponse
                response =
                new CustomerSearchHistoryResponse();

        response.setCustomerSearchHistoryId(
                UUID.randomUUID());
        response.setCustomerId(
                customerId);
        response.setSearchType(
                "PROPERTY");
        response.setSearchCriteria(
                "Hyderabad Villas");
        response.setResultCount(
                12);

        Mockito.when(
                        customerSearchHistoryService
                                .getCustomerSearchHistory(
                                        customerId))
                .thenReturn(
                        List.of(
                                response));

        mockMvc.perform(
                        get(
                                "/api/customers/{customerId}/search-history",
                                customerId))
                .andExpect(
                        status().isOk())
                .andExpect(
                        jsonPath(
                                "$[0].searchType")
                                .value(
                                        "PROPERTY"));
    }

    @Test
    @DisplayName(
            "Should return search history by ID")
    void getSearchHistory_success()
            throws Exception {

        UUID searchHistoryId =
                UUID.randomUUID();

        CustomerSearchHistoryResponse
                response =
                new CustomerSearchHistoryResponse();

        response.setCustomerSearchHistoryId(
                searchHistoryId);
        response.setSearchType(
                "PROPERTY");
        response.setSearchCriteria(
                "Hyderabad Villas");
        response.setResultCount(
                12);

        Mockito.when(
                        customerSearchHistoryService
                                .getSearchHistory(
                                        searchHistoryId))
                .thenReturn(
                        response);

        mockMvc.perform(
                        get(
                                "/api/customers/search-history/{searchHistoryId}",
                                searchHistoryId))
                .andExpect(
                        status().isOk())
                .andExpect(
                        jsonPath(
                                "$.searchType")
                                .value(
                                        "PROPERTY"));
    }
}