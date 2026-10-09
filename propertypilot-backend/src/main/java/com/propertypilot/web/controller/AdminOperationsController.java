package com.propertypilot.web.controller;

import com.propertypilot.application.dto.AuditReportRow;
import com.propertypilot.application.dto.MonitoringDashboardResponse;
import com.propertypilot.application.dto.OperationsDashboardResponse;
import com.propertypilot.application.dto.SlaReportResponse;
import com.propertypilot.application.service.OperationsReportingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/admin/operations")
@PreAuthorize("hasRole('ADMIN') or hasRole('OPERATIONS')")
public class AdminOperationsController {
    private final OperationsReportingService reportingService;

    public AdminOperationsController(OperationsReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @GetMapping("/dashboard")
    public OperationsDashboardResponse dashboard() {
        return reportingService.getOperationsDashboard();
    }

    @GetMapping("/monitoring")
    public MonitoringDashboardResponse monitoring() {
        return reportingService.getMonitoringDashboard();
    }

    @GetMapping("/audit/service-requests")
    public Page<AuditReportRow> serviceRequestAudit(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        return reportingService.getServiceRequestAudit(from, to,
                PageRequest.of(Math.max(0, page), Math.max(1, Math.min(size, 500))));
    }

    @GetMapping("/sla")
    public SlaReportResponse slaReport() {
        return reportingService.getSlaReport();
    }
}
