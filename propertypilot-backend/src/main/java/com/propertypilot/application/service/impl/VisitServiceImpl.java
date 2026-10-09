package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateVisitRequest;
import com.propertypilot.application.dto.VisitResponse;
import com.propertypilot.application.dto.RescheduleVisitRequest;
import com.propertypilot.application.service.VisitService;
import com.propertypilot.application.service.ServiceRequestService;
import com.propertypilot.domain.enums.VisitStatus;
import com.propertypilot.infrastructure.persistence.entity.AgentAssignmentEntity;
import com.propertypilot.infrastructure.persistence.entity.VisitEntity;
import com.propertypilot.infrastructure.persistence.repository.AgentAssignmentRepository;
import com.propertypilot.infrastructure.persistence.repository.VisitRepository;
import com.propertypilot.infrastructure.persistence.repository.UserRepository;
import com.propertypilot.infrastructure.persistence.repository.AgentRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import com.propertypilot.security.SecurityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.List;

@Service
@Transactional
public class VisitServiceImpl implements VisitService {

    private final SecurityService securityService;

    private final VisitRepository visitRepository;
    private final AgentAssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final AgentRepository agentRepository;
    private final ServiceRequestService serviceRequestService;

    public VisitServiceImpl(
            VisitRepository visitRepository,
            AgentAssignmentRepository assignmentRepository,
            SecurityService securityService,
            UserRepository userRepository,
            AgentRepository agentRepository,
            ServiceRequestService serviceRequestService) {

        this.visitRepository = visitRepository;
        this.assignmentRepository = assignmentRepository;
        this.securityService = securityService;
        this.userRepository = userRepository;
        this.agentRepository = agentRepository;
        this.serviceRequestService = serviceRequestService;
    }

