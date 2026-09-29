package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateSubscriptionPlanVersionRequest;
import com.propertypilot.application.dto.SubscriptionPlanVersionResponse;
import com.propertypilot.application.dto.UpdateSubscriptionPlanVersionRequest;
import com.propertypilot.application.service.SubscriptionPlanVersionService;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanVersionEntity;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionPlanRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionPlanVersionRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.Set;

@Service
public class SubscriptionPlanVersionServiceImpl
        implements SubscriptionPlanVersionService {

    private final SubscriptionPlanVersionRepository
            subscriptionPlanVersionRepository;

    private final SubscriptionPlanRepository
            subscriptionPlanRepository;

    public SubscriptionPlanVersionServiceImpl(
            SubscriptionPlanVersionRepository subscriptionPlanVersionRepository,
            SubscriptionPlanRepository subscriptionPlanRepository) {

        this.subscriptionPlanVersionRepository =
                subscriptionPlanVersionRepository;

        this.subscriptionPlanRepository =
                subscriptionPlanRepository;
    }

    @Override
    public SubscriptionPlanVersionResponse createPlanVersion(
            CreateSubscriptionPlanVersionRequest request) {

        SubscriptionPlanEntity plan =
                subscriptionPlanRepository
                        .findById(
                                request.getSubscriptionPlanId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Subscription plan not found"));
validatePrice(request);

validateDates(request);

validateStatus(
        request.getStatus());

if ("ACTIVE".equals(
        request.getStatus())) {

    subscriptionPlanVersionRepository
            .findBySubscriptionPlan_SubscriptionPlanIdAndStatus(
                    plan.getSubscriptionPlanId(),
                    "ACTIVE")
            .ifPresent(existing -> {

                existing.setStatus(
                        "RETIRED");

                subscriptionPlanVersionRepository
                        .save(existing);
            });
}

        SubscriptionPlanVersionEntity entity =
                new SubscriptionPlanVersionEntity();

        entity.setSubscriptionPlan(plan);

      Integer nextVersion =
        subscriptionPlanVersionRepository
                .findTopBySubscriptionPlan_SubscriptionPlanIdOrderByVersionNumberDesc(
                        plan.getSubscriptionPlanId())
                .map(existing ->
                        existing.getVersionNumber() + 1)
                .orElse(1);

entity.setVersionNumber(
        nextVersion);

        entity.setPrice(
                request.getPrice());

        entity.setCurrencyCode(
                request.getCurrencyCode());

        entity.setEffectiveFrom(
                request.getEffectiveFrom());

        entity.setEffectiveTo(
                request.getEffectiveTo());

        entity.setStatus(
                request.getStatus());

        entity.setBenefitsJson(
                request.getBenefitsJson());

        return buildResponse(
                subscriptionPlanVersionRepository
                        .save(entity));
    }

    @Override
    public SubscriptionPlanVersionResponse getPlanVersion(
            UUID planVersionId) {

        return buildResponse(
                subscriptionPlanVersionRepository
                        .findById(planVersionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Subscription plan version not found")));
    }

    @Override
    public List<SubscriptionPlanVersionResponse>
    getAllPlanVersions() {

        return subscriptionPlanVersionRepository
                .findAll()
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    @Override
    public SubscriptionPlanVersionResponse updatePlanVersion(
            UUID planVersionId,
            UpdateSubscriptionPlanVersionRequest request) {

        SubscriptionPlanVersionEntity entity =
                subscriptionPlanVersionRepository
                        .findById(planVersionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Subscription plan version not found"));

       if (request.getPrice() != null
        && request.getPrice().signum() <= 0) {

    throw new IllegalArgumentException(
            "Price must be greater than zero");
}

if (request.getEffectiveTo() != null
        && request.getEffectiveFrom() != null
        && request.getEffectiveTo()
                .isBefore(
                        request.getEffectiveFrom())) {

    throw new IllegalArgumentException(
            "Effective To must be after Effective From");
}

validateStatus(
        request.getStatus());

        entity.setPrice(
                request.getPrice());

        entity.setCurrencyCode(
                request.getCurrencyCode());

        entity.setEffectiveFrom(
                request.getEffectiveFrom());

        entity.setEffectiveTo(
                request.getEffectiveTo());

        entity.setStatus(
                request.getStatus());

        entity.setBenefitsJson(
                request.getBenefitsJson());

        return buildResponse(
                subscriptionPlanVersionRepository
                        .save(entity));
    }

    private SubscriptionPlanVersionResponse
    buildResponse(
            SubscriptionPlanVersionEntity entity) {

        SubscriptionPlanVersionResponse response =
                new SubscriptionPlanVersionResponse();

        response.setPlanVersionId(
                entity.getPlanVersionId());

        response.setSubscriptionPlanId(
                entity.getSubscriptionPlan()
                        .getSubscriptionPlanId());

        response.setVersionNumber(
                entity.getVersionNumber());

        response.setPrice(
                entity.getPrice());

        response.setCurrencyCode(
                entity.getCurrencyCode());

        response.setEffectiveFrom(
                entity.getEffectiveFrom());

        response.setEffectiveTo(
                entity.getEffectiveTo());

        response.setStatus(
                entity.getStatus());

        response.setBenefitsJson(
                entity.getBenefitsJson());

        return response;
    }

private void validatePrice(
        CreateSubscriptionPlanVersionRequest request) {

    if (request.getPrice() == null
            || request.getPrice().signum() <= 0) {

        throw new IllegalArgumentException(
                "Price must be greater than zero");
    }
}

private void validateDates(
        CreateSubscriptionPlanVersionRequest request) {

    if (request.getEffectiveTo() != null
            && request.getEffectiveTo()
                    .isBefore(
                            request.getEffectiveFrom())) {

        throw new IllegalArgumentException(
                "Effective To must be after Effective From");
    }
}

private void validateStatus(
        String status) {

    Set<String> allowedStatuses =
            Set.of(
                    "DRAFT",
                    "ACTIVE",
                    "RETIRED");

    if (!allowedStatuses.contains(status)) {

        throw new IllegalArgumentException(
                "Invalid status");
    }   
}

}