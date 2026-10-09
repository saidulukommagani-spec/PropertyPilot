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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.propertypilot.application.dto.OperationsLeadResponse;

@Service
@Transactional
public class LeadServiceImpl
        implements LeadService {

    private static final Map<LeadStatus, List<LeadStatus>> LEAD_STATUS_TRANSITIONS = Map.of(
            LeadStatus.NEW, List.of(LeadStatus.CONTACTED, LeadStatus.LOST, LeadStatus.CLOSED),
            LeadStatus.CONTACTED, List.of(LeadStatus.QUALIFIED, LeadStatus.LOST, LeadStatus.CLOSED),
            LeadStatus.QUALIFIED, List.of(LeadStatus.READY_FOR_DEAL, LeadStatus.LOST, LeadStatus.CLOSED),
            LeadStatus.READY_FOR_DEAL, List.of(LeadStatus.QUALIFIED, LeadStatus.LOST, LeadStatus.CLOSED),
            LeadStatus.DEAL_CONVERTED, List.of(LeadStatus.CLOSED),
            LeadStatus.CONVERTED, List.of(LeadStatus.CLOSED),
            LeadStatus.LOST, List.of(),
            LeadStatus.CLOSED, List.of());

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

        if (status == LeadStatus.DEAL_CONVERTED || status == LeadStatus.CONVERTED) {
            throw new com.propertypilot.web.exception.BusinessException(
                    "Use the lead conversion workflow to mark a lead converted");
        }

        LeadEntity lead =
                leadRepository
                        .findByLeadId(
                                leadId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Lead not found"));

        LeadStatus previousStatus = lead.getStatus();
        if (status == null || !LEAD_STATUS_TRANSITIONS.getOrDefault(previousStatus, List.of()).contains(status)) {
            throw new com.propertypilot.web.exception.BusinessException(
                    "Invalid lead status transition from " + previousStatus + " to " + status);
        }
        lead.setStatus(status);

        if (status ==
                LeadStatus.CONTACTED &&
                lead.getContactedAt() == null) {

            lead.setContactedAt(
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
        Map<UUID, LeadEntity> personalLeads = new LinkedHashMap<>();
        if (securityService.hasRole("AGENT")) {
            UUID userId = securityService.getCurrentUserId();
            AgentEntity agent = agentRepository.findByUser_UserIdAndStatus(userId, "ACTIVE")
                    .orElseThrow(() -> new AccessDeniedException("An active agent profile is required"));
            leadRepository.findByAssignedAgent_AgentIdOrderByCreatedAtDesc(agent.getAgentId())
                    .forEach(lead -> personalLeads.put(lead.getLeadId(), lead));
        }
        try {
            UUID customerId = securityService.getCurrentCustomerId();
            leadRepository.findByCustomer_CustomerIdOrderByCreatedAtDesc(customerId)
                    .forEach(lead -> personalLeads.putIfAbsent(lead.getLeadId(), lead));
        } catch (ResourceNotFoundException exception) {
            if (personalLeads.isEmpty() && !securityService.hasRole("AGENT")) {
                throw exception;
            }
        }

        return personalLeads.values().stream()
                .sorted(Comparator.comparing(LeadEntity::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
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

@Override
@Transactional(readOnly = true)
public Page<OperationsLeadResponse> searchOperationsLeads(String search, LeadStatus status, Pageable pageable) {
    securityService.validateOperationsAccess();
    String normalizedSearch = search == null ? "" : search.trim();
    return leadRepository.searchForOperations(normalizedSearch, status, pageable)
            .map(lead -> new OperationsLeadResponse(
                    lead.getLeadId(), lead.getSource(), lead.getPropertyType(), lead.getLocation(),
                    lead.getStatus() == null ? null : lead.getStatus().name(),
                    lead.getCustomer() != null && lead.getCustomer().getUser() != null
                            ? lead.getCustomer().getUser().getFullName() : null,
                    lead.getCustomer() != null && lead.getCustomer().getUser() != null
                            ? lead.getCustomer().getUser().getEmail() : null,
                    lead.getCustomer() != null && lead.getCustomer().getUser() != null
                            ? lead.getCustomer().getUser().getMobileNumber() : null,
                    lead.getAssignedAgent() == null ? null : lead.getAssignedAgent().getAgentCode(),
                    lead.getCreatedAt()));
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

    activity.setVisibility("INTERNAL");

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
                    : null,
            lead.getCustomer() == null ? null : lead.getCustomer().getCustomerId()
    );
}
}
