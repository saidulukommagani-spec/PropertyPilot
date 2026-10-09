package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.*;
import com.propertypilot.application.service.DealService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DealController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class DealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DealService dealService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Create Deal")
    void createDeal_success() throws Exception {

        when(dealService.createDeal(any()))
                .thenReturn(buildDealResponse());

        mockMvc.perform(
                        post("/api/v1/deals")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(validCreateDealRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dealStatus")
                        .value("OPEN"));
    }

    @Test
    void createDeal_rejectsInvalidCommissionBeforeCallingService() throws Exception {
        CreateDealRequest request = validCreateDealRequest();
        request.setBuyerCommissionPercent(new BigDecimal("100.01"));

        mockMvc.perform(post("/api/v1/deals")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(dealService, never()).createDeal(any());
    }

    @Test
    @DisplayName("Get Deal")
    void getDeal_success() throws Exception {

        UUID dealId = UUID.randomUUID();

        when(dealService.getDeal(dealId))
                .thenReturn(buildDealResponse());

        mockMvc.perform(
                        get("/api/v1/deals/{id}", dealId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dealStatus")
                        .value("OPEN"));
    }

    @Test
    @DisplayName("Get All Deals")
    void getAllDeals_success() throws Exception {

        when(dealService.getAllDeals())
                .thenReturn(List.of(buildDealResponse()));

        mockMvc.perform(
                        get("/api/v1/deals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dealStatus")
                        .value("OPEN"));
    }

    @Test
    @DisplayName("Add Participant")
    void addParticipant_success() throws Exception {

        UUID dealId = UUID.randomUUID();

        when(dealService.addParticipant(
                eq(dealId),
                any(AddDealParticipantRequest.class)))
                .thenReturn(buildParticipantResponse());

        mockMvc.perform(
                        post("/api/v1/deals/{id}/participants",
                                dealId)
                                .contentType("application/json")
                                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.participantRole")
                        .value("BUYER"));
    }

    @Test
    @DisplayName("Get Participants")
    void getParticipants_success() throws Exception {

        UUID dealId = UUID.randomUUID();

        when(dealService.getParticipants(dealId))
                .thenReturn(
                        List.of(buildParticipantResponse()));

        mockMvc.perform(
                        get("/api/v1/deals/{id}/participants",
                                dealId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].participantRole")
                        .value("BUYER"));
    }

    @Test
    @DisplayName("Select Buyer")
    void selectBuyer_success() throws Exception {

        UUID dealId = UUID.randomUUID();
        UUID participantId = UUID.randomUUID();

        when(dealService.selectBuyer(
                dealId,
                participantId))
                .thenReturn(buildParticipantResponse());

        mockMvc.perform(
                        post("/api/v1/deals/{dealId}/participants/{participantId}/select",
                                dealId,
                                participantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.participantRole")
                        .value("BUYER"));
    }

    @Test
    @DisplayName("Update Deal Status")
    void updateDealStatus_success() throws Exception {

        UUID dealId = UUID.randomUUID();

        when(dealService.updateDealStatus(
                eq(dealId),
                any(UpdateDealStatusRequest.class)))
                .thenReturn(buildDealResponse());

        mockMvc.perform(
                        put("/api/v1/deals/{dealId}/status",
                                dealId)
                                .contentType("application/json")
                                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dealStatus")
                        .value("OPEN"));
    }

    @Test
    @DisplayName("Finalize Deal")
    void finalizeDeal_success() throws Exception {

        UUID dealId = UUID.randomUUID();

        when(dealService.finalizeDeal(
                eq(dealId),
                any(FinalizeDealRequest.class)))
                .thenReturn(buildDealResponse());

        mockMvc.perform(
                        post("/api/v1/deals/{dealId}/finalize",
                                dealId)
                                .contentType("application/json")
                                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Cancel Deal")
    void cancelDeal_success() throws Exception {

        UUID dealId = UUID.randomUUID();

        when(dealService.cancelDeal(
                dealId,
                "Customer cancelled"))
                .thenReturn(buildDealResponse());

        mockMvc.perform(
                        post("/api/v1/deals/{dealId}/cancel",
                                dealId)
                                .param(
                                        "reason",
                                        "Customer cancelled"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Complete Deal")
    void completeDeal_success() throws Exception {

        UUID dealId = UUID.randomUUID();

        when(dealService.completeDeal(
                eq(dealId),
                any(BigDecimal.class)))
                .thenReturn(buildDealResponse());

        mockMvc.perform(
                        put("/api/v1/deals/{dealId}/complete",
                                dealId)
                                .contentType("application/json")
                                .content("""
                                        {
                                          "finalDealAmount":1000000
                                        }
                                        """))
                .andExpect(status().isOk());
    }

    private DealResponse buildDealResponse() {

        DealResponse response =
                new DealResponse();

        response.setDealId(UUID.randomUUID());
        response.setDealStatus("OPEN");
        response.setExpectedAmount(
                BigDecimal.valueOf(1000000));

        return response;
    }

    private CreateDealRequest validCreateDealRequest() {
        CreateDealRequest request = new CreateDealRequest();
        request.setPropertyId(UUID.randomUUID());
        request.setSellerCustomerId(UUID.randomUUID());
        request.setDealType("PROPERTY_SALE");
        request.setExpectedAmount(new BigDecimal("100000.00"));
        request.setSellerCommissionPercent(new BigDecimal("2.50"));
        request.setBuyerCommissionPercent(new BigDecimal("2.50"));
        request.setRemarks("Property transaction deal");
        return request;
    }

    private DealParticipantResponse buildParticipantResponse() {

        DealParticipantResponse response =
                new DealParticipantResponse();

        response.setParticipantId(
                UUID.randomUUID());

        response.setParticipantRole("BUYER");

        return response;
    }
}
