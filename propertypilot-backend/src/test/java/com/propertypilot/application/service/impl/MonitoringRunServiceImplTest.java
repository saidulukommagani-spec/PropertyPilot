package com.propertypilot.application.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.ServiceRequestResponse;
import com.propertypilot.application.service.MonitoringConfigurationService;
import com.propertypilot.application.service.ServiceRequestService;
import com.propertypilot.infrastructure.persistence.entity.*;
import com.propertypilot.infrastructure.persistence.repository.*;
import com.propertypilot.security.SecurityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MonitoringRunServiceImplTest {
    @Mock MonitoringScheduleRepository schedules;
    @Mock MonitoringRunRepository runs;
    @Mock ServiceRequestRepository serviceRequests;
    @Mock SubscriptionPlanEntitlementRepository entitlements;
    @Mock ServiceRequestService serviceRequestService;
    @Mock MonitoringConfigurationService configuration;
    @Mock SecurityService security;
    @Mock JdbcTemplate jdbcTemplate;
    @Mock TransactionTemplate transactionTemplate;
    @Mock ObjectMapper objectMapper;
    @InjectMocks MonitoringRunServiceImpl service;

    @Test
    void createsOrdinaryRequestAndImmutableRunForDueSubscriptionCycle() throws Exception {
        UUID scheduleId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        UUID planVersionId = UUID.randomUUID();
        OffsetDateTime due = OffsetDateTime.now().minusMinutes(1);
        OffsetDateTime now = OffsetDateTime.now();

        CustomerEntity customer = new CustomerEntity();
        customer.setCustomerId(customerId);
        Property property = new Property();
        property.setPropertyId(UUID.randomUUID());
        property.setCustomer(customer);
        ServiceEntity monitoringService = new ServiceEntity();
        monitoringService.setServiceId(serviceId);
        monitoringService.setServiceCode("MONITOR_QUARTERLY");
        monitoringService.setCategory("MONITORING");
        monitoringService.setActiveFlag(true);
        SubscriptionPlanVersionEntity version = new SubscriptionPlanVersionEntity();
        version.setPlanVersionId(planVersionId);
        CustomerSubscriptionEntity subscription = new CustomerSubscriptionEntity();
        subscription.setCustomer(customer);
        subscription.setPlanVersion(version);
        subscription.setStatus("ACTIVE");
        subscription.setStartDate(LocalDate.now().minusDays(1));

        MonitoringScheduleEntity schedule = new MonitoringScheduleEntity();
        schedule.setMonitoringScheduleId(scheduleId);
        schedule.setCustomerSubscription(subscription);
        schedule.setProperty(property);
        schedule.setService(monitoringService);
        schedule.setFrequency("P3M");
        schedule.setNextDueAt(due);
        schedule.setStatus("ACTIVE");

        SubscriptionPlanEntitlementEntity entitlement = new SubscriptionPlanEntitlementEntity();
        entitlement.setStatus("ACTIVE");
        ServiceRequestResponse response = new ServiceRequestResponse();
        response.setServiceRequestId(requestId);
        ServiceRequestEntity savedRequest = new ServiceRequestEntity();
        savedRequest.setServiceRequestId(requestId);

        when(schedules.findByIdForUpdate(scheduleId)).thenReturn(Optional.of(schedule));
        when(entitlements.findByPlanVersion_PlanVersionIdAndService_ServiceId(planVersionId, serviceId))
                .thenReturn(Optional.of(entitlement));
        when(runs.findTopByMonitoringSchedule_MonitoringScheduleIdOrderByRunNumberDesc(scheduleId))
                .thenReturn(Optional.empty());
        when(runs.existsByMonitoringSchedule_MonitoringScheduleIdAndCycleKey(eq(scheduleId), anyString()))
                .thenReturn(false);
        when(serviceRequestService.createEntitledMonitoringRequest(eq(customer), eq(property), eq(monitoringService), anyString()))
                .thenReturn(response);
        when(serviceRequests.findById(requestId)).thenReturn(Optional.of(savedRequest));
        when(configuration.nextDueAt(any(), eq("P3M"))).thenReturn(due.plusMonths(3));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(runs.saveAndFlush(any(MonitoringRunEntity.class))).thenAnswer(invocation -> {
            MonitoringRunEntity run = invocation.getArgument(0);
            run.setMonitoringRunId(UUID.randomUUID());
            return run;
        });

        var result = service.runScheduleIfDue(scheduleId, now);

        assertEquals(scheduleId, result.scheduleId());
        assertEquals(1, result.runNumber());
        assertEquals(requestId, result.serviceRequestId());
        verify(serviceRequestService).createEntitledMonitoringRequest(eq(customer), eq(property), eq(monitoringService), anyString());
        verify(schedules).save(schedule);
        verify(jdbcTemplate).update(anyString(), any(), anyString());
    }

    @Test
    void oneTimeRequestIsLinkedInsteadOfDuplicated() throws Exception {
        UUID scheduleId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        MonitoringScheduleEntity schedule = new MonitoringScheduleEntity();
        schedule.setMonitoringScheduleId(scheduleId);
        schedule.setService(new ServiceEntity());
        ServiceRequestEntity source = new ServiceRequestEntity();
        source.setServiceRequestId(requestId);
        schedule.setSourceServiceRequest(source);
        when(schedules.findByIdForUpdate(scheduleId)).thenReturn(Optional.of(schedule));
        when(runs.findByMonitoringSchedule_MonitoringScheduleIdOrderByRunNumberAsc(scheduleId)).thenReturn(java.util.List.of());
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(runs.saveAndFlush(any(MonitoringRunEntity.class))).thenAnswer(invocation -> {
            MonitoringRunEntity run = invocation.getArgument(0);
            run.setMonitoringRunId(UUID.randomUUID());
            return run;
        });

        var result = service.createInitialRunFromPurchasedRequest(scheduleId, OffsetDateTime.now());

        assertEquals(requestId, result.serviceRequestId());
        assertEquals(1, result.runNumber());
        verifyNoInteractions(serviceRequestService);
        verify(schedules).save(schedule);
    }
}
