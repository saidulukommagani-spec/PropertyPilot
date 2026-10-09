package com.propertypilot.application.service;

import com.propertypilot.application.dto.AuditReportRow;
import com.propertypilot.application.dto.MonitoringDashboardResponse;
import com.propertypilot.application.dto.OperationsDashboardResponse;
import com.propertypilot.application.dto.SlaReportResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public interface OperationsReportingService {
    OperationsDashboardResponse getOperationsDashboard();
    MonitoringDashboardResponse getMonitoringDashboard();
    Page<AuditReportRow> getServiceRequestAudit(Instant from, Instant to, Pageable pageable);
    SlaReportResponse getSlaReport();
}
