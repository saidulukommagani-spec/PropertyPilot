package com.propertypilot.application.service.impl;

import com.propertypilot.domain.enums.InvoiceStatus;
import com.propertypilot.domain.enums.PaymentStatus;
import com.propertypilot.infrastructure.persistence.entity.ServiceEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceSlaPolicyEntity;
import com.propertypilot.infrastructure.persistence.repository.*;
import com.propertypilot.security.SecurityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperationsReportingServiceImplTest {
    @Mock ServiceRequestRepository requests;
    @Mock InvoiceRepository invoices;
    @Mock PaymentRepository payments;
    @Mock MonitoringScheduleRepository schedules;
    @Mock MonitoringAlertRepository alerts;
    @Mock ServiceRequestStatusHistoryRepository history;
    @Mock ServiceSlaPolicyRepository slaPolicies;
    @Mock SecurityService securityService;
    @InjectMocks OperationsReportingServiceImpl service;

    @Test
    void operationsDashboardCombinesOperationalAndFinancialExceptions() {
        when(requests.count()).thenReturn(10L);
        when(requests.countByStatus("NEW")).thenReturn(2L);
        when(requests.countByStatus("PENDING_ASSIGNMENT")).thenReturn(1L);
        when(requests.countByStatus("ASSIGNED")).thenReturn(1L);
        when(requests.countByStatus("ACCEPTED")).thenReturn(1L);
        when(requests.countByStatus("IN_PROGRESS")).thenReturn(2L);
        when(requests.countByStatus("COMPLETED")).thenReturn(3L);
        when(requests.countByStatus("FAILED")).thenReturn(1L);
        when(invoices.countByStatus(InvoiceStatus.OVERDUE)).thenReturn(4L);
        when(payments.countByPaymentStatus(PaymentStatus.FAILED)).thenReturn(2L);

        var response = service.getOperationsDashboard();

        assertThat(response.unassignedRequests()).isEqualTo(3);
        assertThat(response.activeRequests()).isEqualTo(4);
        assertThat(response.overdueInvoices()).isEqualTo(4);
        assertThat(response.failedPayments()).isEqualTo(2);
    }

    @Test
    void monitoringDashboardSeparatesDueWorkAndCriticalOpenAlerts() {
        when(schedules.countByStatusIgnoreCase("ACTIVE")).thenReturn(8L);
        when(schedules.countByStatusIgnoreCaseAndNextDueAtBefore(
                org.mockito.ArgumentMatchers.eq("ACTIVE"), org.mockito.ArgumentMatchers.any())).thenReturn(3L);
        when(alerts.countByStatusIn(List.of("OPEN", "ACKNOWLEDGED"))).thenReturn(5L);
        when(alerts.countBySeverityIgnoreCaseAndStatusIn("CRITICAL", List.of("OPEN", "ACKNOWLEDGED")))
                .thenReturn(1L);

        var response = service.getMonitoringDashboard();

        assertThat(response.activeSchedules()).isEqualTo(8);
        assertThat(response.dueSchedules()).isEqualTo(3);
        assertThat(response.unresolvedAlerts()).isEqualTo(5);
        assertThat(response.criticalUnresolvedAlerts()).isEqualTo(1);
    }

    @Test
    void slaReportFlagsAcknowledgementBreachFromConfiguredPolicy() {
        UUID serviceId = UUID.randomUUID();
        ServiceEntity configuredService = new ServiceEntity();
        configuredService.setServiceId(serviceId);
        configuredService.setServiceName("Inspection");
        ServiceSlaPolicyEntity policy = new ServiceSlaPolicyEntity();
        policy.setService(configuredService);
        policy.setAcknowledgementMinutes(10);
        policy.setCompletionMinutes(180);
        policy.setStatus("ACTIVE");

        ServiceRequestEntity request = new ServiceRequestEntity();
        request.setServiceRequestId(UUID.randomUUID());
        request.setService(configuredService);
        request.setStatus("PENDING_ASSIGNMENT");
        request.setRequestedAt(Instant.now().minusSeconds(3600));

        when(slaPolicies.findByStatusIgnoreCaseOrderByCreatedAtDesc("ACTIVE"))
                .thenReturn(List.of(policy));
        when(requests.findAll()).thenReturn(List.of(request));

        var response = service.getSlaReport();

        assertThat(response.evaluatedRequests()).isEqualTo(1);
        assertThat(response.breachedRequests()).isEqualTo(1);
        assertThat(response.requests().getFirst().slaState()).isEqualTo("BREACHED");
    }
}
