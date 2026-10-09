package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateServiceRequestRequest;
import com.propertypilot.application.dto.ServiceRequestResponse;
import com.propertypilot.application.dto.ServiceRequestAssignmentHistoryResponse;
import com.propertypilot.application.dto.UpdateServiceRequestRequest;
import com.propertypilot.application.service.CompletionValidationService;
import com.propertypilot.application.service.BillingService;
import com.propertypilot.application.service.EligibilityEngine;
import com.propertypilot.application.service.ServiceRequestService;
import com.propertypilot.domain.enums.ServiceRequestStatus;
import com.propertypilot.infrastructure.persistence.entity.AgentAssignmentEntity;
import com.propertypilot.infrastructure.persistence.entity.AgentEntity;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.Property;
import com.propertypilot.infrastructure.persistence.entity.ServiceEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;
import com.propertypilot.infrastructure.persistence.entity.PricingEstimateEntity;
import com.propertypilot.infrastructure.persistence.entity.LeadEntity;
import com.propertypilot.infrastructure.persistence.entity.LeadActivityEntity;
import com.propertypilot.infrastructure.persistence.repository.AgentAssignmentRepository;
import com.propertypilot.infrastructure.persistence.repository.AgentRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestRepository;
import com.propertypilot.infrastructure.persistence.repository.PricingEstimateRepository;
import com.propertypilot.infrastructure.persistence.repository.UserRepository;
import com.propertypilot.infrastructure.persistence.repository.LeadRepository;
import com.propertypilot.infrastructure.persistence.repository.LeadActivityRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceSlaPolicyRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestStatusHistoryEntity;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestStatusHistoryRepository;
import com.propertypilot.web.exception.BusinessException;
import com.propertypilot.security.SecurityService;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;



