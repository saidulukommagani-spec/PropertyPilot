package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.*;
import com.propertypilot.application.service.OperationsReportingService;
import com.propertypilot.domain.enums.InvoiceStatus;
import com.propertypilot.domain.enums.PaymentStatus;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceSlaPolicyEntity;
import com.propertypilot.infrastructure.persistence.repository.*;
import com.propertypilot.security.SecurityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OperationsReportingServiceImpl implements OperationsReportingService {
    private static final List<String> UNRESOLVED_ALERT_STATUSES = List.of("OPEN", "ACKNOWLEDGED");
    private static final List<String> ACTIVE_REQUEST_STATUSES = List.of(
            "NEW", "PENDING_PAYMENT", "PAYMENT_COMPLETED", "PENDING_ASSIGNMENT",
            "ASSIGNED", "ACCEPTED", "IN_PROGRESS", "REPORT_SUBMITTED", "UNDER_REVIEW", "ESCALATED", "COMPLETED");

    private final ServiceRequestRepository requests;
    private final InvoiceRepository invoices;
    private final PaymentRepository payments;
    private final MonitoringScheduleRepository schedules;
    private final MonitoringAlertRepository alerts;
    private final ServiceRequestStatusHistoryRepository history;
    private final ServiceSlaPolicyRepository slaPolicies;
    private final SecurityService securityService;

    public OperationsReportingServiceImpl(ServiceRequestRepository requests,
                                          InvoiceRepository invoices,
                                          PaymentRepository payments,
                                          MonitoringScheduleRepository schedules,
                                          MonitoringAlertRepository alerts,
                                          ServiceRequestStatusHistoryRepository history,
                                          ServiceSlaPolicyRepository slaPolicies,
                                          SecurityService securityService) {
        this.requests = requests;
        this.invoices = invoices;
        this.payments = payments;
        this.schedules = schedules;
        this.alerts = alerts;
        this.history = history;
        this.slaPolicies = slaPolicies;
        this.securityService = securityService;
    }

    @Override
    public OperationsDashboardResponse getOperationsDashboard() {
        securityService.validateOperationsAccess();
        long unassigned = requests.countByStatus("NEW") + requests.countByStatus("PENDING_ASSIGNMENT");
        long active = requests.countByStatus("ASSIGNED") + requests.countByStatus("ACCEPTED")
                + requests.countByStatus("IN_PROGRESS");
        return new OperationsDashboardResponse(requests.count(), unassigned, active,
                requests.countByStatus("COMPLETED"), requests.countByStatus("FAILED"),
                invoices.countByStatus(InvoiceStatus.OVERDUE), payments.countByPaymentStatus(PaymentStatus.FAILED));
    }

    @Override
    public MonitoringDashboardResponse getMonitoringDashboard() {
        securityService.validateOperationsAccess();
        OffsetDateTime now = OffsetDateTime.now();
        long openAlerts = alerts.countByStatusIn(UNRESOLVED_ALERT_STATUSES);
        return new MonitoringDashboardResponse(
                schedules.countByStatusIgnoreCase("ACTIVE"),
                schedules.countByStatusIgnoreCaseAndNextDueAtBefore("ACTIVE", now),
                openAlerts,
                alerts.countBySeverityIgnoreCaseAndStatusIn("CRITICAL", UNRESOLVED_ALERT_STATUSES));
    }

    @Override
    public Page<AuditReportRow> getServiceRequestAudit(Instant from, Instant to, Pageable pageable) {
        securityService.validateOperationsAccess();
        if (from == null || to == null || from.isAfter(to)) {
            throw new IllegalArgumentException("A valid audit time range is required");
        }
        return history.findByChangedAtBetweenOrderByChangedAtDesc(from, to, pageable)
                .map(event -> new AuditReportRow(event.getStatusHistoryId(),
                        event.getServiceRequest().getServiceRequestId(),
                        event.getServiceRequest().getProperty().getPropertyId(),
                        event.getPreviousStatus(), event.getNewStatus(), event.getChangeReason(),
                        event.getCreatedBy(), event.getChangedAt()));
    }

    @Override
    public SlaReportResponse getSlaReport() {
        securityService.validateOperationsAccess();
        Map<UUID, ServiceSlaPolicyEntity> policyByService = slaPolicies.findByStatusIgnoreCaseOrderByCreatedAtDesc("ACTIVE")
                .stream().filter(policy -> policy.getService() != null)
                .collect(Collectors.toMap(policy -> policy.getService().getServiceId(), policy -> policy,
                        (first, ignored) -> first));
        Instant now = Instant.now();
        List<SlaReportRow> rows = new ArrayList<>();
        for (ServiceRequestEntity request : requests.findAll()) {
            if (request.getService() == null || !ACTIVE_REQUEST_STATUSES.contains(request.getStatus())) continue;
            ServiceSlaPolicyEntity policy = policyByService.get(request.getService().getServiceId());
            if (policy == null || request.getRequestedAt() == null) continue;
            Instant acknowledgementDue = request.getRequestedAt().plusSeconds(policy.getAcknowledgementMinutes() * 60L);
            Instant completionDue = request.getRequestedAt().plusSeconds(policy.getCompletionMinutes() * 60L);
            boolean ackPending = request.getAssignedAt() == null;
            boolean ackBreached = ackPending ? now.isAfter(acknowledgementDue) : request.getAssignedAt().isAfter(acknowledgementDue);
            Instant completionObservedAt = request.getCompletedAt() == null ? now : request.getCompletedAt();
            boolean completionPending = request.getCompletedAt() == null;
            boolean completionBreached = completionObservedAt.isAfter(completionDue);
            boolean atRisk = !ackBreached && ackPending && nearDeadline(now, acknowledgementDue)
                    || completionPending && !completionBreached && nearDeadline(now, completionDue);
            String state = ackBreached || completionBreached ? "BREACHED" : atRisk ? "AT_RISK" : "ON_TRACK";
            long overdueMinutes = Math.max(ackBreached
                            ? minutesLate(ackPending ? now : request.getAssignedAt(), acknowledgementDue) : 0,
                    completionBreached ? minutesLate(completionObservedAt, completionDue) : 0);
            rows.add(new SlaReportRow(request.getServiceRequestId(), request.getService().getServiceName(),
                    request.getStatus(), state, acknowledgementDue, completionDue, overdueMinutes));
        }
        long atRisk = rows.stream().filter(row -> "AT_RISK".equals(row.slaState())).count();
        long breached = rows.stream().filter(row -> "BREACHED".equals(row.slaState())).count();
        rows.sort(Comparator.comparingInt((SlaReportRow row) ->
                "BREACHED".equals(row.slaState()) ? 0 : "AT_RISK".equals(row.slaState()) ? 1 : 2)
                .thenComparing(SlaReportRow::minutesOverdue, Comparator.reverseOrder()));
        return new SlaReportResponse(rows.size(), atRisk, breached, List.copyOf(rows));
    }

    private boolean nearDeadline(Instant now, Instant deadline) {
        Duration remaining = Duration.between(now, deadline);
        return !remaining.isNegative() && remaining.compareTo(Duration.ofHours(4)) <= 0;
    }

    private long minutesLate(Instant now, Instant deadline) {
        return Math.max(1, Duration.between(deadline, now).toMinutes());
    }
}
