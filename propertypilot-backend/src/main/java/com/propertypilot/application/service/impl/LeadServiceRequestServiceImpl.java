package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.LeadServiceRequestResponse;
import com.propertypilot.application.dto.ServiceRequestAssignmentHistoryResponse;
import com.propertypilot.application.dto.ServiceRequestStatusHistoryResponse;
import com.propertypilot.application.dto.ServiceRequestRevenueSummary;
import com.propertypilot.application.service.LeadServiceRequestService;
import com.propertypilot.domain.enums.BillingEntityType;
import com.propertypilot.domain.enums.InvoiceStatus;
import com.propertypilot.domain.enums.PaymentStatus;
import com.propertypilot.infrastructure.persistence.entity.InvoiceEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceSlaPolicyEntity;
import com.propertypilot.infrastructure.persistence.repository.AgentAssignmentRepository;
import com.propertypilot.infrastructure.persistence.repository.EvidenceRepository;
import com.propertypilot.infrastructure.persistence.repository.InvoiceRepository;
import com.propertypilot.infrastructure.persistence.repository.LeadRepository;
import com.propertypilot.infrastructure.persistence.repository.PaymentRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestStatusHistoryRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceSlaPolicyRepository;
import com.propertypilot.infrastructure.persistence.repository.VisitRepository;
import com.propertypilot.security.SecurityService;
import com.propertypilot.web.exception.ResourceNotFoundException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

@Service
@Transactional(readOnly = true)
public class LeadServiceRequestServiceImpl implements LeadServiceRequestService {
    private final LeadRepository leadRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final ServiceRequestStatusHistoryRepository statusHistoryRepository;
    private final ServiceSlaPolicyRepository slaPolicyRepository;
    private final AgentAssignmentRepository assignmentRepository;
    private final VisitRepository visitRepository;
    private final EvidenceRepository evidenceRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final SecurityService securityService;

    public LeadServiceRequestServiceImpl(
            LeadRepository leadRepository,
            ServiceRequestRepository serviceRequestRepository,
            ServiceRequestStatusHistoryRepository statusHistoryRepository,
            ServiceSlaPolicyRepository slaPolicyRepository,
            AgentAssignmentRepository assignmentRepository,
            VisitRepository visitRepository,
            EvidenceRepository evidenceRepository,
            InvoiceRepository invoiceRepository,
            PaymentRepository paymentRepository,
            SecurityService securityService) {
        this.leadRepository = leadRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.slaPolicyRepository = slaPolicyRepository;
        this.assignmentRepository = assignmentRepository;
        this.visitRepository = visitRepository;
        this.evidenceRepository = evidenceRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.securityService = securityService;
    }

