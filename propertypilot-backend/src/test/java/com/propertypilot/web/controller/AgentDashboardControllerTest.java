package com.propertypilot.web.controller;

import com.propertypilot.application.dto.AgentDashboardResponse;
import com.propertypilot.application.service.AgentDashboardService;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AgentDashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AgentDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AgentDashboardService agentDashboardService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Get Agent Dashboard")
    void getDashboard_success() throws Exception {

        when(agentDashboardService.getDashboard())
                .thenReturn(buildResponse());

        mockMvc.perform(
                        get("/api/admin/agents/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAgents")
                        .value(100))
                .andExpect(jsonPath("$.activeAgents")
                        .value(80))
                .andExpect(jsonPath("$.assignedAgents")
                        .value(60))
                .andExpect(jsonPath("$.completedAssignments")
                        .value(500))
                .andExpect(jsonPath("$.rejectedAssignments")
                        .value(10))
                .andExpect(jsonPath("$.activeAssignments")
                        .value(45));
    }

    private AgentDashboardResponse buildResponse() {

        AgentDashboardResponse response =
                new AgentDashboardResponse();

        response.setTotalAgents(100);
        response.setActiveAgents(80);
        response.setAssignedAgents(60);
        response.setCompletedAssignments(500);
        response.setRejectedAssignments(10);
        response.setActiveAssignments(45);

        return response;
    }
}