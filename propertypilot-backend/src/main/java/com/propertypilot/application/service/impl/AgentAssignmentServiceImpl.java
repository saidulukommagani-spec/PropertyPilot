package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.AgentAssignmentResponse;
import com.propertypilot.application.dto.CreateAgentAssignmentRequest;
import com.propertypilot.application.dto.RejectAgentAssignmentRequest;
import com.propertypilot.application.service.AgentAssignmentService;
import com.propertypilot.application.service.ServiceRequestService;
import com.propertypilot.infrastructure.persistence.entity.AgentAssignmentEntity;
import com.propertypilot.infrastructure.persistence.repository.AgentAssignmentRepository;
import com.propertypilot.security.SecurityService;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AgentAssignmentServiceImpl
        implements AgentAssignmentService {

    private final AgentAssignmentRepository agentAssignmentRepository;
    private final SecurityService securityService;
    private final ServiceRequestService serviceRequestService;

    public AgentAssignmentServiceImpl(
            AgentAssignmentRepository agentAssignmentRepository,
            SecurityService securityService,
            ServiceRequestService serviceRequestService) {

        this.agentAssignmentRepository =
                agentAssignmentRepository;

        this.securityService =
                securityService;
        this.serviceRequestService = serviceRequestService;
    }

    @Override
    public AgentAssignmentResponse createAssignment(
            CreateAgentAssignmentRequest request) {

        securityService.validateAdminAccess();
        serviceRequestService.assignAgent(request.getServiceRequestId(), request.getAgentId());
        AgentAssignmentEntity saved = agentAssignmentRepository
                .findByServiceRequest_ServiceRequestIdAndAssignmentStatus(
                        request.getServiceRequestId(), "ASSIGNED")
                .orElseThrow(() -> new IllegalStateException("Assignment was not created"));
        saved.setDistanceKm(request.getDistanceKm());
        saved.setEtaMinutes(request.getEtaMinutes());
        saved = agentAssignmentRepository.save(saved);

        return buildResponse(saved);
    }

    @Override
    public AgentAssignmentResponse getAssignment(
            UUID assignmentId) {

        AgentAssignmentEntity assignment =
                agentAssignmentRepository
                        .findById(assignmentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Assignment not found"));
        securityService.validateAssignmentAccess(assignment);

        return buildResponse(assignment);
    }

    @Override
    public AgentAssignmentResponse acceptAssignment(
            UUID assignmentId) {

        AgentAssignmentEntity assignment = loadAndAuthorize(assignmentId);
        serviceRequestService.acceptRequest(
                assignment.getServiceRequest().getServiceRequestId(), "Agent accepted assignment");
        return getAssignment(assignmentId);
    }

    @Override
    public AgentAssignmentResponse rejectAssignment(
            UUID assignmentId,
            RejectAgentAssignmentRequest request) {

        AgentAssignmentEntity assignment = loadAndAuthorize(assignmentId);
        serviceRequestService.rejectAssignment(
                assignment.getServiceRequest().getServiceRequestId(), request.getRejectionReason());
        return getAssignment(assignmentId);
    }

    @Override
    public AgentAssignmentResponse startAssignment(
            UUID assignmentId) {

        AgentAssignmentEntity assignment = loadAndAuthorize(assignmentId);
        serviceRequestService.startRequest(
                assignment.getServiceRequest().getServiceRequestId(), "Agent started work");
        return getAssignment(assignmentId);
    }

    @Override
    public AgentAssignmentResponse completeAssignment(
            UUID assignmentId) {

        AgentAssignmentEntity assignment = loadAndAuthorize(assignmentId);
        serviceRequestService.completeRequest(
                assignment.getServiceRequest().getServiceRequestId(), "Agent completed work");
        return getAssignment(assignmentId);
    }

    private AgentAssignmentResponse buildResponse(
            AgentAssignmentEntity assignment) {

        AgentAssignmentResponse response =
                new AgentAssignmentResponse();

        response.setAssignmentId(
                assignment.getAssignmentId());

        response.setServiceRequestId(
                assignment.getServiceRequest()
                        .getServiceRequestId());

        response.setAgentId(
                assignment.getAgent()
                        .getAgentId());

        response.setAgentName(
                assignment.getAgent()
                        .getUser()
                        .getFullName());

        response.setAssignmentStatus(
                assignment.getAssignmentStatus());

        response.setDistanceKm(
                assignment.getDistanceKm());

        response.setEtaMinutes(
                assignment.getEtaMinutes());

        response.setAssignedAt(
                assignment.getAssignedAt());

        response.setRespondedAt(
                assignment.getRespondedAt());

        response.setEndedAt(
                assignment.getEndedAt());

        response.setRejectionReason(
                assignment.getRejectionReason());

        return response;
    }

    private AgentAssignmentEntity loadAndAuthorize(UUID assignmentId) {
        AgentAssignmentEntity assignment = agentAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        securityService.validateAssignmentAccess(assignment);
        return assignment;
    }
/*
Lifecycle managed by ServiceRequestService
and VisitService.

Methods intentionally deferred
to avoid duplicate workflow ownership.
*/

}
