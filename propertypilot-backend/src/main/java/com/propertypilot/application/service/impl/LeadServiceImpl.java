package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.LeadCreateRequest;
import com.propertypilot.application.dto.LeadResponse;
import com.propertypilot.application.service.LeadService;
import com.propertypilot.domain.enums.LeadStatus;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.LeadEntity;
import com.propertypilot.infrastructure.persistence.repository.LeadRepository;
import com.propertypilot.security.SecurityService;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.propertypilot.application.dto.LeadAssignRequest;
import com.propertypilot.application.dto.LeadAssignmentResponse;
import com.propertypilot.infrastructure.persistence.entity.AgentEntity;
import com.propertypilot.infrastructure.persistence.repository.AgentRepository;
import com.propertypilot.infrastructure.persistence.entity.LeadActivityEntity;
import com.propertypilot.infrastructure.persistence.repository.LeadActivityRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class LeadServiceImpl
        implements LeadService {

    private final LeadRepository leadRepository;

    private final SecurityService securityService;
private final AgentRepository agentRepository;

private final LeadActivityRepository leadActivityRepository;



 public LeadServiceImpl(
        LeadRepository leadRepository,
        SecurityService securityService,
        AgentRepository agentRepository,
        LeadActivityRepository leadActivityRepository) {

        this.leadRepository =
                leadRepository;

        this.securityService =
                securityService;

    this.agentRepository =
            agentRepository;

            this.leadActivityRepository =
        leadActivityRepository;
    }

    @Override
    public LeadResponse createLead(
            LeadCreateRequest request) {

        CustomerEntity customer =
                securityService
                        .getCurrentCustomer();

        LeadEntity lead =
                new LeadEntity();

        lead.setCustomer(
                customer);

        lead.setSource(
                request.source());

        lead.setStatus(
                LeadStatus.NEW);

        lead.setBudgetMin(
                request.budgetMin());

        lead.setBudgetMax(
                request.budgetMax());

        lead.setNotes(
                request.notes());

        LeadEntity saved =
                leadRepository.save(
                        lead);

                        createActivity(
        saved,
        "NOTE",
        "Lead Created",
        "Lead created from source : "
                + saved.getSource());

        return buildResponse(
                saved);
    }

    @Override
    public LeadResponse getLead(
            UUID leadId) {

        LeadEntity lead =
                leadRepository
                        .findByLeadId(
                                leadId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Lead not found"));

        securityService
                .validateLeadOwnership(
                        lead);

        return buildResponse(
                lead);
    }

    @Override
    public LeadResponse updateLeadStatus(
            UUID leadId,
            LeadStatus status) {

        securityService
                .validateAdminAccess();

        LeadEntity lead =
                leadRepository
                        .findByLeadId(
                                leadId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Lead not found"));

        lead.setStatus(
                status);

        if (status ==
                LeadStatus.CONTACTED &&
                lead.getContactedAt() == null) {

            lead.setContactedAt(
                    OffsetDateTime.now());
        }

        if (status ==
                LeadStatus.CONVERTED &&
                lead.getConvertedAt() == null) {

            lead.setConvertedAt(
                    OffsetDateTime.now());
        }

        LeadEntity saved =
                leadRepository.save(
                        lead);

                        createActivity(
        saved,
        "STATUS_CHANGE",
        "Lead Status Updated",
        "New Status : "
                + status.name());

        return buildResponse(
                saved);
    }

    @Override
    public List<LeadResponse> getMyLeads() {

        UUID customerId =
                securityService
                        .getCurrentCustomerId();

        return leadRepository
                .findByCustomer_CustomerIdOrderByCreatedAtDesc(
                        customerId)
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    @Override
public LeadAssignmentResponse assignLead(
        UUID leadId,
        LeadAssignRequest request) {

    securityService.validateAdminAccess();

    LeadEntity lead =
            leadRepository
                    .findByLeadId(leadId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Lead not found"));

    AgentEntity agent =
            agentRepository
                    .findById(
                            request.agentId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Agent not found"));

    lead.setAssignedAgent(agent);

    LeadEntity saved =
            leadRepository.save(lead);
createActivity(
        saved,
        "STATUS_CHANGE",
        "Lead Assigned",
        "Assigned to agent "
                + agent.getAgentCode());
    return new LeadAssignmentResponse(
            saved.getLeadId(),
            agent.getAgentId(),
            agent.getAgentCode(),
            saved.getStatus().name()
    );
}

private void createActivity(
        LeadEntity lead,
        String activityType,
        String subject,
        String details) {

    LeadActivityEntity activity =
            new LeadActivityEntity();

    activity.setLead(lead);

    activity.setActivityType(
            activityType);

    activity.setSubject(
            subject);

    activity.setDetails(
            details);

    activity.setOccurredAt(
            OffsetDateTime.now());

    activity.setPerformedBy(
            securityService.getCurrentUser());

    leadActivityRepository.save(
            activity);
}

   private LeadResponse buildResponse(
        LeadEntity lead) {

    return new LeadResponse(

            lead.getLeadId(),

            lead.getSource(),

            lead.getPropertyType(),

            lead.getLocation(),

            lead.getStatus().name(),

            lead.getBudgetMin(),

            lead.getBudgetMax(),

            lead.getNotes(),

            lead.getContactedAt(),

            lead.getConvertedAt(),

            lead.getAssignedAgent() != null
                    ? lead.getAssignedAgent()
                            .getAgentId()
                    : null,

            lead.getAssignedAgent() != null
                    ? lead.getAssignedAgent()
                            .getAgentCode()
                    : null
    );
}
}