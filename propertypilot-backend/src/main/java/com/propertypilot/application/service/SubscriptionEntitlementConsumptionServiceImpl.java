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

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.propertypilot.security.SecurityService;

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
    private final SubscriptionPaymentVerificationService paymentVerificationService;
    private final SecurityService securityService;
    private final Clock businessClock;

    public SubscriptionEntitlementConsumptionServiceImpl(
            SubscriptionEntitlementConsumptionRepository consumptionRepository,
            CustomerSubscriptionRepository customerSubscriptionRepository,
            SubscriptionPlanEntitlementRepository entitlementRepository,
            ServiceRepository serviceRepository,
            ServiceRequestRepository serviceRequestRepository,
            SubscriptionPaymentVerificationService paymentVerificationService,
            SecurityService securityService,
            Clock businessClock) {

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
        this.paymentVerificationService = paymentVerificationService;
        this.securityService = securityService;
        this.businessClock = businessClock;
    }

    @Override
    public ConsumptionResponse consume(
            CreateConsumptionRequest request) {

        CustomerSubscriptionEntity subscription =
                customerSubscriptionRepository
                        .findByIdForUpdate(
                                request.getCustomerSubscriptionId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer subscription not found"));
        securityService.validateCustomerAccess(subscription.getCustomer());

        LocalDate today = LocalDate.now(businessClock);
        if (!"ACTIVE".equalsIgnoreCase(subscription.getStatus())
                || (subscription.getStartDate() != null && subscription.getStartDate().isAfter(today))
                || (subscription.getEndDate() != null && subscription.getEndDate().isBefore(today))) {

            throw new BusinessException(
                    "Subscription is not active for the current date");
        }
        requireVerifiedPayment(subscription);

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
        if (!"ACTIVE".equalsIgnoreCase(entitlement.getStatus())) {
            throw new BusinessException("Service entitlement is not active");
        }

        int requestedQty =
                request.getQuantity() == null
                        ? 1
                        : request.getQuantity();

        if (requestedQty <= 0) {

            throw new BusinessException(
                    "Quantity must be greater than zero");
        }

        if (request.getServiceRequestId() != null) {
            ServiceRequestEntity serviceRequest = serviceRequestRepository
                    .findByIdForUpdate(request.getServiceRequestId())
                    .orElseThrow(() -> new ResourceNotFoundException("Service request not found"));
            securityService.validateServiceRequestAccess(serviceRequest);
            if (serviceRequest.getCustomer() == null
                    || !subscription.getCustomer().getCustomerId()
                            .equals(serviceRequest.getCustomer().getCustomerId())) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "Service request and subscription belong to different customers");
            }
            if (serviceRequest.getService() == null
                    || !service.getServiceId().equals(serviceRequest.getService().getServiceId())) {
                throw new BusinessException("The entitlement service must match the service request");
            }
            if ("COMPLETED".equalsIgnoreCase(serviceRequest.getStatus())
                    || "CANCELLED".equalsIgnoreCase(serviceRequest.getStatus())
                    || "FAILED".equalsIgnoreCase(serviceRequest.getStatus())) {
                throw new BusinessException("An entitlement cannot be applied to a closed service request");
            }

            SubscriptionEntitlementConsumptionEntity prior = consumptionRepository
                    .findByServiceRequest_ServiceRequestIdAndPeriodEndIsNull(
                            serviceRequest.getServiceRequestId())
                    .orElse(null);
            if (prior != null) {
                if (!subscription.getCustomerSubscriptionId().equals(
                                prior.getCustomerSubscription().getCustomerSubscriptionId())
                        || !service.getServiceId().equals(prior.getService().getServiceId())
                        || !Integer.valueOf(requestedQty).equals(prior.getConsumedQuantity())) {
                    throw new BusinessException("This service request already consumed a different entitlement");
                }
                int totalConsumed = getActiveConsumedQuantity(subscription, service);
                return mapResponse(prior, totalConsumed, entitlement.getQuantity());
            }
        }

        int currentConsumed = getActiveConsumedQuantity(subscription, service);
        int newConsumed = currentConsumed + requestedQty;

        if (newConsumed >
                entitlement.getQuantity()) {

            throw new BusinessException(
                    "Entitlement limit exceeded");
        }

        SubscriptionEntitlementConsumptionEntity consumption =
                new SubscriptionEntitlementConsumptionEntity();
        consumption.setCustomerSubscription(subscription);
        consumption.setService(service);
        consumption.setEntitlement(entitlement);
        consumption.setPeriodStart(LocalDate.now(businessClock));
        consumption.setEntitledQuantity(entitlement.getQuantity());
        consumption.setConsumedQuantity(
                requestedQty);

        if (request.getServiceRequestId() != null) {
            ServiceRequestEntity serviceRequest = serviceRequestRepository
                    .findByIdForUpdate(request.getServiceRequestId())
                    .orElseThrow(() -> new ResourceNotFoundException("Service request not found"));
            consumption.setServiceRequest(
                    serviceRequest);
        }

        consumption =
                consumptionRepository.save(
                        consumption);

        return mapResponse(consumption, newConsumed, entitlement.getQuantity());
    }

    @Override
