package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateSubscriptionPlanEntitlementRequest;
import com.propertypilot.application.dto.SubscriptionPlanEntitlementResponse;
import com.propertypilot.application.dto.UpdateSubscriptionPlanEntitlementRequest;
import com.propertypilot.application.service.SubscriptionPlanEntitlementService;
import com.propertypilot.infrastructure.persistence.entity.ServiceEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanEntitlementEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanVersionEntity;
import com.propertypilot.infrastructure.persistence.repository.ServiceRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionPlanEntitlementRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionPlanVersionRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SubscriptionPlanEntitlementServiceImpl
        implements SubscriptionPlanEntitlementService {

    private final SubscriptionPlanEntitlementRepository
            entitlementRepository;

    private final SubscriptionPlanVersionRepository
            planVersionRepository;

    private final ServiceRepository
            serviceRepository;

    public SubscriptionPlanEntitlementServiceImpl(
            SubscriptionPlanEntitlementRepository entitlementRepository,
            SubscriptionPlanVersionRepository planVersionRepository,
            ServiceRepository serviceRepository) {

        this.entitlementRepository =
                entitlementRepository;

        this.planVersionRepository =
                planVersionRepository;

        this.serviceRepository =
                serviceRepository;
    }

    @Override
    public SubscriptionPlanEntitlementResponse
    createEntitlement(
            CreateSubscriptionPlanEntitlementRequest request) {

        SubscriptionPlanVersionEntity planVersion =
                planVersionRepository
                        .findById(request.getPlanVersionId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Plan version not found"));

        ServiceEntity service =
                serviceRepository
                        .findById(request.getServiceId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Service not found"));

        SubscriptionPlanEntitlementEntity entity =
                new SubscriptionPlanEntitlementEntity();

        entity.setPlanVersion(planVersion);
        entity.setService(service);
        entity.setQuantity(request.getQuantity());
        entity.setPeriodType(request.getPeriodType());
        entity.setCarryForwardAllowed(
                request.getCarryForwardAllowed());
        entity.setStatus(request.getStatus());

        return buildResponse(
                entitlementRepository.save(entity));
    }

    @Override
    public SubscriptionPlanEntitlementResponse
    getEntitlement(
            UUID entitlementId) {

        return buildResponse(
                entitlementRepository
                        .findById(entitlementId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Entitlement not found")));
    }

    @Override
    public List<SubscriptionPlanEntitlementResponse>
    getAllEntitlements() {

        return entitlementRepository
                .findAll()
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    @Override
    public SubscriptionPlanEntitlementResponse
    updateEntitlement(
            UUID entitlementId,
            UpdateSubscriptionPlanEntitlementRequest request) {

        SubscriptionPlanEntitlementEntity entity =
                entitlementRepository
                        .findById(entitlementId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Entitlement not found"));

        entity.setQuantity(request.getQuantity());
        entity.setPeriodType(request.getPeriodType());
        entity.setCarryForwardAllowed(
                request.getCarryForwardAllowed());
        entity.setStatus(request.getStatus());

        return buildResponse(
                entitlementRepository.save(entity));
    }

    private SubscriptionPlanEntitlementResponse
    buildResponse(
            SubscriptionPlanEntitlementEntity entity) {

        SubscriptionPlanEntitlementResponse response =
                new SubscriptionPlanEntitlementResponse();

        response.setEntitlementId(
                entity.getEntitlementId());

        response.setPlanVersionId(
                entity.getPlanVersion()
                        .getPlanVersionId());

        response.setServiceId(
                entity.getService()
                        .getServiceId());

        response.setQuantity(
                entity.getQuantity());

        response.setPeriodType(
                entity.getPeriodType());

        response.setCarryForwardAllowed(
                entity.getCarryForwardAllowed());

        response.setStatus(
                entity.getStatus());

        return response;
    }
}