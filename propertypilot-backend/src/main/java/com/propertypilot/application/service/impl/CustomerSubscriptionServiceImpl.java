package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateCustomerSubscriptionRequest;
import com.propertypilot.application.dto.CustomerSubscriptionResponse;
import com.propertypilot.application.dto.UpdateCustomerSubscriptionRequest;
import com.propertypilot.application.service.BillingService;
import com.propertypilot.application.service.CustomerSubscriptionService;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanVersionEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerJpaRepository;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionPlanVersionRepository;
import com.propertypilot.web.exception.BusinessException;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.propertypilot.application.service.BillingService;
import com.propertypilot.domain.enums.BillingEntityType;
import com.propertypilot.domain.enums.InvoiceType;

import java.math.BigDecimal;
import java.time.LocalDate;
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

            private final BillingService billingService;

    public CustomerSubscriptionServiceImpl(
            CustomerSubscriptionRepository customerSubscriptionRepository,
            CustomerJpaRepository customerRepository,
            SubscriptionPlanVersionRepository subscriptionPlanVersionRepository,
        BillingService billingService) {

        this.customerSubscriptionRepository =
                customerSubscriptionRepository;

        this.customerRepository =
                customerRepository;

        this.subscriptionPlanVersionRepository =
                subscriptionPlanVersionRepository;
                this.billingService = billingService;
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

          if (request.getEndDate()
        .isBefore(request.getStartDate())) {

    throw new BusinessException(
            "End date must be after start date");
}         

        if (!"ACTIVE".equalsIgnoreCase(
        planVersion.getStatus())) {

    throw new BusinessException(
            "Plan version is not active");
}

        CustomerSubscriptionEntity entity =
                new CustomerSubscriptionEntity();

        entity.setCustomer(customer);
        entity.setPlanVersion(planVersion);
        entity.setStatus("ACTIVE");
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        entity.setAutoRenew(Boolean.TRUE.equals(
                request.getAutoRenew()));

        CustomerSubscriptionEntity saved =
                customerSubscriptionRepository.save(entity);
if (planVersion.getPrice() != null
        && planVersion.getPrice()
                .compareTo(BigDecimal.ZERO) > 0) {

    billingService.createInvoice(
            BillingEntityType.SUBSCRIPTION,
            saved.getCustomerSubscriptionId(),
            customer.getCustomerId(),
            InvoiceType.SUBSCRIPTION_FEE,
            planVersion.getPrice(),
            "Subscription purchase invoice");
}
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

    if (request.getStatus() != null) {

        String status =
                request.getStatus()
                        .toUpperCase();

        if (!java.util.List.of(
                "ACTIVE",
                "EXPIRED",
                "CANCELLED",
                "SUSPENDED")
                .contains(status)) {

            throw new IllegalArgumentException(
                    "Invalid subscription status");
        }

        entity.setStatus(status);
    }

    if (request.getEndDate() != null) {

        if (request.getEndDate()
                .isBefore(entity.getStartDate())) {

            throw new IllegalArgumentException(
                    "End date must be after start date");
        }

        entity.setEndDate(
                request.getEndDate());
    }

    if (request.getAutoRenew() != null) {

        entity.setAutoRenew(
                request.getAutoRenew());
    }

    CustomerSubscriptionEntity updated =
            customerSubscriptionRepository.save(
                    entity);

    return map(updated);
}
@Override
@Transactional(readOnly = true)
public List<CustomerSubscriptionResponse>
getExpiringSubscriptions(
        Integer days) {

    LocalDate today =
            LocalDate.now();

    LocalDate futureDate =
            today.plusDays(days);

    return customerSubscriptionRepository
            .findByStatusAndEndDateBetween(
                    "ACTIVE",
                    today,
                    futureDate)
            .stream()
            .map(this::map)
            .toList();
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