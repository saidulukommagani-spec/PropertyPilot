package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateVisitRequest;
import com.propertypilot.application.dto.VisitResponse;
import com.propertypilot.application.service.VisitService;
import com.propertypilot.infrastructure.persistence.entity.AgentAssignmentEntity;
import com.propertypilot.infrastructure.persistence.entity.VisitEntity;
import com.propertypilot.infrastructure.persistence.repository.AgentAssignmentRepository;
import com.propertypilot.infrastructure.persistence.repository.VisitRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;
    private final AgentAssignmentRepository assignmentRepository;

    public VisitServiceImpl(
            VisitRepository visitRepository,
            AgentAssignmentRepository assignmentRepository) {

        this.visitRepository = visitRepository;
        this.assignmentRepository = assignmentRepository;
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

        if (visitRepository.existsByAssignment_AssignmentId(
                assignment.getAssignmentId())) {

            throw new IllegalStateException(
                    "Visit already exists for assignment");
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

        visit.setStatus(
                "SCHEDULED");

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

        return buildResponse(visit);
    }

    @Override
    public VisitResponse startVisit(
            UUID visitId) {

        VisitEntity visit =
                visitRepository.findById(visitId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Visit not found"));

        if (!"SCHEDULED".equalsIgnoreCase(
                visit.getStatus())) {

            throw new IllegalStateException(
                    "Only scheduled visits can be started");
        }

        visit.setStatus(
                "IN_PROGRESS");

        visit.setStartedAt(
                OffsetDateTime.now());

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

        if (!"IN_PROGRESS".equalsIgnoreCase(
                visit.getStatus())) {

            throw new IllegalStateException(
                    "Visit must be in progress");
        }

        visit.setStatus(
                "COMPLETED");

        visit.setEndedAt(
                OffsetDateTime.now());

        VisitEntity updatedVisit =
                visitRepository.save(visit);

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

        if ("COMPLETED".equalsIgnoreCase(
                visit.getStatus())) {

            throw new IllegalStateException(
                    "Completed visit cannot be cancelled");
        }

        visit.setStatus(
                "CANCELLED");

        visit.setCancellationReason(
                reason);

        VisitEntity updatedVisit =
                visitRepository.save(visit);

        return buildResponse(updatedVisit);
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
        }

        if (visit.getAgent() != null) {

            response.setAgentId(
                    visit.getAgent()
                            .getAgentId());
        }

        response.setStatus(
                visit.getStatus());

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

        return response;
    }
}