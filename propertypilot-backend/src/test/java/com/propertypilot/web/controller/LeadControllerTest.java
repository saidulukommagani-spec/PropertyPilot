package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.LeadAssignRequest;
import com.propertypilot.application.dto.LeadAssignmentResponse;
import com.propertypilot.application.dto.LeadCreateRequest;
import com.propertypilot.application.dto.LeadResponse;
import com.propertypilot.application.service.LeadService;
import com.propertypilot.application.service.DealService;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import com.propertypilot.application.dto.OperationsLeadResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.mockito.ArgumentCaptor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private DealService dealService;

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
    @DisplayName("Operations lead search is paged and returns customer-facing context only")
    void searchOperationsLeads_success() throws Exception {
        OperationsLeadResponse row = new OperationsLeadResponse(UUID.randomUUID(), "Website", "Apartment",
                "Hyderabad", "NEW", "Test Customer", "customer@example.test", "9000000000", "AGT-1",
                OffsetDateTime.now());
        when(leadService.searchOperationsLeads(eq("Customer"), eq(LeadStatus.NEW), any()))
                .thenReturn(new PageImpl<>(List.of(row), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/leads")
                        .param("search", "Customer")
                        .param("status", "NEW")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].customerName").value("Test Customer"))
                .andExpect(jsonPath("$.content[0].customerEmail").value("customer@example.test"))
                .andExpect(jsonPath("$.content[0].notes").doesNotExist())
                .andExpect(jsonPath("$.content[0].activityDetails").doesNotExist())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("Operations lead search rejects unsupported sort fields")
    void searchOperationsLeads_rejectsUnsupportedSort() throws Exception {
        mockMvc.perform(get("/api/v1/leads").param("sort", "notes"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Operations lead search applies bounded defaults and deterministic sorting")
    void searchOperationsLeads_usesSafeDefaultsAndStableSort() throws Exception {
        when(leadService.searchOperationsLeads(eq(null), eq(null), any()))
                .thenReturn(Page.empty(PageRequest.of(0, 25)));

        mockMvc.perform(get("/api/v1/leads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(25))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));

        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(leadService).searchOperationsLeads(eq(null), eq(null), pageable.capture());
        assertEquals(25, pageable.getValue().getPageSize());
        assertEquals(0, pageable.getValue().getPageNumber());
        assertEquals("createdAt", pageable.getValue().getSort().getOrderFor("createdAt").getProperty());
        assertEquals(org.springframework.data.domain.Sort.Direction.DESC,
                pageable.getValue().getSort().getOrderFor("createdAt").getDirection());
        assertEquals("leadId", pageable.getValue().getSort().getOrderFor("leadId").getProperty());
    }

    @Test
    void searchOperationsLeads_rejectsInvalidPaginationAndOversizedSearch() throws Exception {
        mockMvc.perform(get("/api/v1/leads").param("page", "-1"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/v1/leads").param("size", "101"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/v1/leads").param("search", "x".repeat(201)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchOperationsLeads_passesStatusAndSortFiltersToService() throws Exception {
        when(leadService.searchOperationsLeads(eq("Apartment"), eq(LeadStatus.NEW), any()))
                .thenReturn(Page.empty(PageRequest.of(1, 50)));
        mockMvc.perform(get("/api/v1/leads")
                        .param("search", "Apartment")
                        .param("status", "NEW")
                        .param("page", "1")
                        .param("size", "50")
                        .param("sort", "status")
                        .param("direction", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(50));
        verify(leadService).searchOperationsLeads(eq("Apartment"), eq(LeadStatus.NEW), any());
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
                "AGT001",
                UUID.randomUUID()
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