@Transactional(readOnly = true)
public List<SubscriptionUsageResponse> getUsage(
        UUID customerSubscriptionId) {

    CustomerSubscriptionEntity subscription =
            customerSubscriptionRepository
                    .findById(customerSubscriptionId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Customer subscription not found"));
    securityService.validateCustomerAccess(subscription.getCustomer());
    requireVerifiedPayment(subscription);

    List<SubscriptionPlanEntitlementEntity>
            entitlements =
            entitlementRepository
                    .findByPlanVersion_PlanVersionId(
                            subscription
                                    .getPlanVersion()
                                    .getPlanVersionId());

    List<SubscriptionUsageResponse>
            responses =
            new ArrayList<>();

    for (SubscriptionPlanEntitlementEntity entitlement
            : entitlements) {

        List<SubscriptionEntitlementConsumptionEntity> consumptions =
                consumptionRepository
                        .findByCustomerSubscription_CustomerSubscriptionIdAndService_ServiceIdAndPeriodEndIsNull(
                                customerSubscriptionId,
                                entitlement.getService().getServiceId());

        int consumedQty =
                consumptions.stream()
                        .mapToInt(SubscriptionEntitlementConsumptionEntity::getConsumedQuantity)
                        .sum();

        SubscriptionUsageResponse response =
                new SubscriptionUsageResponse();

        response.setServiceId(
                entitlement.getService()
                        .getServiceId());
        response.setServiceName(
                entitlement.getService()
                        .getServiceName());

        response.setEntitledQuantity(
                entitlement.getQuantity());

        response.setConsumedQuantity(
                consumedQty);

        response.setRemainingQuantity(
                entitlement.getQuantity()
                        - consumedQty);

        responses.add(
                response);
    }

    return responses;
}

    private void requireVerifiedPayment(CustomerSubscriptionEntity subscription) {
        if (!paymentVerificationService.isPaymentVerified(subscription)) {
            throw new BusinessException(
                    "Subscription benefits require verified payment for the purchase or renewal");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionUsageResponse> getRemaining(
            UUID customerSubscriptionId) {

        return getUsage(
                customerSubscriptionId);
    }

    private int getActiveConsumedQuantity(CustomerSubscriptionEntity subscription, ServiceEntity service) {
        return consumptionRepository
                .findByCustomerSubscription_CustomerSubscriptionIdAndService_ServiceIdAndPeriodEndIsNull(
                        subscription.getCustomerSubscriptionId(), service.getServiceId())
                .stream()
                .mapToInt(SubscriptionEntitlementConsumptionEntity::getConsumedQuantity)
                .sum();
    }

    private ConsumptionResponse mapResponse(
            SubscriptionEntitlementConsumptionEntity entity,
            int totalConsumed,
            int entitledQuantity) {

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
                entitledQuantity);

        response.setConsumedQuantity(
                totalConsumed);

        response.setRemainingQuantity(
                entitledQuantity - totalConsumed);

        response.setPeriodStart(
                entity.getPeriodStart());

        response.setPeriodEnd(
                entity.getPeriodEnd());

        return response;
    }
}