    @Override
    public VisitResponse createVisit(
            CreateVisitRequest request) {

        AgentAssignmentEntity assignment =
                assignmentRepository.findById(
                                request.getAssignmentId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Assignment not found"));
        securityService.validateAssignmentAccess(assignment);

        var service = assignment.getServiceRequest().getService();
        if (service != null && Boolean.TRUE.equals(service.getRemote())) {
            throw new IllegalStateException("Remote services cannot have visits");
        }
        if (service != null && service.getMaxVisitsAllowed() != null
                && service.getMaxVisitsAllowed() > 0
                && visitRepository.countByServiceRequest_ServiceRequestIdAndStatusNot(
                        assignment.getServiceRequest().getServiceRequestId(), "CANCELLED")
                        >= service.getMaxVisitsAllowed()) {
            throw new IllegalStateException("Maximum visits configured for this service has been reached");
        }

        if (!"ACCEPTED".equals(
        assignment.getAssignmentStatus())
        &&
        !"IN_PROGRESS".equals(
                assignment.getAssignmentStatus())) {

    throw new IllegalStateException(
            "Visit can only be created for accepted assignments");
}
String requestStatus =
        assignment.getServiceRequest()
                .getStatus();

if (!"ACCEPTED".equals(requestStatus)
        &&
        !"IN_PROGRESS".equals(requestStatus)) {

    throw new IllegalStateException(
            "Visit cannot be created for request status "
                    + requestStatus);
}
        if (request.getScheduledAt() == null) {

    throw new IllegalArgumentException(
            "Scheduled date is required");
}

if (request.getScheduledAt()
        .isBefore(
                OffsetDateTime.now())) {

    throw new IllegalArgumentException(
            "Scheduled date cannot be in the past");
}

        VisitEntity visit = new VisitEntity();

        visit.setAssignment(assignment);

        visit.setAgent(
                assignment.getAgent());

        visit.setServiceRequest(
                assignment.getServiceRequest());

        visit.setScheduledAt(
                request.getScheduledAt());

        visit.setNotes(
                request.getNotes());
        visit.setCreatedBy(securityService.getCurrentUserId());

        visit.setStatus(
        VisitStatus.SCHEDULED.name());

        VisitEntity savedVisit =
                visitRepository.save(visit);

        return buildResponse(savedVisit);
    }

    @Override
    public VisitResponse getVisit(
            UUID visitId) {

        VisitEntity visit =
                visitRepository.findById(visitId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Visit not found"));
        securityService.validateVisitAccess(visit);

        return buildResponse(visit);
    }

    @Override
    public List<VisitResponse> getVisitsForServiceRequest(UUID serviceRequestId) {
        // Reuse the core request access policy so customers and assigned agents
        // can see their own visit history, while other users remain denied.
        serviceRequestService.getRequest(serviceRequestId);
        return visitRepository.findByServiceRequest_ServiceRequestId(serviceRequestId).stream()
                .sorted(java.util.Comparator.comparing(VisitEntity::getScheduledAt))
                .map(this::buildResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitResponse> getAllVisitsForOperations() {
        securityService.validateOperationsAccess();
        return visitRepository.findAll().stream()
                .sorted(java.util.Comparator.comparing(VisitEntity::getScheduledAt).reversed())
                .map(this::buildResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitResponse> getVisitsForCurrentAgent() {
        var agent = agentRepository.findByUser_UserIdAndStatus(securityService.getCurrentUserId(), "ACTIVE")
                .orElseThrow(() -> new ResourceNotFoundException("Active agent profile not found"));
        return visitRepository.findByAgent_AgentId(agent.getAgentId()).stream()
                .sorted(java.util.Comparator.comparing(VisitEntity::getScheduledAt).reversed())
                .map(this::buildResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitResponse> getVisitsForCurrentCustomer() {
        UUID customerId = securityService.getCurrentCustomer().getCustomerId();
        return visitRepository.findByServiceRequest_Customer_CustomerIdOrderByScheduledAtDesc(customerId)
                .stream().map(this::buildResponse).toList();
    }

    @Override
    public VisitResponse startVisit(
            UUID visitId) {

        VisitEntity visit =
                visitRepository.findById(visitId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Visit not found"));
        securityService.validateVisitExecutionAccess(visit);

       validateVisitTransition(
        visit.getStatus(),
        VisitStatus.IN_PROGRESS.name());

                visit.setStatus(
        VisitStatus.IN_PROGRESS.name());

        visit.setStartedAt(
                OffsetDateTime.now());
        visit.setStartedByUserId(securityService.getCurrentUserId());
        visit.setUpdatedBy(securityService.getCurrentUserId());
AgentAssignmentEntity assignment =
        visit.getAssignment();

if (assignment == null) {
    throw new IllegalStateException(
            "Visit is not linked to an assignment");
}

if (!"ACCEPTED".equals(
        assignment.getAssignmentStatus())
        &&
    !"IN_PROGRESS".equals(
            assignment.getAssignmentStatus())) {

    throw new IllegalStateException(
            "Assignment is not active");
}

if ("ACCEPTED".equals(visit.getServiceRequest().getStatus())) {
    serviceRequestService.startRequest(visit.getServiceRequest().getServiceRequestId(), "Visit started by assigned agent");
} else if (!"IN_PROGRESS".equals(visit.getServiceRequest().getStatus())) {
    throw new IllegalStateException("Service request must be accepted before starting a visit");
}

assignment.setAssignmentStatus(
        "IN_PROGRESS");

assignmentRepository.save(
        assignment);
               VisitEntity updatedVisit =
                visitRepository.save(visit);

        return buildResponse(updatedVisit);
    }

    @Override
    public VisitResponse completeVisit(
            UUID visitId) {

        VisitEntity visit =
                visitRepository.findById(visitId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Visit not found"));
        securityService.validateVisitExecutionAccess(visit);

        if (!VisitStatus.IN_PROGRESS.name().equalsIgnoreCase(
                visit.getStatus())) {

            throw new IllegalStateException(
                    "Visit must be in progress");
        }

validateVisitTransition(
        visit.getStatus(),
        VisitStatus.COMPLETED.name());

visit.setStatus(
        VisitStatus.COMPLETED.name());

visit.setEndedAt(
        OffsetDateTime.now());
visit.setCompletedByUserId(securityService.getCurrentUserId());
visit.setUpdatedBy(securityService.getCurrentUserId());
AgentAssignmentEntity assignment =
        visit.getAssignment();

if (assignment == null) {
    throw new IllegalStateException(
            "Visit is not linked to an assignment");
}

if (!"IN_PROGRESS".equals(
        assignment.getAssignmentStatus())) {

    throw new IllegalStateException(
            "Assignment must be IN_PROGRESS");
}
        VisitEntity updatedVisit =
                visitRepository.save(visit);

        serviceRequestService.completeRequestIfReady(visit.getServiceRequest().getServiceRequestId(), "Visit completed");

        return buildResponse(updatedVisit);
    }

    @Override
    public VisitResponse cancelVisit(
            UUID visitId,
            String reason) {

        VisitEntity visit =
                visitRepository.findById(visitId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Visit not found"));
        securityService.validateVisitExecutionAccess(visit);

      validateVisitTransition(
        visit.getStatus(),
        VisitStatus.CANCELLED.name());

       
                visit.setStatus(
        VisitStatus.CANCELLED.name());

        visit.setCancellationReason(
                reason);
        visit.setUpdatedBy(securityService.getCurrentUserId());
                visit.setEndedAt(
        OffsetDateTime.now());

        VisitEntity updatedVisit =
                visitRepository.save(visit);

        return buildResponse(updatedVisit);
    }

    @Override
    public VisitResponse rescheduleVisit(UUID visitId, RescheduleVisitRequest request) {
        VisitEntity visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found"));
        securityService.validateVisitExecutionAccess(visit);
        if (!VisitStatus.SCHEDULED.name().equals(visit.getStatus())) {
            throw new IllegalStateException("Only scheduled visits can be rescheduled");
        }
        if (request.getScheduledAt() == null || !request.getScheduledAt().isAfter(OffsetDateTime.now())) {
            throw new IllegalArgumentException("Scheduled date must be in the future");
        }
        visit.setScheduledAt(request.getScheduledAt());
        visit.setNotes(request.getNotes());
        visit.setRescheduleReason(request.getReason().trim());
        visit.setUpdatedBy(securityService.getCurrentUserId());
        return buildResponse(visitRepository.save(visit));
    }

    private VisitResponse buildResponse(
            VisitEntity visit) {

        VisitResponse response =
                new VisitResponse();

        response.setVisitId(
                visit.getVisitId());

        response.setServiceRequestId(
                visit.getServiceRequest()
                        .getServiceRequestId());

        if (visit.getAssignment() != null) {

            response.setAssignmentId(
                    visit.getAssignment()
                            .getAssignmentId());
            response.setAssignedAt(visit.getAssignment().getAssignedAt());
            response.setAcceptedAt(visit.getAssignment().getRespondedAt());
            if (visit.getAssignment().getCreatedBy() != null) userRepository.findById(visit.getAssignment().getCreatedBy()).ifPresent(user -> response.setAssignedByName(user.getFullName()));
        }

        if (visit.getAgent() != null) {

            response.setAgentId(
                    visit.getAgent()
                            .getAgentId());
            if (visit.getAgent().getUser() != null) {
                response.setAgentName(visit.getAgent().getUser().getFullName());
            }
        }

        response.setStatus(
                visit.getStatus());
        if (visit.getServiceRequest() != null) {
            if (visit.getServiceRequest().getProperty() != null) response.setPropertyName(visit.getServiceRequest().getProperty().getTitle());
            if (visit.getServiceRequest().getCustomer() != null && visit.getServiceRequest().getCustomer().getUser() != null) response.setCustomerName(visit.getServiceRequest().getCustomer().getUser().getFullName());
            if (visit.getServiceRequest().getService() != null) response.setServiceName(visit.getServiceRequest().getService().getServiceName());
        }

        response.setScheduledAt(
                visit.getScheduledAt());

        response.setStartedAt(
                visit.getStartedAt());

        response.setEndedAt(
                visit.getEndedAt());

        response.setNotes(
                visit.getNotes());

        response.setCancellationReason(
                visit.getCancellationReason());

        response.setCreatedAt(visit.getCreatedAt());
        response.setUpdatedAt(visit.getUpdatedAt());
        response.setRescheduleReason(visit.getRescheduleReason());
        if (visit.getCreatedBy() != null) {
            userRepository.findById(visit.getCreatedBy()).ifPresent(user -> response.setCreatedByName(user.getFullName()));
        }
        if (visit.getStartedByUserId() != null) {
            userRepository.findById(visit.getStartedByUserId()).ifPresent(user -> response.setStartedByName(user.getFullName()));
        }
        if (visit.getCompletedByUserId() != null) {
            userRepository.findById(visit.getCompletedByUserId()).ifPresent(user -> response.setCompletedByName(user.getFullName()));
        }
        if (visit.getUpdatedBy() != null) {
            userRepository.findById(visit.getUpdatedBy()).ifPresent(user -> response.setUpdatedByName(user.getFullName()));
        }

        return response;
    }

    private void validateVisitTransition(
        String currentStatus,
        String targetStatus) {

    if (VisitStatus.COMPLETED.name()
            .equals(currentStatus)
            ||
        VisitStatus.CANCELLED.name()
                .equals(currentStatus)
            ||
        VisitStatus.FAILED.name()
                .equals(currentStatus)) {

        throw new IllegalStateException(
                "Terminal state reached: "
                        + currentStatus);
    }

    boolean valid =

            (VisitStatus.SCHEDULED.name()
                    .equals(currentStatus)
                    &&
                    (
                            VisitStatus.IN_PROGRESS.name()
                                    .equals(targetStatus)
                                    ||
                            VisitStatus.CANCELLED.name()
                                    .equals(targetStatus)
                    ))

            ||

            (VisitStatus.IN_PROGRESS.name()
                    .equals(currentStatus)
                    &&
                    (
                            VisitStatus.COMPLETED.name()
                                    .equals(targetStatus)
                                    ||
                            VisitStatus.FAILED.name()
                                    .equals(targetStatus)
                    ));

    if (!valid) {

        throw new IllegalStateException(
                "Invalid visit transition from "
                        + currentStatus
                        + " to "
                        + targetStatus);
    }
}
}
