package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateSubscriptionPlanRequest;
import com.propertypilot.application.dto.SubscriptionPlanResponse;
import com.propertypilot.application.dto.UpdateSubscriptionPlanRequest;
import com.propertypilot.application.service.SubscriptionPlanService;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanEntity;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionPlanRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SubscriptionPlanServiceImpl
        implements SubscriptionPlanService {

    private final SubscriptionPlanRepository
            subscriptionPlanRepository;

    public SubscriptionPlanServiceImpl(
            SubscriptionPlanRepository
                    subscriptionPlanRepository) {

        this.subscriptionPlanRepository =
                subscriptionPlanRepository;
    }

    @Override
    public SubscriptionPlanResponse createPlan(
            CreateSubscriptionPlanRequest request) {

        SubscriptionPlanEntity plan =
                new SubscriptionPlanEntity();

        plan.setPlanCode(
                request.getPlanCode());

        plan.setPlanName(
                request.getPlanName());

        plan.setDescription(
                request.getDescription());

        plan.setStatus(
                request.getStatus());

        return buildResponse(
                subscriptionPlanRepository.save(
                        plan));
    }

    @Override
    public SubscriptionPlanResponse getPlan(
            UUID planId) {

        return buildResponse(
                subscriptionPlanRepository
                        .findById(planId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Subscription plan not found")));
    }

    @Override
    public List<SubscriptionPlanResponse>
    getAllPlans() {

        return subscriptionPlanRepository
                .findAll()
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    @Override
    public SubscriptionPlanResponse updatePlan(
            UUID planId,
            UpdateSubscriptionPlanRequest request) {

        SubscriptionPlanEntity plan =
                subscriptionPlanRepository
                        .findById(planId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Subscription plan not found"));

        plan.setPlanName(
                request.getPlanName());

        plan.setDescription(
                request.getDescription());

        plan.setStatus(
                request.getStatus());

        return buildResponse(
                subscriptionPlanRepository.save(
                        plan));
    }

    private SubscriptionPlanResponse
    buildResponse(
            SubscriptionPlanEntity plan) {

        SubscriptionPlanResponse response =
                new SubscriptionPlanResponse();

        response.setSubscriptionPlanId(
                plan.getSubscriptionPlanId());

        response.setPlanCode(
                plan.getPlanCode());

        response.setPlanName(
                plan.getPlanName());

        response.setDescription(
                plan.getDescription());

        response.setStatus(
                plan.getStatus());

        return response;
    }
}