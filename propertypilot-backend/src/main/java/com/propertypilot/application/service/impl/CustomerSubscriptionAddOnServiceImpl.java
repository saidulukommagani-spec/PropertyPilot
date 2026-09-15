package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateCustomerSubscriptionAddOnRequest;
import com.propertypilot.application.dto.CustomerSubscriptionAddOnResponse;
import com.propertypilot.application.service.CustomerSubscriptionAddOnService;
import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionAddOnEntity;
import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionAddOnEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionAddOnRepository;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionAddOnRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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

    public CustomerSubscriptionAddOnServiceImpl(
            CustomerSubscriptionRepository
                    customerSubscriptionRepository,
            SubscriptionAddOnRepository
                    subscriptionAddOnRepository,
            CustomerSubscriptionAddOnRepository
                    customerSubscriptionAddOnRepository) {

        this.customerSubscriptionRepository =
                customerSubscriptionRepository;

        this.subscriptionAddOnRepository =
                subscriptionAddOnRepository;

        this.customerSubscriptionAddOnRepository =
                customerSubscriptionAddOnRepository;
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

        SubscriptionAddOnEntity addOn =
                subscriptionAddOnRepository
                        .findById(
                                request.getSubscriptionAddOnId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Subscription add-on not found"));

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

        return map(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerSubscriptionAddOnResponse>
    getSubscriptionAddOns(
            UUID customerSubscriptionId) {

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