@Service
@Transactional
public class ServiceRequestServiceImpl
        implements ServiceRequestService {

    private final SecurityService securityService;

    private final ServiceRequestRepository
            serviceRequestRepository;

    private final ServiceRequestStatusHistoryRepository
            statusHistoryRepository;
private final EligibilityEngine
        eligibilityEngine;

        private final AgentRepository
        agentRepository;

private final AgentAssignmentRepository
        agentAssignmentRepository;

        private final PricingEstimateRepository pricingEstimateRepository;

        private final CompletionValidationService
        completionValidationService;
    private final BillingService billingService;

    private final UserRepository userRepository;
    private final LeadRepository leadRepository;
    private final LeadActivityRepository leadActivityRepository;
    private final ServiceSlaPolicyRepository slaPolicyRepository;

    public ServiceRequestServiceImpl(
        ServiceRequestRepository serviceRequestRepository,
        ServiceRequestStatusHistoryRepository statusHistoryRepository,
        EligibilityEngine eligibilityEngine,
AgentRepository agentRepository,
AgentAssignmentRepository agentAssignmentRepository,
        PricingEstimateRepository pricingEstimateRepository,
        CompletionValidationService completionValidationService,
        BillingService billingService,
        SecurityService securityService,
        UserRepository userRepository,
        LeadRepository leadRepository,
        LeadActivityRepository leadActivityRepository,
        ServiceSlaPolicyRepository slaPolicyRepository) {

        this.serviceRequestRepository =
                serviceRequestRepository;

        this.statusHistoryRepository =
                statusHistoryRepository;

                this.eligibilityEngine =
        eligibilityEngine;

        this.agentRepository =
        agentRepository;

this.agentAssignmentRepository = agentAssignmentRepository;

        this.pricingEstimateRepository = pricingEstimateRepository;
        this.completionValidationService =
        completionValidationService;
        this.billingService = billingService;
        this.securityService = securityService;
        this.userRepository = userRepository;
        this.leadRepository = leadRepository;
        this.leadActivityRepository = leadActivityRepository;
        this.slaPolicyRepository = slaPolicyRepository;
             
    }

    @Override
    public ServiceRequestResponse createRequest(
            CreateServiceRequestRequest request) {
        if (request == null || request.getQuoteId() == null) {
            throw new BusinessException("An accepted quote is required to create a service request");
        }

        PricingEstimateEntity quote = pricingEstimateRepository.findByIdForUpdate(request.getQuoteId())
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found"));
        securityService.validateCustomerAccess(quote.getCustomer());
        if (!"ACCEPTED".equals(quote.getStatus()) || quote.getAcceptedAt() == null
                || quote.getAcceptedAt().isAfter(quote.getExpiresAt())) {
            throw new BusinessException("Only a valid accepted quote can create a service request");
        }
        if (serviceRequestRepository.existsByPricingEstimate_PricingEstimateId(quote.getPricingEstimateId())) {
            throw new BusinessException("A service request already exists for this quote");
        }

        CustomerEntity customer = quote.getCustomer();
        Property property = quote.getProperty();
        ServiceEntity service = quote.getService();
        if (customer == null || property == null || service == null
                || property.getCustomer() == null
                || !customer.getCustomerId().equals(property.getCustomer().getCustomerId())) {
            throw new BusinessException("The accepted quote has incomplete or inconsistent customer/property/service data");
        }
        securityService.validatePropertyOwnership(property);
        LeadEntity lead = null;
        if (request.getLeadId() != null) {
            lead = leadRepository.findByLeadId(request.getLeadId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lead not found"));
            securityService.validateLeadOwnership(lead);
            if (lead.getCustomer() == null || !customer.getCustomerId().equals(lead.getCustomer().getCustomerId())) {
                throw new BusinessException("The selected lead and accepted quote must belong to the same customer");
            }
        }
        if (!Boolean.TRUE.equals(service.getActiveFlag())) {
            throw new BusinessException("The quoted service is no longer available for request creation");
        }
        eligibilityEngine.validateEligibility(service.getServiceId(), property.getPropertyId());

        ServiceRequestEntity entity = new ServiceRequestEntity();
        entity.setPricingEstimate(quote);
        entity.setLead(lead);
        entity.setCustomer(customer);
        entity.setProperty(property);
        entity.setService(service);
        entity.setRequestType(service.getServiceCode() == null ? service.getServiceName() : service.getServiceCode());
        entity.setPriority(request.getPriority() == null ? "MEDIUM" : request.getPriority());
        entity.setAmount(quote.getTotalAmount());
        entity.setDescription(request.getDescription());
        entity.setScheduledAt(request.getPreferredDate());
        entity.setStatus(ServiceRequestStatus.NEW.name());
        entity.setRequestedAt(Instant.now());
        captureSlaSnapshot(entity);

        ServiceRequestEntity saved;
        try {
            saved = serviceRequestRepository.saveAndFlush(entity);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException("A service request already exists for this quote");
        }
        createStatusHistory(saved, null, ServiceRequestStatus.NEW.name(), "Created from accepted quote");
        if (lead != null) {
            LeadActivityEntity activity = new LeadActivityEntity();
            activity.setLead(lead);
            activity.setActivityType("SERVICE_REQUEST_CREATED");
            activity.setSubject("Service request created");
            activity.setDetails("Service request " + saved.getServiceRequestId() + " created from accepted quote " + quote.getPricingEstimateId());
            activity.setVisibility("INTERNAL");
            activity.setOccurredAt(java.time.OffsetDateTime.now());
            activity.setPerformedBy(securityService.getCurrentUser());
            leadActivityRepository.save(activity);
        }
        return buildResponse(saved);
}

    @Override
    public ServiceRequestResponse createEntitledMonitoringRequest(
            CustomerEntity customer, Property property, ServiceEntity service, String reason) {
        if (customer == null || property == null || service == null
                || property.getCustomer() == null
                || !customer.getCustomerId().equals(property.getCustomer().getCustomerId())) {
            throw new BusinessException("Monitoring request customer and property must match");
        }
        if (!Boolean.TRUE.equals(service.getActiveFlag())
                || ((service.getCategory() == null || !"MONITORING".equalsIgnoreCase(service.getCategory()))
                && (service.getServiceCode() == null || !service.getServiceCode().toUpperCase().startsWith("MONITOR")))) {
            throw new BusinessException("An active Monitoring service is required");
        }
        eligibilityEngine.validateEligibility(service.getServiceId(), property.getPropertyId());

        ServiceRequestEntity entity = new ServiceRequestEntity();
        entity.setCustomer(customer);
        entity.setProperty(property);
        entity.setService(service);
        entity.setRequestType(service.getServiceCode() == null ? service.getServiceName() : service.getServiceCode());
        entity.setPriority("MEDIUM");
        entity.setAmount(java.math.BigDecimal.ZERO);
        entity.setDescription(reason);
        entity.setStatus(ServiceRequestStatus.NEW.name());
        entity.setRequestedAt(Instant.now());
        captureSlaSnapshot(entity);
        ServiceRequestEntity saved = serviceRequestRepository.saveAndFlush(entity);
        createSystemStatusHistory(saved, ServiceRequestStatus.NEW.name(), reason);
        return buildResponse(saved);
    }

    @Override
    public ServiceRequestResponse getRequest(
            UUID serviceRequestId) {
        ServiceRequestEntity request = serviceRequestRepository
                        .findById(serviceRequestId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Service request not found"));
        securityService.validateServiceRequestAccess(request);
        ServiceRequestResponse response = buildResponse(request);
        response.setStatusHistory(loadStatusHistory(serviceRequestId));
        return response;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<ServiceRequestResponse> getAllRequestsForOperations() {
        securityService.validateOperationsAccess();
        return serviceRequestRepository.findAllForAdminDashboard().stream()
                .map(this::buildResponse)
                .toList();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<ServiceRequestResponse> getRequestsForCurrentAgent() {
        if (!securityService.hasRole("AGENT")) {
            throw new org.springframework.security.access.AccessDeniedException("Agent access required");
        }
        AgentEntity agent = agentRepository.findByUser_UserIdAndStatus(securityService.getCurrentUserId(), "ACTIVE")
                .orElseThrow(() -> new ResourceNotFoundException("Active agent profile not found"));
        return agentAssignmentRepository.findByAgent_AgentId(agent.getAgentId()).stream()
                .filter(assignment -> List.of("ASSIGNED", "ACCEPTED", "IN_PROGRESS", "COMPLETED")
                        .contains(assignment.getAssignmentStatus()))
                .map(AgentAssignmentEntity::getServiceRequest)
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toMap(ServiceRequestEntity::getServiceRequestId,
                        request -> request, (first, ignored) -> first, java.util.LinkedHashMap::new))
                .values().stream()
                .sorted(java.util.Comparator.comparing(ServiceRequestEntity::getRequestedAt,
                        java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder())))
                .map(this::buildResponse)
                .toList();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public ServiceRequestResponse getRequestForOperations(UUID serviceRequestId) {
        securityService.validateOperationsAccess();
        ServiceRequestEntity request = serviceRequestRepository.findByIdForAdmin(serviceRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Service request not found"));
        ServiceRequestResponse response = buildResponse(request);
        response.setStatusHistory(loadStatusHistory(serviceRequestId));
        return response;
    }

    private List<com.propertypilot.application.dto.ServiceRequestStatusHistoryResponse> loadStatusHistory(UUID serviceRequestId) {
        return statusHistoryRepository.findByServiceRequest_ServiceRequestIdOrderByChangedAtDesc(serviceRequestId).stream()
                .map(history -> {
                    com.propertypilot.application.dto.ServiceRequestStatusHistoryResponse item =
                            new com.propertypilot.application.dto.ServiceRequestStatusHistoryResponse();
                    item.setPreviousStatus(history.getPreviousStatus());
                    item.setNewStatus(history.getNewStatus());
                    item.setChangeReason(history.getChangeReason());
                    item.setChangedAt(history.getChangedAt());
                    return item;
                }).toList();
    }

    @Override
    public List<ServiceRequestResponse>
    getCustomerRequests(
            UUID customerId) {

        securityService.validateCustomerAccess(customerId);

        return serviceRequestRepository
                .findByCustomerWithProperty(customerId)
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    @Override
    public ServiceRequestResponse updateRequest(
            UUID serviceRequestId,
            UpdateServiceRequestRequest request) {

        securityService.validateOperationsAccess();
        if (request.getStatus() != null || request.getCompletedAt() != null
                || request.getAssignedTo() != null || request.getAssignedBy() != null
                || request.getCancellationReasonCode() != null) {
            throw new BusinessException(
                    "Use the service request workflow actions to change status, assignment, completion, or cancellation");
        }

        ServiceRequestEntity entity =
                serviceRequestRepository
                        .findByIdForUpdate(serviceRequestId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Service request not found"));

        entity.setScheduledAt(
                request.getScheduledAt());

        if (request.getPriority() != null) entity.setPriority(request.getPriority());
        if (request.getDescription() != null) entity.setDescription(request.getDescription());

        return buildResponse(
                serviceRequestRepository.save(entity));
    }
    private void validateTransition(
        String currentStatus,
        String targetStatus) {

    if (ServiceRequestStatus.COMPLETED.name()
            .equals(currentStatus)
            ||
            ServiceRequestStatus.CANCELLED.name()
                    .equals(currentStatus)) {

        throw new BusinessException(
                "Terminal state reached: "
                        + currentStatus);
    }

    boolean valid =

            (ServiceRequestStatus.NEW.name()
                    .equals(currentStatus)
                    &&
                    (
                            ServiceRequestStatus.ASSIGNED.name()
                                    .equals(targetStatus)
                                    ||
                            ServiceRequestStatus.CANCELLED.name()
                                    .equals(targetStatus)
                    ))

            ||

            (ServiceRequestStatus.PENDING_ASSIGNMENT.name()
                    .equals(currentStatus)
                    && ServiceRequestStatus.ASSIGNED.name().equals(targetStatus))

            ||

            (ServiceRequestStatus.ASSIGNED.name()
                    .equals(currentStatus)
                    &&
                    (
                            ServiceRequestStatus.ACCEPTED.name()
                                    .equals(targetStatus)
                                    ||
                            ServiceRequestStatus.PENDING_ASSIGNMENT.name()
                                    .equals(targetStatus)
                                    ||
                            ServiceRequestStatus.CANCELLED.name()
                                    .equals(targetStatus)
                    ))

            ||

            (ServiceRequestStatus.ACCEPTED.name()
                    .equals(currentStatus)
                    &&
                    (
                            ServiceRequestStatus.IN_PROGRESS.name()
                                    .equals(targetStatus)
                                    ||
                            ServiceRequestStatus.CANCELLED.name()
                                    .equals(targetStatus)
                    ))

            ||

            (ServiceRequestStatus.IN_PROGRESS.name()
                    .equals(currentStatus)
                    &&
                    (
                            ServiceRequestStatus.COMPLETED.name()
                                    .equals(targetStatus)
                                    ||
                            ServiceRequestStatus.CANCELLED.name()
                                    .equals(targetStatus)
                    ));

    if (!valid) {

        throw new BusinessException(
                "Invalid transition from "
                        + currentStatus
                        + " to "
                        + targetStatus);
    }
}
@Override
public ServiceRequestResponse assignRequest(
        UUID serviceRequestId,
        UUID assignedTo,
        UUID assignedBy,
        String reason) {

    ServiceRequestEntity entity =
            serviceRequestRepository
                    .findByIdForUpdate(serviceRequestId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Service request not found"));
    securityService.validateOperationsAccess();

    validateTransition(
            entity.getStatus(),
            ServiceRequestStatus.ASSIGNED.name());

    entity.setAssignedTo(
            assignedTo);

    entity.setAssignedBy(securityService.getCurrentUserId());

    entity.setAssignedAt(
            Instant.now());

    String previousStatus =
            entity.getStatus();

    entity.setStatus(
            ServiceRequestStatus.ASSIGNED.name());

    createStatusHistory(
            entity,
            previousStatus,
            ServiceRequestStatus.ASSIGNED.name(),
            reason);

    return buildResponse(
            serviceRequestRepository.save(entity));
}

@Override
public ServiceRequestResponse completeRequest(
        UUID serviceRequestId,
        String reason) {

    ServiceRequestEntity request =
            serviceRequestRepository
                    .findByIdForUpdate(serviceRequestId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Service request not found"));
    if (!ServiceRequestStatus.IN_PROGRESS.name()
            .equals(request.getStatus())) {

        throw new BusinessException(
                "Only IN_PROGRESS requests can be completed");
    }

    AgentAssignmentEntity activeAssignment = agentAssignmentRepository
            .findByServiceRequest_ServiceRequestIdAndAssignmentStatus(serviceRequestId, "IN_PROGRESS")
            .orElseThrow(() -> new ResourceNotFoundException("In-progress assignment not found"));
    securityService.validateAssignmentAccess(activeAssignment);

    completionValidationService
        .validateCompletionRequirements(
                request);

    ServiceRequestResponse response = changeStatus(
            request,
        ServiceRequestStatus.COMPLETED.name(),
        reason);
    activeAssignment.setAssignmentStatus("COMPLETED");
    activeAssignment.setEndedAt(OffsetDateTime.now());
    agentAssignmentRepository.save(activeAssignment);
    if (request.getPricingEstimate() != null && request.getService() != null
            && Boolean.TRUE.equals(request.getService().getBillable())
            && request.getAmount() != null && request.getAmount().signum() > 0) {
        billingService.createInvoiceFromCompletedServiceRequest(request);
    }
    return response;
}

@Override
public void completeRequestIfReady(UUID serviceRequestId, String reason) {
    ServiceRequestEntity request = serviceRequestRepository.findById(serviceRequestId)
            .orElseThrow(() -> new ResourceNotFoundException("Service request not found"));
    if (ServiceRequestStatus.IN_PROGRESS.name().equals(request.getStatus())
            && completionValidationService.isCompletionReady(request)) {
        completeRequest(serviceRequestId, reason);
    }
}
@Override
public ServiceRequestResponse cancelRequest(
        UUID serviceRequestId,
        String reason) {

    ServiceRequestEntity request = serviceRequestRepository.findByIdForUpdate(serviceRequestId)
            .orElseThrow(() -> new ResourceNotFoundException("Service request not found"));
    if (!securityService.isOperationsUser() && !securityService.hasRole("CUSTOMER")) {
        throw new AccessDeniedException("Only the customer or operations team can cancel a service request");
    }
    if (securityService.hasRole("CUSTOMER")) {
        securityService.validateCustomerAccess(request.getCustomer());
    }
    return changeStatus(
            request,
            ServiceRequestStatus.CANCELLED.name(),
            reason);
}
@Override
public ServiceRequestResponse assignAgent(
        UUID serviceRequestId,
        UUID agentId) {

    return assignAgentWithNotes(serviceRequestId, agentId, null);
}

@Override
public ServiceRequestResponse assignAgentWithNotes(UUID serviceRequestId, UUID agentId, String notes) {
    ServiceRequestEntity request = serviceRequestRepository.findByIdForUpdate(serviceRequestId)
            .orElseThrow(() -> new ResourceNotFoundException("Service request not found"));
    securityService.validateOperationsAccess();

    if (!List.of(ServiceRequestStatus.NEW.name(), ServiceRequestStatus.PENDING_ASSIGNMENT.name(),
            ServiceRequestStatus.ASSIGNED.name(), ServiceRequestStatus.ACCEPTED.name()).contains(request.getStatus())) {
        throw new BusinessException("An agent cannot be assigned to a request in status " + request.getStatus());
    }

    AgentEntity agent = agentRepository.findByAgentIdAndStatus(agentId, "ACTIVE")
            .orElseThrow(() -> new ResourceNotFoundException("Active agent not found"));

    Optional<AgentAssignmentEntity> active = agentAssignmentRepository
            .findByServiceRequest_ServiceRequestIdAndAssignmentStatusIn(serviceRequestId,
                    List.of("ASSIGNED", "ACCEPTED", "IN_PROGRESS"));
    if (active.isPresent()) {
        AgentEntity assignedAgent = active.get().getAgent();
        if (assignedAgent == null) {
            throw new IllegalStateException("Active assignment does not reference an agent");
        }
        if (assignedAgent.getAgentId().equals(agentId)) {
            throw new BusinessException("This agent is already assigned to the request");
        }
    }
    if (active.isPresent()) {
        AgentAssignmentEntity previous = active.get();
        if ("IN_PROGRESS".equals(previous.getAssignmentStatus())) {
            throw new BusinessException("An in-progress assignment cannot be reassigned");
        }
        previous.setAssignmentStatus("CANCELLED");
        previous.setEndedAt(OffsetDateTime.now());
        previous.setUpdatedBy(securityService.getCurrentUserId());
        agentAssignmentRepository.saveAndFlush(previous);
    }

    String oldStatus = request.getStatus();
    AgentAssignmentEntity assignment = new AgentAssignmentEntity();
    assignment.setServiceRequest(request);
    assignment.setAgent(agent);
    assignment.setAssignmentStatus("ASSIGNED");
    assignment.setAssignedAt(OffsetDateTime.now());
    assignment.setAssignmentNotes(notes == null || notes.isBlank() ? null : notes.trim());
    assignment.setCreatedBy(securityService.getCurrentUserId());
    assignment.setUpdatedBy(securityService.getCurrentUserId());
    agentAssignmentRepository.save(assignment);

    request.setAssignedTo(agent.getAgentId());
    request.setAssignedBy(securityService.getCurrentUserId());
    request.setAssignedAt(Instant.now());
    request.setStatus(ServiceRequestStatus.ASSIGNED.name());
    ServiceRequestEntity saved = serviceRequestRepository.save(request);
    if (!ServiceRequestStatus.ASSIGNED.name().equals(oldStatus)) {
        createStatusHistory(saved, oldStatus, ServiceRequestStatus.ASSIGNED.name(), notes);
    }
    return buildResponse(saved);
}

@Override
public List<ServiceRequestAssignmentHistoryResponse> getAssignmentHistory(UUID serviceRequestId) {
    securityService.validateOperationsAccess();
    if (!serviceRequestRepository.existsById(serviceRequestId)) {
        throw new ResourceNotFoundException("Service request not found");
    }
    List<AgentAssignmentEntity> assignments = agentAssignmentRepository
            .findByServiceRequest_ServiceRequestIdOrderByAssignedAtAsc(serviceRequestId);
    return java.util.stream.IntStream.range(0, assignments.size())
            .mapToObj(index -> {
                AgentAssignmentEntity assignment = assignments.get(index);
                String assignedByName = assignment.getCreatedBy() == null ? null
                        : userRepository.findById(assignment.getCreatedBy()).map(user -> user.getFullName()).orElse(null);
                AgentEntity agent = assignment.getAgent();
                return new ServiceRequestAssignmentHistoryResponse(
                        assignment.getAssignmentId(), index == 0 ? "Assigned" : "Reassigned",
                        agent.getAgentId(), agent.getUser() == null ? null : agent.getUser().getFullName(),
                        agent.getAgentCode(), assignedByName, assignment.getCreatedBy(),
                        assignment.getAssignmentStatus(), assignment.getAssignmentNotes(), assignment.getAssignedAt(), assignment.getRespondedAt());
            })
            .sorted(java.util.Comparator.comparing(ServiceRequestAssignmentHistoryResponse::assignedAt).reversed())
            .toList();
}

@Override
public ServiceRequestResponse acceptRequest(
        UUID serviceRequestId,
        String reason) {

    ServiceRequestEntity request =
            serviceRequestRepository
                    .findByIdForUpdate(serviceRequestId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Service request not found"));
    securityService.validateServiceRequestAccess(request);
    securityService.validateAssignmentAccess(agentAssignmentRepository
            .findByServiceRequest_ServiceRequestIdAndAssignmentStatus(serviceRequestId, "ASSIGNED")
            .orElseThrow(() -> new ResourceNotFoundException("No active assignment found")));
validateTransition(
        request.getStatus(),
        ServiceRequestStatus.ACCEPTED.name());
    AgentAssignmentEntity assignment =
            agentAssignmentRepository
                    .findByServiceRequest_ServiceRequestIdAndAssignmentStatus(
                            serviceRequestId,
                            "ASSIGNED")
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "No active assignment found"));

    assignment.setAssignmentStatus(
            "ACCEPTED");

    assignment.setRespondedAt(
            OffsetDateTime.now());

    agentAssignmentRepository.save(
            assignment);

    String oldStatus =
            request.getStatus();

    request.setStatus(
            ServiceRequestStatus.ACCEPTED.name());

    serviceRequestRepository.save(
            request);

    createStatusHistory(
            request,
            oldStatus,
            ServiceRequestStatus.ACCEPTED.name(),
            reason);

    return buildResponse(
            request);
}

@Override
public ServiceRequestResponse rejectAssignment(UUID serviceRequestId, String reason) {
    ServiceRequestEntity request = serviceRequestRepository.findByIdForUpdate(serviceRequestId)
            .orElseThrow(() -> new ResourceNotFoundException("Service request not found"));
    AgentAssignmentEntity assignment = agentAssignmentRepository
            .findByServiceRequest_ServiceRequestIdAndAssignmentStatus(serviceRequestId, "ASSIGNED")
            .orElseThrow(() -> new ResourceNotFoundException("No active assignment found"));
    securityService.validateAssignmentAccess(assignment);
    if (!ServiceRequestStatus.ASSIGNED.name().equals(request.getStatus())) {
        throw new BusinessException("Only assigned requests can be rejected by the agent");
    }
    assignment.setAssignmentStatus("REJECTED");
    assignment.setRejectionReason(reason);
    assignment.setRespondedAt(OffsetDateTime.now());
    assignment.setEndedAt(OffsetDateTime.now());
    agentAssignmentRepository.save(assignment);
    request.setAssignedTo(null);
    String previousStatus = request.getStatus();
    request.setStatus(ServiceRequestStatus.PENDING_ASSIGNMENT.name());
    ServiceRequestEntity saved = serviceRequestRepository.save(request);
    createStatusHistory(saved, previousStatus, ServiceRequestStatus.PENDING_ASSIGNMENT.name(), reason);
    return buildResponse(saved);
}

@Override
public ServiceRequestResponse startRequest(
        UUID serviceRequestId,
        String reason) {

    ServiceRequestEntity request =
            serviceRequestRepository
                    .findByIdForUpdate(serviceRequestId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Service request not found"));
    securityService.validateServiceRequestAccess(request);
    securityService.validateAssignmentAccess(agentAssignmentRepository
            .findByServiceRequest_ServiceRequestIdAndAssignmentStatus(serviceRequestId, "ACCEPTED")
            .orElseThrow(() -> new ResourceNotFoundException("Accepted assignment not found")));
validateTransition(
        request.getStatus(),
        ServiceRequestStatus.IN_PROGRESS.name());
    AgentAssignmentEntity assignment =
            agentAssignmentRepository
                    .findByServiceRequest_ServiceRequestIdAndAssignmentStatus(
                            serviceRequestId,
                            "ACCEPTED")
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Accepted assignment not found"));

    assignment.setAssignmentStatus(
            "IN_PROGRESS");

    agentAssignmentRepository.save(
            assignment);

    String oldStatus =
            request.getStatus();

    request.setStatus(
            ServiceRequestStatus.IN_PROGRESS.name());

    serviceRequestRepository.save(
            request);

    createStatusHistory(
            request,
            oldStatus,
            ServiceRequestStatus.IN_PROGRESS.name(),
            reason);

    return buildResponse(
            request);
}

private ServiceRequestResponse
changeStatus(
        ServiceRequestEntity entity,
        String targetStatus,
        String reason) {

    if (ServiceRequestStatus.CANCELLED.name().equals(targetStatus)) {
        securityService.validateServiceRequestAccess(entity);
    }

    String currentStatus =
            entity.getStatus();

    validateTransition(
            currentStatus,
            targetStatus);

    entity.setStatus(
            targetStatus);

    if (ServiceRequestStatus.COMPLETED.name().equals(targetStatus)) {
        entity.setCompletedAt(
                java.time.Instant.now());
    }

    createStatusHistory(
            entity,
            currentStatus,
            targetStatus,
            reason);

    return buildResponse(
            serviceRequestRepository.save(entity));
}

    private ServiceRequestResponse buildResponse(
        ServiceRequestEntity entity) {

    ServiceRequestResponse response =
            new ServiceRequestResponse();

    response.setServiceRequestId(
            entity.getServiceRequestId());

    response.setCustomerId(
            entity.getCustomer() != null
                    ? entity.getCustomer().getCustomerId()
                    : null);

    response.setPropertyId(
            entity.getProperty() != null
                    ? entity.getProperty().getPropertyId()
                    : null);

    response.setServiceId(
            entity.getService() != null
                    ? entity.getService().getServiceId()
                    : null);

    response.setQuoteId(entity.getPricingEstimate() == null
            ? null : entity.getPricingEstimate().getPricingEstimateId());
    response.setLeadId(entity.getLead() == null ? null : entity.getLead().getLeadId());

    response.setRequestType(
            entity.getRequestType());

    response.setPriority(
            entity.getPriority());

    response.setStatus(
            entity.getStatus());

    response.setAmount(
            entity.getAmount());

    response.setDescription(
            entity.getDescription());

    response.setRequestedAt(
            entity.getRequestedAt());

    response.setScheduledAt(
            entity.getScheduledAt());

    response.setCompletedAt(
            entity.getCompletedAt());

    response.setAssignedTo(
            entity.getAssignedTo());
    response.setAssignedToName(null);
    response.setAssignedAt(entity.getAssignedAt());
    response.setAssignedByUserId(entity.getAssignedBy());
    if (entity.getAssignedBy() != null) {
        userRepository.findById(entity.getAssignedBy()).ifPresent(user -> response.setAssignedByName(user.getFullName()));
    }
    if (entity.getCustomer() != null && entity.getCustomer().getUser() != null) {
        response.setCustomerName(entity.getCustomer().getUser().getFullName());
    }
    if (entity.getProperty() != null) {
        response.setPropertyTitle(entity.getProperty().getTitle());
    }
    if (entity.getService() != null) {
        response.setServiceName(entity.getService().getServiceName());
    }

    populateSlaResponse(entity, response);

    if (entity.getAssignedTo() != null) {
        agentRepository.findById(entity.getAssignedTo()).ifPresent(agent -> {
            response.setAssignedAgentName(agent.getUser().getFullName());
            response.setAssignedToName(agent.getUser().getFullName());
            response.setAssignedAgentId(agent.getAgentId());
            response.setAssignedAgentCode(agent.getAgentCode());
            response.setAssignedAgentPhone(agent.getUser().getMobileNumber());
        });
    }
    agentAssignmentRepository.findByServiceRequest_ServiceRequestIdOrderByAssignedAtAsc(entity.getServiceRequestId())
            .stream().reduce((first, second) -> second)
            .ifPresent(assignment -> response.setAssignmentNotes(assignment.getAssignmentNotes()));

    return response;
}

private void captureSlaSnapshot(ServiceRequestEntity request) {
    request.setSlaSnapshotCaptured(true);
    if (request.getService() == null || request.getRequestedAt() == null) return;
    slaPolicyRepository
            .findByService_ServiceIdAndStatusIgnoreCaseOrderByCreatedAtDesc(request.getService().getServiceId(), "ACTIVE")
            .stream().findFirst().ifPresent(policy -> {
                request.setSlaPolicyIdSnapshot(policy.getSlaPolicyId());
                request.setSlaDueAt(request.getRequestedAt().plusSeconds(policy.getCompletionMinutes().longValue() * 60));
            });
}

private void populateSlaResponse(ServiceRequestEntity request, ServiceRequestResponse response) {
    java.time.Instant dueAt = request.getSlaDueAt();
    if (!request.isSlaSnapshotCaptured() && request.getService() != null && request.getRequestedAt() != null) {
        var currentPolicy = slaPolicyRepository
                .findByService_ServiceIdAndStatusIgnoreCaseOrderByCreatedAtDesc(request.getService().getServiceId(), "ACTIVE")
                .stream().findFirst().orElse(null);
        if (currentPolicy != null) {
            dueAt = request.getRequestedAt().plusSeconds(currentPolicy.getCompletionMinutes().longValue() * 60);
        }
    }
    response.setSlaDueAt(dueAt);
    if (dueAt == null) {
        response.setSlaStatus("NO_POLICY");
    } else {
        response.setSlaStatus(request.getCompletedAt() != null
                ? (request.getCompletedAt().isAfter(dueAt) ? "BREACHED" : "MET")
                : java.time.Instant.now().isAfter(dueAt) ? "BREACHED" : "ON_TRACK");
    }
}
    private void createStatusHistory(
        ServiceRequestEntity serviceRequest,
        String previousStatus,
        String newStatus,
        String reason) {

    ServiceRequestStatusHistoryEntity history =
            new ServiceRequestStatusHistoryEntity();

    history.setServiceRequest(
            serviceRequest);

    history.setPreviousStatus(
            previousStatus);

    history.setNewStatus(
            newStatus);

    history.setChangeReason(
            reason);

    history.setChangedAt(
            java.time.Instant.now());
    history.setCreatedBy(securityService.getCurrentUserId());
    history.setUpdatedBy(securityService.getCurrentUserId());

    statusHistoryRepository.save(
            history);
}

    private void createSystemStatusHistory(ServiceRequestEntity serviceRequest, String newStatus, String reason) {
        ServiceRequestStatusHistoryEntity history = new ServiceRequestStatusHistoryEntity();
        history.setServiceRequest(serviceRequest);
        history.setNewStatus(newStatus);
        history.setChangeReason(reason);
        history.setChangedAt(Instant.now());
        // Scheduler-created work has no interactive user principal; the reason identifies its origin.
        statusHistoryRepository.save(history);
    }
}
