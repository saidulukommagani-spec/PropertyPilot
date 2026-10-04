package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.LeadAssignRequest;
import com.propertypilot.application.dto.LeadAssignmentResponse;
import com.propertypilot.application.dto.LeadCreateRequest;
import com.propertypilot.application.dto.LeadResponse;
import com.propertypilot.application.service.LeadService;
import com.propertypilot.domain.enums.LeadStatus;
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
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LeadController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class LeadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LeadService leadService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Create Lead")
    void createLead_success() throws Exception {

        when(leadService.createLead(
                any(LeadCreateRequest.class)))
                .thenReturn(buildLeadResponse());

        mockMvc.perform(
                        post("/api/v1/leads")
                                .contentType("application/json")
                                .content("""
                                        {
                                          "source":"Website",
                                          "propertyType":"Apartment",
                                          "location":"Hyderabad",
                                          "budgetMin":1000000,
                                          "budgetMax":2000000,
                                          "notes":"Interested buyer"
                                        }
                                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status")
                        .value("NEW"));
    }

    @Test
    @DisplayName("Get Lead")
    void getLead_success() throws Exception {

        UUID leadId = UUID.randomUUID();

        when(leadService.getLead(
                leadId))
                .thenReturn(buildLeadResponse());

        mockMvc.perform(
                        get("/api/v1/leads/{leadId}",
                                leadId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("NEW"));
    }

    @Test
    @DisplayName("Update Lead Status")
    void updateLeadStatus_success() throws Exception {

        UUID leadId = UUID.randomUUID();

        when(leadService.updateLeadStatus(
                leadId,
                LeadStatus.CONTACTED))
                .thenReturn(buildLeadResponse());

        mockMvc.perform(
                        patch("/api/v1/leads/{leadId}/status",
                                leadId)
                                .param(
                                        "status",
                                        "CONTACTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("NEW"));
    }

    @Test
    @DisplayName("Get My Leads")
    void getMyLeads_success() throws Exception {

        when(leadService.getMyLeads())
                .thenReturn(
                        List.of(
                                buildLeadResponse()));

        mockMvc.perform(
                        get("/api/v1/leads/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status")
                        .value("NEW"));
    }

    @Test
    @DisplayName("Assign Lead")
    void assignLead_success() throws Exception {

        UUID leadId = UUID.randomUUID();

        when(leadService.assignLead(
                eq(leadId),
                any(LeadAssignRequest.class)))
                .thenReturn(
                        buildAssignmentResponse());

        mockMvc.perform(
                        patch("/api/v1/leads/{leadId}/assign",
                                leadId)
                                .contentType("application/json")
                                .content("""
                                        {
                                          "agentId":"11111111-1111-1111-1111-111111111111"
                                        }
                                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("ASSIGNED"));
    }

    private LeadResponse buildLeadResponse() {

        return new LeadResponse(
                UUID.randomUUID(),
                "Website",
                "Apartment",
                "Hyderabad",
                "NEW",
                BigDecimal.valueOf(1000000),
                BigDecimal.valueOf(2000000),
                "Interested buyer",
                OffsetDateTime.now(),
                null,
                UUID.randomUUID(),
                "AGT001"
        );
    }

    private LeadAssignmentResponse buildAssignmentResponse() {

        return new LeadAssignmentResponse(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "AGT001",
                "ASSIGNED"
        );
    }
}