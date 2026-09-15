package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.AgentAssignmentResponse;
import com.propertypilot.application.dto.CreateAgentAssignmentRequest;
import com.propertypilot.application.dto.RejectAgentAssignmentRequest;
import com.propertypilot.application.service.AgentAssignmentService;
import com.propertypilot.infrastructure.persistence.entity.AgentAssignmentEntity;
import com.propertypilot.infrastructure.persistence.entity.AgentEntity;
import com.propertypilot.infrastructure.persistence.repository.AgentAssignmentRepository;
import com.propertypilot.infrastructure.persistence.repository.AgentRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestRepository;
import com.propertypilot.security.SecurityService;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AgentAssignmentServiceImpl
        implements AgentAssignmentService {

    private final AgentAssignmentRepository agentAssignmentRepository;
    private final AgentRepository agentRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final SecurityService securityService;

    public AgentAssignmentServiceImpl(
            AgentAssignmentRepository agentAssignmentRepository,
            AgentRepository agentRepository,
            ServiceRequestRepository serviceRequestRepository,
            SecurityService securityService) {

        this.agentAssignmentRepository =
                agentAssignmentRepository;

        this.agentRepository =
                agentRepository;

        this.serviceRequestRepository =
                serviceRequestRepository;

        this.securityService =
                securityService;
    }

    @Override
    public AgentAssignmentResponse createAssignment(
            CreateAgentAssignmentRequest request) {

        securityService.validateAdminAccess();

        var serviceRequest =
                serviceRequestRepository
                        .findById(request.getServiceRequestId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Service request not found"));

        AgentEntity agent =
                agentRepository
                        .findById(request.getAgentId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Agent not found"));

        agentAssignmentRepository
                .findByServiceRequest_ServiceRequestIdAndAssignmentStatusIn(
                        request.getServiceRequestId(),
                        List.of(
                                "ASSIGNED",
                                "ACCEPTED",
                                "IN_PROGRESS"))
                .ifPresent(existing -> {
                    throw new IllegalStateException(
                            "Active assignment already exists for this request");
                });

        AgentAssignmentEntity assignment =
                new AgentAssignmentEntity();

        assignment.setServiceRequest(serviceRequest);
        assignment.setAgent(agent);
        assignment.setAssignmentStatus("ASSIGNED");
        assignment.setDistanceKm(request.getDistanceKm());
        assignment.setEtaMinutes(request.getEtaMinutes());
        assignment.setAssignedAt(OffsetDateTime.now());

        AgentAssignmentEntity saved =
                agentAssignmentRepository.save(
                        assignment);

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

        return buildResponse(assignment);
    }

    @Override
    public AgentAssignmentResponse acceptAssignment(
            UUID assignmentId) {

        throw new UnsupportedOperationException(
                "Not implemented yet");
    }

    @Override
    public AgentAssignmentResponse rejectAssignment(
            UUID assignmentId,
            RejectAgentAssignmentRequest request) {

        throw new UnsupportedOperationException(
                "Not implemented yet");
    }

    @Override
    public AgentAssignmentResponse startAssignment(
            UUID assignmentId) {

        throw new UnsupportedOperationException(
                "Not implemented yet");
    }

    @Override
    public AgentAssignmentResponse completeAssignment(
            UUID assignmentId) {

        throw new UnsupportedOperationException(
                "Not implemented yet");
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
}