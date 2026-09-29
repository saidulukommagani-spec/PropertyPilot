package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateSubscriptionBenefitRequest;
import com.propertypilot.application.dto.SubscriptionBenefitResponse;
import com.propertypilot.application.dto.UpdateSubscriptionBenefitRequest;
import com.propertypilot.application.service.SubscriptionBenefitService;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionBenefitEntity;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionBenefitRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SubscriptionBenefitServiceImpl
        implements SubscriptionBenefitService {

    private final SubscriptionBenefitRepository
            subscriptionBenefitRepository;

    public SubscriptionBenefitServiceImpl(
            SubscriptionBenefitRepository
                    subscriptionBenefitRepository) {

        this.subscriptionBenefitRepository =
                subscriptionBenefitRepository;
    }

    @Override
    public SubscriptionBenefitResponse createBenefit(
            CreateSubscriptionBenefitRequest request) {

        SubscriptionBenefitEntity benefit =
                new SubscriptionBenefitEntity();

        benefit.setBenefitId(
                UUID.randomUUID());

        benefit.setPlanId(
                request.getPlanId());

        benefit.setBenefitType(
                request.getBenefitType());

        benefit.setBenefitValue(
                request.getBenefitValue());

        benefit.setStatus(
                request.getStatus());

        return buildResponse(
                subscriptionBenefitRepository.save(
                        benefit));
    }

    @Override
    public SubscriptionBenefitResponse getBenefit(
            UUID benefitId) {

        return buildResponse(
                subscriptionBenefitRepository
                        .findById(benefitId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Subscription benefit not found")));
    }

    @Override
    public List<SubscriptionBenefitResponse>
    getAllBenefits() {

        return subscriptionBenefitRepository
                .findAll()
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    @Override
    public SubscriptionBenefitResponse updateBenefit(
            UUID benefitId,
            UpdateSubscriptionBenefitRequest request) {

        SubscriptionBenefitEntity benefit =
                subscriptionBenefitRepository
                        .findById(benefitId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Subscription benefit not found"));

        benefit.setBenefitType(
                request.getBenefitType());

        benefit.setBenefitValue(
                request.getBenefitValue());

        benefit.setStatus(
                request.getStatus());

        return buildResponse(
                subscriptionBenefitRepository.save(
                        benefit));
    }

    private SubscriptionBenefitResponse
    buildResponse(
            SubscriptionBenefitEntity benefit) {

        SubscriptionBenefitResponse response =
                new SubscriptionBenefitResponse();

        response.setBenefitId(
                benefit.getBenefitId());

        response.setPlanId(
                benefit.getPlanId());

        response.setBenefitType(
                benefit.getBenefitType());

        response.setBenefitValue(
                benefit.getBenefitValue());

        response.setStatus(
                benefit.getStatus());

        return response;
    }
}