    @Override
    public List<LeadServiceRequestResponse> getLeadServiceRequests(UUID leadId) {
        var lead = leadRepository.findByLeadId(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found"));
        securityService.validateLeadOwnership(lead);
        List<ServiceRequestEntity> linkedRequests = serviceRequestRepository.findByLead_LeadIdOrderByRequestedAtDesc(leadId);
        if (securityService.hasRole("AGENT")) {
            linkedRequests = linkedRequests.stream()
                    .filter(this::isCurrentUserAllowedToSeeRequest)
                    .toList();
        }
        return linkedRequests
                .stream().map(this::toResponse).toList();
    }

    private boolean isCurrentUserAllowedToSeeRequest(ServiceRequestEntity request) {
        try {
            securityService.validateServiceRequestAccess(request);
            return true;
        } catch (AccessDeniedException exception) {
            return false;
        }
    }

    private LeadServiceRequestResponse toResponse(ServiceRequestEntity request) {
        UUID requestId = request.getServiceRequestId();
        UUID serviceId = request.getService() == null ? null : request.getService().getServiceId();
        Instant dueAt = request.getSlaDueAt();
        if (!request.isSlaSnapshotCaptured()) {
            ServiceSlaPolicyEntity policy = serviceId == null ? null : slaPolicyRepository
                    .findByService_ServiceIdAndStatusIgnoreCaseOrderByCreatedAtDesc(serviceId, "ACTIVE")
                    .stream().findFirst().orElse(null);
            dueAt = policy == null || request.getRequestedAt() == null ? null
                    : request.getRequestedAt().plusSeconds(policy.getCompletionMinutes().longValue() * 60);
        }
        String slaStatus = dueAt == null ? "NO_POLICY"
                : request.getCompletedAt() != null
                        ? (request.getCompletedAt().isBefore(dueAt) || request.getCompletedAt().equals(dueAt) ? "MET" : "BREACHED")
                        : Instant.now().isAfter(dueAt) ? "BREACHED" : "ON_TRACK";
        String agentName = assignmentRepository
                .findByServiceRequest_ServiceRequestIdAndAssignmentStatusIn(requestId, List.of("ASSIGNED", "ACCEPTED", "IN_PROGRESS"))
                .map(assignment -> assignment.getAgent().getUser().getFullName())
                .orElse(null);

        java.util.Map<String, BigDecimal[]> revenue = new java.util.LinkedHashMap<>();
        for (InvoiceEntity invoice : invoiceRepository.findByEntityTypeAndEntityId(BillingEntityType.SERVICE_REQUEST, requestId)) {
            if (invoice.getStatus() != InvoiceStatus.CANCELLED) {
                String currency = invoice.getCurrencyCode() == null ? "INR" : invoice.getCurrencyCode().trim();
                BigDecimal[] amounts = revenue.computeIfAbsent(currency, ignored -> new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ZERO });
                amounts[0] = amounts[0].add(invoice.getInvoiceAmount() == null ? BigDecimal.ZERO : invoice.getInvoiceAmount());
                amounts[1] = amounts[1].add(paymentRepository.sumVerifiedSettledAmountByInvoiceAndCurrency(
                        invoice.getInvoiceId(), PaymentStatus.SUCCESS, invoice.getCurrencyCode()));
            }
        }

        return new LeadServiceRequestResponse(
                requestId,
                request.getLead().getLeadId(),
                request.getProperty() == null ? null : request.getProperty().getPropertyId(),
                serviceId,
                request.getPricingEstimate() == null ? null : request.getPricingEstimate().getPricingEstimateId(),
                request.getService() == null ? request.getRequestType() : request.getService().getServiceName(),
                request.getStatus(),
                request.getPriority(),
                agentName,
                request.getRequestedAt(),
                request.getScheduledAt(),
                request.getCompletedAt(),
                dueAt,
                slaStatus,
                visitRepository.countByServiceRequest_ServiceRequestId(requestId),
                evidenceRepository.countAllForServiceRequest(requestId),
                request.getAmount(),
                request.getPricingEstimate() == null ? null : request.getPricingEstimate().getCurrencyCode(),
                revenue.entrySet().stream().map(entry -> new ServiceRequestRevenueSummary(entry.getKey(), entry.getValue()[0], entry.getValue()[1])).toList(),
                statusHistoryRepository.findByServiceRequest_ServiceRequestIdOrderByChangedAtDesc(requestId).stream().map(row -> {
                    ServiceRequestStatusHistoryResponse history = new ServiceRequestStatusHistoryResponse();
                    history.setPreviousStatus(row.getPreviousStatus());
                    history.setNewStatus(row.getNewStatus());
                    history.setChangeReason(row.getChangeReason());
                    history.setChangedAt(row.getChangedAt());
                    return history;
                }).toList(),
                assignmentRepository.findByServiceRequest_ServiceRequestIdOrderByAssignedAtAsc(requestId).stream().map(assignment ->
                        new ServiceRequestAssignmentHistoryResponse(
                                assignment.getAssignmentId(),
                                "ASSIGNMENT",
                                assignment.getAgent() == null ? null : assignment.getAgent().getAgentId(),
                                assignment.getAgent() == null || assignment.getAgent().getUser() == null ? null : assignment.getAgent().getUser().getFullName(),
                                assignment.getAgent() == null ? null : assignment.getAgent().getAgentCode(),
                                null,
                                assignment.getCreatedBy(),
                                assignment.getAssignmentStatus(),
                                assignment.getAssignmentNotes(),
                                assignment.getAssignedAt(),
                                assignment.getRespondedAt())).toList());
    }
}
