package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateSubscriptionRenewalRequest;
import com.propertypilot.application.dto.SubscriptionRenewalResponse;
import com.propertypilot.application.service.SubscriptionRenewalService;
import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanVersionEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionRenewalEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionPlanVersionRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionRenewalRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SubscriptionRenewalServiceImpl
        implements SubscriptionRenewalService {

    private final SubscriptionRenewalRepository
            renewalRepository;

    private final CustomerSubscriptionRepository
            customerSubscriptionRepository;

    private final SubscriptionPlanVersionRepository
            planVersionRepository;

    public SubscriptionRenewalServiceImpl(
            SubscriptionRenewalRepository renewalRepository,
            CustomerSubscriptionRepository customerSubscriptionRepository,
            SubscriptionPlanVersionRepository planVersionRepository) {

        this.renewalRepository =
                renewalRepository;

        this.customerSubscriptionRepository =
                customerSubscriptionRepository;

        this.planVersionRepository =
                planVersionRepository;
    }

    @Override
    public SubscriptionRenewalResponse renewSubscription(
            CreateSubscriptionRenewalRequest request) {

        CustomerSubscriptionEntity
                existingSubscription =
                customerSubscriptionRepository
                        .findById(
                                request.getCustomerSubscriptionId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer Subscription not found"));

        SubscriptionPlanVersionEntity
                latestPlanVersion =
                planVersionRepository
                        .findTopBySubscriptionPlan_SubscriptionPlanIdOrderByVersionNumberDesc(
                                existingSubscription
                                        .getPlanVersion()
                                        .getSubscriptionPlan()
                                        .getSubscriptionPlanId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Latest Plan Version not found"));

        CustomerSubscriptionEntity
                newSubscription =
                new CustomerSubscriptionEntity();

        newSubscription.setCustomer(
                existingSubscription.getCustomer());

        newSubscription.setPlanVersion(
                latestPlanVersion);

        newSubscription.setStatus(
                "ACTIVE");

        newSubscription.setStartDate(
                request.getPeriodStart());

        newSubscription.setEndDate(
                request.getPeriodEnd());

        newSubscription.setAutoRenew(
                existingSubscription.getAutoRenew());

        newSubscription =
                customerSubscriptionRepository
                        .save(newSubscription);

        SubscriptionRenewalEntity renewal =
                new SubscriptionRenewalEntity();

        renewal.setCustomerSubscription(
                existingSubscription);

        renewal.setNewCustomerSubscription(
                newSubscription);

        renewal.setPaymentId(
                request.getPaymentId());

        renewal.setRenewalDate(
                LocalDate.now());

        renewal.setPeriodStart(
                request.getPeriodStart());

        renewal.setPeriodEnd(
                request.getPeriodEnd());

        renewal.setRenewalType(
                request.getRenewalType());

        renewal.setRenewalAmount(
                request.getRenewalAmount());

        renewal.setRemarks(
                request.getRemarks());

        renewal.setStatus(
                "SUCCESS");

        renewal =
                renewalRepository.save(
                        renewal);

        existingSubscription.setStatus(
                "EXPIRED");

        customerSubscriptionRepository
                .save(existingSubscription);

        return mapToResponse(
                renewal);
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionRenewalResponse getRenewal(
            UUID subscriptionRenewalId) {

        SubscriptionRenewalEntity renewal =
                renewalRepository.findById(
                                subscriptionRenewalId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Subscription Renewal not found"));

        return mapToResponse(
                renewal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionRenewalResponse>
    getRenewalsBySubscription(
            UUID customerSubscriptionId) {

        return renewalRepository
                .findByCustomerSubscription_CustomerSubscriptionId(
                        customerSubscriptionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private SubscriptionRenewalResponse
    mapToResponse(
            SubscriptionRenewalEntity entity) {

        SubscriptionRenewalResponse response =
                new SubscriptionRenewalResponse();

        response.setSubscriptionRenewalId(
                entity.getSubscriptionRenewalId());

        response.setCustomerSubscriptionId(
                entity.getCustomerSubscription()
                        .getCustomerSubscriptionId());

        if (entity.getNewCustomerSubscription()
                != null) {

            response.setNewCustomerSubscriptionId(
                    entity.getNewCustomerSubscription()
                            .getCustomerSubscriptionId());
        }

        response.setPaymentId(
                entity.getPaymentId());

        response.setRenewalDate(
                entity.getRenewalDate());

        response.setPeriodStart(
                entity.getPeriodStart());

        response.setPeriodEnd(
                entity.getPeriodEnd());

        response.setRenewalType(
                entity.getRenewalType());

        response.setRenewalAmount(
                entity.getRenewalAmount());

        response.setRemarks(
                entity.getRemarks());

        response.setStatus(
                entity.getStatus());

        return response;
    }
}