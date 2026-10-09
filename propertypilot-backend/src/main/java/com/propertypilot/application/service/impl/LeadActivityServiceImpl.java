package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.LeadActivityCreateRequest;
import com.propertypilot.application.dto.LeadActivityResponse;
import com.propertypilot.application.service.LeadActivityService;
import com.propertypilot.infrastructure.persistence.entity.LeadActivityEntity;
import com.propertypilot.infrastructure.persistence.entity.LeadEntity;
import com.propertypilot.infrastructure.persistence.entity.UserEntity;
import com.propertypilot.infrastructure.persistence.repository.LeadActivityRepository;
import com.propertypilot.infrastructure.persistence.repository.LeadRepository;
import com.propertypilot.security.SecurityService;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;

@Service
@Transactional
public class LeadActivityServiceImpl
        implements LeadActivityService {

    private final LeadActivityRepository leadActivityRepository;

    private final LeadRepository leadRepository;

    private final SecurityService securityService;

    public LeadActivityServiceImpl(
            LeadActivityRepository leadActivityRepository,
            LeadRepository leadRepository,
            SecurityService securityService) {

        this.leadActivityRepository =
                leadActivityRepository;

        this.leadRepository =
                leadRepository;

        this.securityService =
                securityService;
    }

    @Override
    public LeadActivityResponse createActivity(
            UUID leadId,
            LeadActivityCreateRequest request) {

        LeadEntity lead =
                leadRepository
                        .findByLeadId(leadId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Lead not found"));

        securityService
                .validateLeadOwnership(lead);

        UserEntity currentUser =
                securityService
                        .getCurrentUser();

        LeadActivityEntity activity =
                new LeadActivityEntity();

        activity.setLead(lead);

        activity.setActivityType(
                request.activityType());

        activity.setSubject(
                request.subject());

        activity.setDetails(
                request.details());

        activity.setOutcome(
                request.outcome());

        activity.setOccurredAt(
                OffsetDateTime.now());

        activity.setNextActionAt(
                request.nextActionAt());

        // The caller cannot choose visibility. Customer-authored entries on
        // their own lead are visible to that customer; all staff entries are internal.
        activity.setVisibility(!securityService.isOperationsUser()
                && !securityService.hasRole("AGENT")
                && securityService.hasRole("CUSTOMER")
                ? "CUSTOMER_VISIBLE" : "INTERNAL");

        activity.setPerformedBy(
                currentUser);

        LeadActivityEntity saved =
                leadActivityRepository
                        .save(activity);

        return buildResponse(saved);
    }

    @Override
    public List<LeadActivityResponse> getActivities(
            UUID leadId) {

        LeadEntity lead =
                leadRepository
                        .findByLeadId(leadId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Lead not found"));

        securityService.validateLeadOwnership(lead);

        boolean customer = securityService.hasRole("CUSTOMER")
                && !securityService.isOperationsUser();

        List<LeadActivityEntity> activities = customer
                ? leadActivityRepository.findByLead_LeadIdAndVisibilityOrderByOccurredAtDesc(
                        leadId, "CUSTOMER_VISIBLE")
                : leadActivityRepository.findByLead_LeadIdOrderByOccurredAtDesc(leadId);

        return activities.stream()
                .map(this::buildResponse)
                .toList();
    }

    private LeadActivityResponse buildResponse(
            LeadActivityEntity activity) {

        return new LeadActivityResponse(

                activity.getLeadActivityId(),

                activity.getLead()
                        .getLeadId(),

                activity.getActivityType(),

                activity.getSubject(),

                activity.getDetails(),

                activity.getOutcome(),

                activity.getPerformedBy() != null
                        ? activity.getPerformedBy()
                                .getEmail()
                        : null,

                activity.getOccurredAt(),

                activity.getNextActionAt(),

                activity.getVisibility() == null ? "INTERNAL" : activity.getVisibility()
        );
    }
}
