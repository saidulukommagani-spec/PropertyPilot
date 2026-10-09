package com.propertypilot.web.controller;

import com.propertypilot.application.dto.AuditReportRow;
import com.propertypilot.application.dto.MonitoringDashboardResponse;
import com.propertypilot.application.dto.OperationsDashboardResponse;
import com.propertypilot.application.dto.SlaReportResponse;
import com.propertypilot.application.service.OperationsReportingService;
import com.propertypilot.security.CustomUserDetailsService;
import com.propertypilot.security.JwtService;
import com.propertypilot.web.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminOperationsController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AdminOperationsControllerTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean OperationsReportingService reportingService;
    @MockitoBean JwtService jwtService;
    @MockitoBean CustomUserDetailsService customUserDetailsService;

    @Test
    void exposesOperationsAndMonitoringDashboards() throws Exception {
        when(reportingService.getOperationsDashboard())
                .thenReturn(new OperationsDashboardResponse(10, 2, 4, 3, 1, 2, 1));
        when(reportingService.getMonitoringDashboard())
                .thenReturn(new MonitoringDashboardResponse(5, 1, 2, 1));

        mockMvc.perform(get("/api/admin/operations/dashboard"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.unassignedRequests").value(2));
        mockMvc.perform(get("/api/admin/operations/monitoring"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.criticalUnresolvedAlerts").value(1));
    }

    @Test
    void exposesPagedServiceRequestAuditAndSlaReport() throws Exception {
        AuditReportRow event = new AuditReportRow(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "ASSIGNED", "IN_PROGRESS", "Work started", UUID.randomUUID(), Instant.parse("2026-10-01T00:00:00Z"));
        when(reportingService.getServiceRequestAudit(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(event)));
        when(reportingService.getSlaReport()).thenReturn(new SlaReportResponse(0, 0, 0, List.of()));

        mockMvc.perform(get("/api/admin/operations/audit/service-requests")
                        .param("from", "2026-10-01T00:00:00Z")
                        .param("to", "2026-10-02T00:00:00Z"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].newStatus").value("IN_PROGRESS"));
        mockMvc.perform(get("/api/admin/operations/sla"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.evaluatedRequests").value(0));
    }
}
