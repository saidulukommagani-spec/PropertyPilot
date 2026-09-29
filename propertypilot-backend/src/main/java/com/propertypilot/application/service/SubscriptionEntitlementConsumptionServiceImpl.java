package com.propertypilot.application.service;

import com.propertypilot.application.dto.ConsumptionResponse;
import com.propertypilot.application.dto.CreateConsumptionRequest;
import com.propertypilot.application.dto.SubscriptionUsageResponse;

import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionEntitlementConsumptionEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanEntitlementEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionEntitlementConsumptionRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionPlanEntitlementRepository;
import com.propertypilot.web.exception.BusinessException;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SubscriptionEntitlementConsumptionServiceImpl
        implements SubscriptionEntitlementConsumptionService {

    private final SubscriptionEntitlementConsumptionRepository
            consumptionRepository;

    private final CustomerSubscriptionRepository
            customerSubscriptionRepository;

    private final SubscriptionPlanEntitlementRepository
            entitlementRepository;

    private final ServiceRepository
            serviceRepository;

    private final ServiceRequestRepository
            serviceRequestRepository;

    public SubscriptionEntitlementConsumptionServiceImpl(
            SubscriptionEntitlementConsumptionRepository consumptionRepository,
            CustomerSubscriptionRepository customerSubscriptionRepository,
            SubscriptionPlanEntitlementRepository entitlementRepository,
            ServiceRepository serviceRepository,
            ServiceRequestRepository serviceRequestRepository) {

        this.consumptionRepository =
                consumptionRepository;

        this.customerSubscriptionRepository =
                customerSubscriptionRepository;

        this.entitlementRepository =
                entitlementRepository;

        this.serviceRepository =
                serviceRepository;

        this.serviceRequestRepository =
                serviceRequestRepository;
    }

    @Override
    public ConsumptionResponse consume(
            CreateConsumptionRequest request) {

        CustomerSubscriptionEntity subscription =
                customerSubscriptionRepository
                        .findById(
                                request.getCustomerSubscriptionId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer subscription not found"));

        if (!"ACTIVE".equalsIgnoreCase(
                subscription.getStatus())) {

            throw new BusinessException(
                    "Subscription is not active");
        }

        ServiceEntity service =
                serviceRepository
                        .findById(
                                request.getServiceId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Service not found"));

        SubscriptionPlanEntitlementEntity entitlement =
                entitlementRepository
                        .findByPlanVersion_PlanVersionIdAndService_ServiceId(
                                subscription
                                        .getPlanVersion()
                                        .getPlanVersionId(),
                                service.getServiceId())
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Service not included in subscription"));

        SubscriptionEntitlementConsumptionEntity consumption =
                consumptionRepository
                        .findByCustomerSubscription_CustomerSubscriptionIdAndService_ServiceIdAndPeriodEndIsNull(
                                subscription.getCustomerSubscriptionId(),
                                service.getServiceId())
                        .orElse(null);

        if (consumption == null) {

            consumption =
                    new SubscriptionEntitlementConsumptionEntity();

            consumption.setCustomerSubscription(
                    subscription);

            consumption.setService(
                    service);

            consumption.setEntitlement(
                    entitlement);

            consumption.setPeriodStart(
                    LocalDate.now());

            consumption.setConsumedQuantity(
                    0);

            consumption.setEntitledQuantity(
                    entitlement.getQuantity());
        }

        int requestedQty =
                request.getQuantity() == null
                        ? 1
                        : request.getQuantity();

        if (requestedQty <= 0) {

            throw new BusinessException(
                    "Quantity must be greater than zero");
        }

        int newConsumed =
                consumption.getConsumedQuantity()
                        + requestedQty;

        if (newConsumed >
                consumption.getEntitledQuantity()) {

            throw new BusinessException(
                    "Entitlement limit exceeded");
        }

        consumption.setConsumedQuantity(
                newConsumed);

        if (request.getServiceRequestId() != null) {

            ServiceRequestEntity serviceRequest =
                    serviceRequestRepository
                            .findById(
                                    request.getServiceRequestId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Service request not found"));

            consumption.setServiceRequest(
                    serviceRequest);
        }

        consumption =
                consumptionRepository.save(
                        consumption);

        return mapResponse(
                consumption);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionUsageResponse> getUsage(
            UUID customerSubscriptionId) {

        List<SubscriptionEntitlementConsumptionEntity>
                consumptions =
                consumptionRepository
                        .findByCustomerSubscription_CustomerSubscriptionId(
                                customerSubscriptionId);

        List<SubscriptionUsageResponse>
                responses =
                new ArrayList<>();

        for (SubscriptionEntitlementConsumptionEntity c
                : consumptions) {

            SubscriptionUsageResponse response =
                    new SubscriptionUsageResponse();

            response.setServiceId(
                    c.getService().getServiceId());

            response.setEntitledQuantity(
                    c.getEntitledQuantity());

            response.setConsumedQuantity(
                    c.getConsumedQuantity());

            response.setRemainingQuantity(
                    c.getEntitledQuantity()
                            - c.getConsumedQuantity());

            responses.add(
                    response);
        }

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionUsageResponse> getRemaining(
            UUID customerSubscriptionId) {

        return getUsage(
                customerSubscriptionId);
    }

    private ConsumptionResponse mapResponse(
            SubscriptionEntitlementConsumptionEntity entity) {

        ConsumptionResponse response =
                new ConsumptionResponse();

        response.setEntitlementConsumptionId(
                entity.getEntitlementConsumptionId());

        response.setCustomerSubscriptionId(
                entity.getCustomerSubscription()
                        .getCustomerSubscriptionId());

        response.setServiceId(
                entity.getService()
                        .getServiceId());

        response.setEntitledQuantity(
                entity.getEntitledQuantity());

        response.setConsumedQuantity(
                entity.getConsumedQuantity());

        response.setRemainingQuantity(
                entity.getEntitledQuantity()
                        - entity.getConsumedQuantity());

        response.setPeriodStart(
                entity.getPeriodStart());

        response.setPeriodEnd(
                entity.getPeriodEnd());

        return response;
    }
}