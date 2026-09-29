package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateVisitRequest;
import com.propertypilot.application.dto.VisitResponse;
import com.propertypilot.application.service.VisitService;
import com.propertypilot.domain.enums.VisitStatus;
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
        if (visitRepository.existsByAssignment_AssignmentId(
                assignment.getAssignmentId())) {

            throw new IllegalStateException(
                    "Visit already exists for assignment");
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

       validateVisitTransition(
        visit.getStatus(),
        VisitStatus.IN_PROGRESS.name());

                visit.setStatus(
        VisitStatus.IN_PROGRESS.name());

        visit.setStartedAt(
                OffsetDateTime.now());
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
assignment.setAssignmentStatus(
        "COMPLETED");

assignment.setEndedAt(
        OffsetDateTime.now());

assignmentRepository.save(
        assignment);
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

      validateVisitTransition(
        visit.getStatus(),
        VisitStatus.CANCELLED.name());

       
                visit.setStatus(
        VisitStatus.CANCELLED.name());

        visit.setCancellationReason(
                reason);
                visit.setEndedAt(
        OffsetDateTime.now());

                AgentAssignmentEntity assignment =
        visit.getAssignment();

if (assignment != null) {

    assignment.setAssignmentStatus(
            "CANCELLED");

    assignment.setEndedAt(
            OffsetDateTime.now());

    assignmentRepository.save(
            assignment);
}

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