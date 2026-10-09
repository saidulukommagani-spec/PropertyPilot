package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateMonitoringScheduleRequest;
import com.propertypilot.application.dto.MonitoringRunResponse;
import com.propertypilot.application.dto.MonitoringScheduleResponse;
import com.propertypilot.application.service.MonitoringConfigurationService;
import com.propertypilot.application.service.MonitoringRunService;
import com.propertypilot.application.service.MonitoringScheduleService;
import com.propertypilot.domain.enums.ServiceRequestStatus;
import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionEntity;
import com.propertypilot.infrastructure.persistence.entity.MonitoringScheduleEntity;
import com.propertypilot.infrastructure.persistence.entity.PricingEstimateEntity;
import com.propertypilot.infrastructure.persistence.entity.Property;
import com.propertypilot.infrastructure.persistence.entity.ServiceEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionRepository;
import com.propertypilot.infrastructure.persistence.repository.MonitoringRunRepository;
import com.propertypilot.infrastructure.persistence.repository.MonitoringScheduleRepository;
import com.propertypilot.infrastructure.persistence.repository.PropertyRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestRepository;
import com.propertypilot.security.SecurityService;
import com.propertypilot.web.exception.BusinessException;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MonitoringScheduleServiceImpl implements MonitoringScheduleService {
    private final MonitoringScheduleRepository schedules;
    private final MonitoringRunRepository runs;
    private final CustomerSubscriptionRepository subscriptions;
    private final PropertyRepository properties;
    private final ServiceRepository services;
    private final ServiceRequestRepository serviceRequests;
    private final MonitoringConfigurationService configuration;
    private final MonitoringRunService runService;
    private final SecurityService security;

    public MonitoringScheduleServiceImpl(MonitoringScheduleRepository schedules,
            MonitoringRunRepository runs, CustomerSubscriptionRepository subscriptions,
            PropertyRepository properties, ServiceRepository services,
            ServiceRequestRepository serviceRequests,
            MonitoringConfigurationService configuration, MonitoringRunService runService,
            SecurityService security) {
        this.schedules = schedules;
        this.runs = runs;
        this.subscriptions = subscriptions;
        this.properties = properties;
        this.services = services;
        this.serviceRequests = serviceRequests;
        this.configuration = configuration;
        this.runService = runService;
        this.security = security;
    }

    @Override
    public MonitoringScheduleResponse createSchedule(CreateMonitoringScheduleRequest request) {
        if (request == null || (request.customerSubscriptionId() == null)
                == (request.sourceServiceRequestId() == null)) {
            throw new BusinessException("Provide exactly one subscription or purchased service request");
        }
        String frequency = configuration.normalizeFrequency(request.frequency());
        ServiceEntity service = services.findById(request.serviceId())
                .orElseThrow(() -> new ResourceNotFoundException("Monitoring service not found"));
        configuration.validateMonitoringService(service);
        Property property = properties.findById(request.propertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        CustomerSubscriptionEntity subscription = null;
        ServiceRequestEntity sourceRequest = null;
        if (request.customerSubscriptionId() != null) {
            subscription = subscriptions.findById(request.customerSubscriptionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer subscription not found"));
            validateActiveSubscription(subscription);
            security.validateCustomerAccess(subscription.getCustomer());
            if (!subscription.getCustomer().getCustomerId().equals(property.getCustomer().getCustomerId())) {
                throw new BusinessException("Subscription and property must belong to the same customer");
            }
        } else {
            sourceRequest = serviceRequests.findById(request.sourceServiceRequestId())
                    .orElseThrow(() -> new ResourceNotFoundException("Purchased service request not found"));
            if (schedules.existsBySourceServiceRequest_ServiceRequestId(sourceRequest.getServiceRequestId())) {
                throw new BusinessException("A Monitoring schedule already exists for this purchased request");
            }
            security.validateCustomerAccess(sourceRequest.getCustomer());
            PricingEstimateEntity quote = sourceRequest.getPricingEstimate();
            if (quote == null || !"ACCEPTED".equals(quote.getStatus()) || quote.getAcceptedAt() == null) {
                throw new BusinessException("One-time Monitoring must originate from a request created from an accepted quote");
            }
            if (!ServiceRequestStatus.NEW.name().equals(sourceRequest.getStatus())
                    && !ServiceRequestStatus.PENDING_ASSIGNMENT.name().equals(sourceRequest.getStatus())) {
                throw new BusinessException("The purchased Monitoring request is not eligible to start a monitoring run");
            }
            if (!property.getPropertyId().equals(sourceRequest.getProperty().getPropertyId())
                    || sourceRequest.getService() == null
                    || !service.getServiceId().equals(sourceRequest.getService().getServiceId())) {
                throw new BusinessException("The purchased request must match the Monitoring property and service");
            }
        }

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime firstDueAt = request.firstDueAt() == null ? now : request.firstDueAt();
        if (firstDueAt.isBefore(now.minusMinutes(1)) && subscription != null) {
            throw new BusinessException("Initial monitoring due time cannot be in the past");
        }

        MonitoringScheduleEntity schedule = new MonitoringScheduleEntity();
        schedule.setCustomerSubscription(subscription);
        schedule.setSourceServiceRequest(sourceRequest);
        schedule.setProperty(property);
        schedule.setService(service);
        schedule.setFrequency(frequency);
        schedule.setNextDueAt(firstDueAt);
        schedule.setStatus("ACTIVE");
        schedule = schedules.saveAndFlush(schedule);

        if (sourceRequest != null) {
            runService.createInitialRunFromPurchasedRequest(schedule.getMonitoringScheduleId(), now);
        } else if (!firstDueAt.isAfter(now)) {
            runService.runScheduleIfDue(schedule.getMonitoringScheduleId(), now);
        }
        return toResponse(loadSchedule(schedule.getMonitoringScheduleId()));
    }

    @Override
    @Transactional(readOnly = true)
    public MonitoringScheduleResponse getSchedule(UUID scheduleId) {
        MonitoringScheduleEntity schedule = loadSchedule(scheduleId);
        security.validateCustomerAccess(schedule.getProperty().getCustomer());
        return toResponse(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonitoringScheduleResponse> getMySchedules() {
        UUID customerId = security.getCurrentCustomerId();
        return schedules.findByProperty_Customer_CustomerIdOrderByNextDueAtAsc(customerId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonitoringRunResponse> getRuns(UUID scheduleId) {
        MonitoringScheduleEntity schedule = loadSchedule(scheduleId);
        security.validateCustomerAccess(schedule.getProperty().getCustomer());
        return runs.findByMonitoringSchedule_MonitoringScheduleIdOrderByRunNumberAsc(scheduleId)
                .stream().map(run -> new MonitoringRunResponse(run.getMonitoringRunId(), scheduleId,
                        run.getRunNumber(), run.getCycleKey(), run.getServiceRequest().getServiceRequestId(),
                        run.getScheduledFor(), run.getStartedAt(), run.getRunStatus())).toList();
    }

    private void validateActiveSubscription(CustomerSubscriptionEntity subscription) {
        LocalDate today = LocalDate.now();
        if (!"ACTIVE".equalsIgnoreCase(subscription.getStatus())
                || subscription.getStartDate().isAfter(today)
                || (subscription.getEndDate() != null && subscription.getEndDate().isBefore(today))) {
            throw new BusinessException("An active subscription is required to create a Monitoring schedule");
        }
    }

    private MonitoringScheduleEntity loadSchedule(UUID id) {
        return schedules.findById(id).orElseThrow(() -> new ResourceNotFoundException("Monitoring schedule not found"));
    }

    private MonitoringScheduleResponse toResponse(MonitoringScheduleEntity schedule) {
        return new MonitoringScheduleResponse(schedule.getMonitoringScheduleId(),
                schedule.getCustomerSubscription() == null ? null
                        : schedule.getCustomerSubscription().getCustomerSubscriptionId(),
                schedule.getSourceServiceRequest() == null ? null
                        : schedule.getSourceServiceRequest().getServiceRequestId(),
                schedule.getProperty().getPropertyId(), schedule.getService().getServiceId(),
                schedule.getFrequency(), schedule.getNextDueAt(), schedule.getLastGeneratedAt(), schedule.getStatus());
    }
}
