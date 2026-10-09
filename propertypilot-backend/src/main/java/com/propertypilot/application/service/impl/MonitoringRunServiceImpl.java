package com.propertypilot.application.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.MonitoringRunResponse;
import com.propertypilot.application.dto.ServiceRequestResponse;
import com.propertypilot.application.service.MonitoringConfigurationService;
import com.propertypilot.application.service.MonitoringRunService;
import com.propertypilot.application.service.ServiceRequestService;
import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionEntity;
import com.propertypilot.infrastructure.persistence.entity.MonitoringRunEntity;
import com.propertypilot.infrastructure.persistence.entity.MonitoringScheduleEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;
import com.propertypilot.infrastructure.persistence.repository.MonitoringRunRepository;
import com.propertypilot.infrastructure.persistence.repository.MonitoringScheduleRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionPlanEntitlementRepository;
import com.propertypilot.security.SecurityService;
import com.propertypilot.web.exception.BusinessException;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class MonitoringRunServiceImpl implements MonitoringRunService {
    private static final Logger log = LoggerFactory.getLogger(MonitoringRunServiceImpl.class);
    private final MonitoringScheduleRepository schedules;
    private final MonitoringRunRepository runs;
    private final ServiceRequestRepository serviceRequests;
    private final SubscriptionPlanEntitlementRepository entitlements;
    private final ServiceRequestService serviceRequestService;
    private final MonitoringConfigurationService configuration;
    private final SecurityService security;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final TransactionTemplate transactionTemplate;

    public MonitoringRunServiceImpl(MonitoringScheduleRepository schedules, MonitoringRunRepository runs,
            ServiceRequestRepository serviceRequests, SubscriptionPlanEntitlementRepository entitlements,
            ServiceRequestService serviceRequestService, MonitoringConfigurationService configuration,
            SecurityService security, JdbcTemplate jdbcTemplate, ObjectMapper objectMapper,
            TransactionTemplate transactionTemplate) {
        this.schedules = schedules;
        this.runs = runs;
        this.serviceRequests = serviceRequests;
        this.entitlements = entitlements;
        this.serviceRequestService = serviceRequestService;
        this.configuration = configuration;
        this.security = security;
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    @Transactional
    public MonitoringRunResponse runScheduleIfDue(UUID scheduleId, OffsetDateTime now) {
        MonitoringScheduleEntity schedule = lockSchedule(scheduleId);
        if (!"ACTIVE".equalsIgnoreCase(schedule.getStatus()) || schedule.getNextDueAt().isAfter(now)) {
            return null;
        }
        if (schedule.getCustomerSubscription() == null) {
            // A one-time request funds its linked run only; it is never silently reused as recurring entitlement.
            schedule.setStatus("COMPLETED");
            schedule.setLastGeneratedAt(now);
            schedules.save(schedule);
            return null;
        }
        CustomerSubscriptionEntity subscription = schedule.getCustomerSubscription();
        validateEntitlement(subscription, schedule);

        OffsetDateTime scheduledFor = schedule.getNextDueAt();
        var previousRun = runs.findTopByMonitoringSchedule_MonitoringScheduleIdOrderByRunNumberDesc(scheduleId);
        if (previousRun.isPresent()
                && !"COMPLETED".equalsIgnoreCase(previousRun.get().getServiceRequest().getStatus())) {
            return null;
        }
        int runNumber = previousRun.map(run -> run.getRunNumber() + 1).orElse(1);
        String cycleKey = scheduledFor.toInstant().toString();
        if (runs.existsByMonitoringSchedule_MonitoringScheduleIdAndCycleKey(scheduleId, cycleKey)) {
            return null;
        }

        ServiceRequestResponse response = serviceRequestService.createEntitledMonitoringRequest(
                subscription.getCustomer(), schedule.getProperty(), schedule.getService(),
                "Generated from Monitoring schedule " + scheduleId + " cycle " + runNumber);
        ServiceRequestEntity serviceRequest = serviceRequests.findById(response.getServiceRequestId())
                .orElseThrow(() -> new IllegalStateException("Generated service request was not persisted"));
        MonitoringRunEntity run = buildRun(schedule, serviceRequest, runNumber, cycleKey, scheduledFor, now);
        run = runs.saveAndFlush(run);

        schedule.setLastGeneratedAt(now);
        schedule.setNextDueAt(configuration.nextDueAt(scheduledFor, schedule.getFrequency()));
        schedules.save(schedule);
        auditCreated(run);
        return toResponse(run);
    }

    @Override
    public int processDueSchedules(OffsetDateTime now) {
        List<UUID> dueIds = schedules.findByStatusIgnoreCaseAndNextDueAtLessThanEqualOrderByNextDueAtAsc("ACTIVE", now)
                .stream().map(MonitoringScheduleEntity::getMonitoringScheduleId).toList();
        int created = 0;
        for (UUID id : dueIds) {
            try {
                MonitoringRunResponse result = transactionTemplate.execute(status -> runScheduleIfDue(id, now));
                if (result != null) created++;
            } catch (RuntimeException ignored) {
                // A bad schedule must not prevent other due schedules from being processed.
                // Transaction rollback leaves the cycle due for a later retry.
                log.error("Unable to create a Monitoring run for schedule {}", id, ignored);
            }
        }
        return created;
    }

    @Override
    @Transactional
    public MonitoringRunResponse createInitialRunFromPurchasedRequest(UUID scheduleId, OffsetDateTime now) {
        MonitoringScheduleEntity schedule = lockSchedule(scheduleId);
        ServiceRequestEntity source = schedule.getSourceServiceRequest();
        if (source == null || schedule.getCustomerSubscription() != null) {
            throw new BusinessException("A one-time purchased request is required for the initial run");
        }
        if (!runs.findByMonitoringSchedule_MonitoringScheduleIdOrderByRunNumberAsc(scheduleId).isEmpty()) {
            throw new BusinessException("A monitoring run already exists for this one-time request");
        }
        OffsetDateTime scheduledFor = source.getRequestedAt() == null
                ? now : source.getRequestedAt().atOffset(ZoneOffset.UTC);
        String cycleKey = "ONE_TIME:" + source.getServiceRequestId();
        MonitoringRunEntity run = buildRun(schedule, source, 1, cycleKey, scheduledFor, now);
        run = runs.saveAndFlush(run);
        schedule.setLastGeneratedAt(now);
        schedule.setStatus("COMPLETED");
        schedules.save(schedule);
        auditCreated(run);
        return toResponse(run);
    }

    private void validateEntitlement(CustomerSubscriptionEntity subscription, MonitoringScheduleEntity schedule) {
        LocalDate today = LocalDate.now();
        if (!"ACTIVE".equalsIgnoreCase(subscription.getStatus()) || subscription.getStartDate().isAfter(today)
                || (subscription.getEndDate() != null && subscription.getEndDate().isBefore(today))) {
            throw new BusinessException("Monitoring schedule subscription is not active");
        }
        boolean included = entitlements.findByPlanVersion_PlanVersionIdAndService_ServiceId(
                        subscription.getPlanVersion().getPlanVersionId(), schedule.getService().getServiceId())
                .filter(entitlement -> "ACTIVE".equalsIgnoreCase(entitlement.getStatus()))
                .isPresent();
        if (!included) {
            throw new BusinessException("Monitoring service is not included in the active subscription plan");
        }
        if (!subscription.getCustomer().getCustomerId().equals(schedule.getProperty().getCustomer().getCustomerId())) {
            throw new BusinessException("Monitoring subscription and property owner do not match");
        }
    }

    private MonitoringScheduleEntity lockSchedule(UUID scheduleId) {
        MonitoringScheduleEntity schedule = schedules.findByIdForUpdate(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Monitoring schedule not found"));
        configuration.validateMonitoringService(schedule.getService());
        return schedule;
    }

    private MonitoringRunEntity buildRun(MonitoringScheduleEntity schedule, ServiceRequestEntity request,
            int runNumber, String cycleKey, OffsetDateTime scheduledFor, OffsetDateTime now) {
        MonitoringRunEntity run = new MonitoringRunEntity();
        run.setMonitoringSchedule(schedule);
        run.setServiceRequest(request);
        run.setRunNumber(runNumber);
        run.setCycleKey(cycleKey);
        run.setScheduledFor(scheduledFor);
        run.setStartedAt(now);
        run.setRunStatus("SERVICE_REQUEST_LINKED");
        return run;
    }

    private void auditCreated(MonitoringRunEntity run) {
        try {
            String payload = objectMapper.writeValueAsString(java.util.Map.of(
                    "scheduleId", run.getMonitoringSchedule().getMonitoringScheduleId(),
                    "runNumber", run.getRunNumber(),
                    "cycleKey", run.getCycleKey(),
                    "serviceRequestId", run.getServiceRequest().getServiceRequestId()));
            jdbcTemplate.update("INSERT INTO audit_log (entity_type, entity_id, action, new_data, occurred_at) "
                            + "VALUES ('MONITORING_RUN', ?, 'CREATE', CAST(? AS jsonb), CURRENT_TIMESTAMP)",
                    run.getMonitoringRunId(), payload);
        } catch (com.fasterxml.jackson.core.JsonProcessingException ex) {
            throw new IllegalStateException("Unable to serialize Monitoring run audit record", ex);
        }
    }

    private MonitoringRunResponse toResponse(MonitoringRunEntity run) {
        return new MonitoringRunResponse(run.getMonitoringRunId(),
                run.getMonitoringSchedule().getMonitoringScheduleId(), run.getRunNumber(), run.getCycleKey(),
                run.getServiceRequest().getServiceRequestId(), run.getScheduledFor(), run.getStartedAt(), run.getRunStatus());
    }
}
