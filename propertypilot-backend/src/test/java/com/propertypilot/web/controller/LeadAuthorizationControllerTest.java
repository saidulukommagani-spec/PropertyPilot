package com.propertypilot.web.controller;

import com.propertypilot.application.dto.LeadActivityResponse;
import com.propertypilot.application.dto.OperationsLeadPageResponse;
import com.propertypilot.application.service.DealService;
import com.propertypilot.application.service.LeadActivityService;
import com.propertypilot.application.service.LeadService;
import com.propertypilot.config.PasswordConfig;
import com.propertypilot.config.SecurityConfig;
import com.propertypilot.security.CustomUserDetailsService;
import com.propertypilot.security.JwtAuthenticationFilter;
import com.propertypilot.security.JwtService;
import com.propertypilot.web.exception.GlobalExceptionHandler;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {LeadController.class, LeadActivityController.class})
@AutoConfigureMockMvc
@Import({SecurityConfig.class, PasswordConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
class LeadAuthorizationControllerTest {
    @Autowired private MockMvc mockMvc;

    @MockitoBean private LeadService leadService;
    @MockitoBean private DealService dealService;
    @MockitoBean private LeadActivityService leadActivityService;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private CustomUserDetailsService customUserDetailsService;

    @Test
    void anonymousCannotReadLeadActivities() throws Exception {
        mockMvc.perform(get("/api/v1/leads/{leadId}/activities", UUID.randomUUID()))
                .andExpect(status().isForbidden());
        verify(leadActivityService, never()).getActivities(any());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerMayCallOwnActivityRouteAndServiceEnforcesLeadOwnership() throws Exception {
        UUID leadId = UUID.randomUUID();
        when(leadActivityService.getActivities(leadId)).thenReturn(List.of(
                new LeadActivityResponse(UUID.randomUUID(), leadId, "NOTE", "Customer update", "Visible details",
                        null, "customer@example.test", OffsetDateTime.now(), null, "CUSTOMER_VISIBLE")));

        mockMvc.perform(get("/api/v1/leads/{leadId}/activities", leadId))
                .andExpect(status().isOk());
        verify(leadActivityService).getActivities(leadId);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerCannotSearchTheOperationsLeadQueue() throws Exception {
        mockMvc.perform(get("/api/v1/leads"))
                .andExpect(status().isForbidden());
        verify(leadService, never()).searchOperationsLeads(any(), any(), any());
    }

    @Test
    @WithMockUser(roles = "AGENT")
    void agentCannotSearchTheOperationsLeadQueue() throws Exception {
        mockMvc.perform(get("/api/v1/leads"))
                .andExpect(status().isForbidden());
        verify(leadService, never()).searchOperationsLeads(any(), any(), any());
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void operationsCanSearchTheLeadQueue() throws Exception {
        when(leadService.searchOperationsLeads(eq(null), eq(null), any()))
                .thenReturn(Page.empty(PageRequest.of(0, 25)));

        mockMvc.perform(get("/api/v1/leads"))
                .andExpect(status().isOk());
        verify(leadService).searchOperationsLeads(eq(null), eq(null), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanSearchTheLeadQueue() throws Exception {
        when(leadService.searchOperationsLeads(eq(null), eq(null), any()))
                .thenReturn(Page.empty(PageRequest.of(0, 25)));

        mockMvc.perform(get("/api/v1/leads"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void activityUpdateAndDeleteAreNotExposed() throws Exception {
        String path = "/api/v1/leads/{leadId}/activities";
        UUID leadId = UUID.randomUUID();
        mockMvc.perform(put(path, leadId)).andExpect(status().isMethodNotAllowed());
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete(path, leadId))
                .andExpect(status().isMethodNotAllowed());
    }
}
