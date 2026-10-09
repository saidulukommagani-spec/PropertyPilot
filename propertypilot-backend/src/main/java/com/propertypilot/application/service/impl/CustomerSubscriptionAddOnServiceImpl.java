package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateCustomerSubscriptionAddOnRequest;
import com.propertypilot.application.dto.CustomerSubscriptionAddOnResponse;
import com.propertypilot.application.dto.SubscriptionAddOnCatalogResponse;
import com.propertypilot.application.service.CustomerSubscriptionAddOnService;
import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionAddOnEntity;
import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionAddOnEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionAddOnRepository;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionAddOnRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import com.propertypilot.web.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import com.propertypilot.security.SecurityService;

@Service
@Transactional
public class CustomerSubscriptionAddOnServiceImpl
        implements CustomerSubscriptionAddOnService {

    private final CustomerSubscriptionRepository
            customerSubscriptionRepository;

    private final SubscriptionAddOnRepository
            subscriptionAddOnRepository;

    private final CustomerSubscriptionAddOnRepository
            customerSubscriptionAddOnRepository;
    private final SecurityService securityService;

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionAddOnCatalogResponse> getAvailableAddOns() {
        return subscriptionAddOnRepository.findAll().stream()
                .filter(addOn -> "ACTIVE".equalsIgnoreCase(addOn.getStatus()))
                .map(addOn -> new SubscriptionAddOnCatalogResponse(
                        addOn.getSubscriptionAddOnId(), addOn.getService().getServiceId(),
                        addOn.getService().getServiceName(), addOn.getName(), addOn.getPrice(), addOn.getStatus()))
                .toList();
    }

    public CustomerSubscriptionAddOnServiceImpl(
            CustomerSubscriptionRepository
                    customerSubscriptionRepository,
            SubscriptionAddOnRepository
                    subscriptionAddOnRepository,
            CustomerSubscriptionAddOnRepository
                    customerSubscriptionAddOnRepository,
            SecurityService securityService) {

        this.customerSubscriptionRepository =
                customerSubscriptionRepository;

        this.subscriptionAddOnRepository =
                subscriptionAddOnRepository;

        this.customerSubscriptionAddOnRepository =
                customerSubscriptionAddOnRepository;
        this.securityService = securityService;
    }

    @Override
    public CustomerSubscriptionAddOnResponse
    createAddOn(
            UUID customerSubscriptionId,
            CreateCustomerSubscriptionAddOnRequest request) {

        CustomerSubscriptionEntity subscription =
                customerSubscriptionRepository
                        .findById(customerSubscriptionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer subscription not found"));
        securityService.validateCustomerAccess(subscription.getCustomer());

        SubscriptionAddOnEntity addOn =
                subscriptionAddOnRepository
                        .findById(
                                request.getSubscriptionAddOnId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Subscription add-on not found"));

        if (!"ACTIVE".equalsIgnoreCase(subscription.getStatus())) {
            throw new BusinessException("An add-on can only be attached to an active subscription");
        }
        if (!"ACTIVE".equalsIgnoreCase(addOn.getStatus())) {
            throw new BusinessException("The selected subscription add-on is not available");
        }
        if (addOn.getPrice() == null || addOn.getPrice().signum() < 0) {
            throw new BusinessException("The selected subscription add-on has invalid pricing");
        }
        if (addOn.getPrice().signum() > 0) {
            throw new BusinessException(
                    "Paid add-ons require a verified billing workflow before they can be activated");
        }
        if (request.getStartsAt() == null
                || (request.getEndsAt() != null && !request.getEndsAt().isAfter(request.getStartsAt()))) {
            throw new BusinessException("A valid add-on start and end date is required");
        }

        CustomerSubscriptionAddOnEntity entity =
                new CustomerSubscriptionAddOnEntity();

        entity.setCustomerSubscription(
                subscription);

        entity.setSubscriptionAddOn(
                addOn);

        entity.setPricingEstimateId(
                request.getPricingEstimateId());

        entity.setQuantity(
                request.getQuantity());

        entity.setStartsAt(
                request.getStartsAt());

        entity.setEndsAt(
                request.getEndsAt());

        entity.setStatus(
                "ACTIVE");

        entity =
                customerSubscriptionAddOnRepository
                        .save(entity);

        return map(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerSubscriptionAddOnResponse
    getAddOn(
            UUID addOnId) {

        CustomerSubscriptionAddOnEntity entity =
                customerSubscriptionAddOnRepository
                        .findById(addOnId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer subscription add-on not found"));
        securityService.validateCustomerAccess(entity.getCustomerSubscription().getCustomer());

        return map(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerSubscriptionAddOnResponse>
    getSubscriptionAddOns(
            UUID customerSubscriptionId) {

        CustomerSubscriptionEntity subscription = customerSubscriptionRepository.findById(customerSubscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer subscription not found"));
        securityService.validateCustomerAccess(subscription.getCustomer());

        return customerSubscriptionAddOnRepository
                .findByCustomerSubscriptionCustomerSubscriptionId(
                        customerSubscriptionId)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public void deleteAddOn(
            UUID addOnId) {

        CustomerSubscriptionAddOnEntity entity =
                customerSubscriptionAddOnRepository
                        .findById(addOnId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer subscription add-on not found"));
        securityService.validateCustomerAccess(entity.getCustomerSubscription().getCustomer());

        customerSubscriptionAddOnRepository
                .delete(entity);
    }

    private CustomerSubscriptionAddOnResponse map(
            CustomerSubscriptionAddOnEntity entity) {

        CustomerSubscriptionAddOnResponse response =
                new CustomerSubscriptionAddOnResponse();

        response.setCustomerSubscriptionAddOnId(
                entity.getCustomerSubscriptionAddOnId());

        response.setCustomerSubscriptionId(
                entity.getCustomerSubscription()
                        .getCustomerSubscriptionId());

        response.setSubscriptionAddOnId(
                entity.getSubscriptionAddOn()
                        .getSubscriptionAddOnId());

        response.setAddOnName(
                entity.getSubscriptionAddOn()
                        .getName());

        response.setPricingEstimateId(
                entity.getPricingEstimateId());

        response.setQuantity(
                entity.getQuantity());

        response.setStatus(
                entity.getStatus());

        response.setStartsAt(
                entity.getStartsAt());

        response.setEndsAt(
                entity.getEndsAt());

        return response;
    }
}
