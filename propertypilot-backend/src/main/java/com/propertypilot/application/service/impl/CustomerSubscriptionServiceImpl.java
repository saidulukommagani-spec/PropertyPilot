package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateCustomerSubscriptionRequest;
import com.propertypilot.application.dto.CustomerSubscriptionResponse;
import com.propertypilot.application.dto.UpdateCustomerSubscriptionRequest;
import com.propertypilot.application.service.CustomerSubscriptionService;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanVersionEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerJpaRepository;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionPlanVersionRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CustomerSubscriptionServiceImpl
        implements CustomerSubscriptionService {

    private final CustomerSubscriptionRepository
            customerSubscriptionRepository;

    private final CustomerJpaRepository
            customerRepository;

    private final SubscriptionPlanVersionRepository
            subscriptionPlanVersionRepository;

    public CustomerSubscriptionServiceImpl(
            CustomerSubscriptionRepository customerSubscriptionRepository,
            CustomerJpaRepository customerRepository,
            SubscriptionPlanVersionRepository subscriptionPlanVersionRepository) {

        this.customerSubscriptionRepository =
                customerSubscriptionRepository;

        this.customerRepository =
                customerRepository;

        this.subscriptionPlanVersionRepository =
                subscriptionPlanVersionRepository;
    }

    @Override
    public CustomerSubscriptionResponse createSubscription(
            UUID customerId,
            CreateCustomerSubscriptionRequest request) {

        CustomerEntity customer =
                customerRepository.findById(customerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found"));

        SubscriptionPlanVersionEntity planVersion =
                subscriptionPlanVersionRepository
                        .findById(request.getPlanVersionId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Subscription Plan Version not found"));

        CustomerSubscriptionEntity entity =
                new CustomerSubscriptionEntity();

        entity.setCustomer(customer);
        entity.setPlanVersion(planVersion);
        entity.setStatus("ACTIVE");
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        entity.setAutoRenew(request.getAutoRenew());

        CustomerSubscriptionEntity saved =
                customerSubscriptionRepository.save(entity);

        return map(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerSubscriptionResponse getSubscription(
            UUID customerSubscriptionId) {

        CustomerSubscriptionEntity entity =
                customerSubscriptionRepository
                        .findById(customerSubscriptionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer Subscription not found"));

        return map(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerSubscriptionResponse>
    getCustomerSubscriptions(
            UUID customerId) {

        return customerSubscriptionRepository
                .findByCustomer_CustomerId(customerId)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public CustomerSubscriptionResponse updateSubscription(
            UUID customerSubscriptionId,
            UpdateCustomerSubscriptionRequest request) {

        CustomerSubscriptionEntity entity =
                customerSubscriptionRepository
                        .findById(customerSubscriptionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer Subscription not found"));

        entity.setStatus(request.getStatus());
        entity.setEndDate(request.getEndDate());
        entity.setAutoRenew(request.getAutoRenew());

        CustomerSubscriptionEntity updated =
                customerSubscriptionRepository.save(entity);

        return map(updated);
    }

    private CustomerSubscriptionResponse map(
            CustomerSubscriptionEntity entity) {

        CustomerSubscriptionResponse response =
                new CustomerSubscriptionResponse();

        response.setCustomerSubscriptionId(
                entity.getCustomerSubscriptionId());

        response.setCustomerId(
                entity.getCustomer().getCustomerId());

        response.setPlanVersionId(
                entity.getPlanVersion().getPlanVersionId());

        response.setStatus(
                entity.getStatus());

        response.setStartDate(
                entity.getStartDate());

        response.setEndDate(
                entity.getEndDate());

        response.setAutoRenew(
                entity.getAutoRenew());

        return response;
    }
}