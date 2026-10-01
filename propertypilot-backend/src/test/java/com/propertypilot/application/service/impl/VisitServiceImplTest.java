package com.propertypilot.application.service.impl;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;

import com.propertypilot.application.dto.CreateVisitRequest;
import com.propertypilot.application.dto.VisitResponse;
import com.propertypilot.domain.enums.VisitStatus;
import com.propertypilot.infrastructure.persistence.entity.AgentAssignmentEntity;
import com.propertypilot.infrastructure.persistence.entity.AgentEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;
import com.propertypilot.infrastructure.persistence.entity.VisitEntity;
import com.propertypilot.web.exception.ResourceNotFoundException;




import com.propertypilot.infrastructure.persistence.repository.AgentAssignmentRepository;
import com.propertypilot.infrastructure.persistence.repository.VisitRepository;



import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class VisitServiceImplTest {

     @Mock
    private VisitRepository visitRepository;

    @Mock
    private AgentAssignmentRepository assignmentRepository;

    @InjectMocks
    private VisitServiceImpl service;

    private AgentEntity agent() {

    AgentEntity agent = new AgentEntity();
    agent.setAgentId(UUID.randomUUID());

    return agent;
}

private ServiceRequestEntity serviceRequest(
        String status) {

    ServiceRequestEntity request =
            new ServiceRequestEntity();

    request.setServiceRequestId(
            UUID.randomUUID());

    request.setStatus(status);

    return request;
}

private AgentAssignmentEntity assignment(
        String assignmentStatus,
        String requestStatus) {

    AgentAssignmentEntity assignment =
            new AgentAssignmentEntity();

    assignment.setAssignmentId(
            UUID.randomUUID());

    assignment.setAssignmentStatus(
            assignmentStatus);

    assignment.setAgent(
            agent());

    assignment.setServiceRequest(
            serviceRequest(requestStatus));

    return assignment;
}

private VisitEntity visit(
        String status) {

    VisitEntity visit =
            new VisitEntity();

    visit.setVisitId(
            UUID.randomUUID());

    visit.setStatus(status);

    visit.setAssignment(
            assignment(
                    "IN_PROGRESS",
                    "IN_PROGRESS"));

    visit.setAgent(
            visit.getAssignment().getAgent());

    visit.setServiceRequest(
            visit.getAssignment()
                    .getServiceRequest());

    return visit;
}
@Test
void createVisit_success() {

    AgentAssignmentEntity assignment =
            assignment(
                    "ACCEPTED",
                    "ACCEPTED");

    CreateVisitRequest request =
            new CreateVisitRequest();

    request.setAssignmentId(
            assignment.getAssignmentId());

    request.setScheduledAt(
            OffsetDateTime.now()
                    .plusDays(1));

    request.setNotes(
            "Site Visit");

    when(
            assignmentRepository.findById(
                    assignment.getAssignmentId()))
            .thenReturn(
                    Optional.of(assignment));

    when(
            visitRepository
                    .existsByAssignment_AssignmentId(
                            assignment.getAssignmentId()))
            .thenReturn(false);

    when(
            visitRepository.save(any()))
            .thenAnswer(
                    invocation -> {

                        VisitEntity visit =
                                invocation.getArgument(0);

                        visit.setVisitId(
                                UUID.randomUUID());

                        return visit;
                    });

    VisitResponse response =
            service.createVisit(request);

    assertThat(response)
            .isNotNull();

    assertThat(response.getStatus())
            .isEqualTo(
                    VisitStatus.SCHEDULED.name());

    verify(visitRepository)
            .save(any());
}

@Test
void createVisit_assignmentNotFound() {

    UUID assignmentId =
            UUID.randomUUID();

    CreateVisitRequest request =
            new CreateVisitRequest();

    request.setAssignmentId(
            assignmentId);

    when(
            assignmentRepository.findById(
                    assignmentId))
            .thenReturn(
                    Optional.empty());

    assertThatThrownBy(() ->
            service.createVisit(request))
            .isInstanceOf(
                    ResourceNotFoundException.class)
            .hasMessage(
                    "Assignment not found");
}

@Test
void createVisit_invalidAssignmentStatus() {

    AgentAssignmentEntity assignment =
            assignment(
                    "PENDING",
                    "ACCEPTED");

    CreateVisitRequest request =
            new CreateVisitRequest();

    request.setAssignmentId(
            assignment.getAssignmentId());

    when(
            assignmentRepository.findById(
                    assignment.getAssignmentId()))
            .thenReturn(
                    Optional.of(assignment));

    assertThatThrownBy(() ->
            service.createVisit(request))
            .isInstanceOf(
                    IllegalStateException.class)
            .hasMessageContaining(
                    "Visit can only be created");
}

@Test
void createVisit_invalidRequestStatus() {

    AgentAssignmentEntity assignment =
            assignment(
                    "ACCEPTED",
                    "NEW");

    CreateVisitRequest request =
            new CreateVisitRequest();

    request.setAssignmentId(
            assignment.getAssignmentId());

    when(
            assignmentRepository.findById(
                    assignment.getAssignmentId()))
            .thenReturn(
                    Optional.of(assignment));

    assertThatThrownBy(() ->
            service.createVisit(request))
            .isInstanceOf(
                    IllegalStateException.class)
            .hasMessageContaining(
                    "Visit cannot be created");
}
@Test
void createVisit_alreadyExists() {

    AgentAssignmentEntity assignment =
            assignment("ACCEPTED", "ACCEPTED");

    CreateVisitRequest request =
            new CreateVisitRequest();

    request.setAssignmentId(
            assignment.getAssignmentId());

    when(assignmentRepository.findById(
            assignment.getAssignmentId()))
            .thenReturn(Optional.of(assignment));

    when(visitRepository
            .existsByAssignment_AssignmentId(
                    assignment.getAssignmentId()))
            .thenReturn(true);

    assertThatThrownBy(() ->
            service.createVisit(request))
            .isInstanceOf(
                    IllegalStateException.class)
            .hasMessageContaining(
                    "Visit already exists");
}

@Test
void createVisit_scheduledDateNull() {

    AgentAssignmentEntity assignment =
            assignment("ACCEPTED", "ACCEPTED");

    CreateVisitRequest request =
            new CreateVisitRequest();

    request.setAssignmentId(
            assignment.getAssignmentId());

    when(assignmentRepository.findById(
            assignment.getAssignmentId()))
            .thenReturn(Optional.of(assignment));

    when(visitRepository
            .existsByAssignment_AssignmentId(
                    assignment.getAssignmentId()))
            .thenReturn(false);

    assertThatThrownBy(() ->
            service.createVisit(request))
            .isInstanceOf(
                    IllegalArgumentException.class)
            .hasMessageContaining(
                    "Scheduled date is required");
}

@Test
void createVisit_scheduledDatePast() {

    AgentAssignmentEntity assignment =
            assignment("ACCEPTED", "ACCEPTED");

    CreateVisitRequest request =
            new CreateVisitRequest();

    request.setAssignmentId(
            assignment.getAssignmentId());

    request.setScheduledAt(
            OffsetDateTime.now().minusDays(1));

    when(assignmentRepository.findById(
            assignment.getAssignmentId()))
            .thenReturn(Optional.of(assignment));

    when(visitRepository
            .existsByAssignment_AssignmentId(
                    assignment.getAssignmentId()))
            .thenReturn(false);

    assertThatThrownBy(() ->
            service.createVisit(request))
            .isInstanceOf(
                    IllegalArgumentException.class)
            .hasMessageContaining(
                    "cannot be in the past");
}

@Test
void getVisit_success() {

    VisitEntity visit =
            visit(
                    VisitStatus.SCHEDULED.name());

    when(visitRepository.findById(
            visit.getVisitId()))
            .thenReturn(Optional.of(visit));

    VisitResponse response =
            service.getVisit(
                    visit.getVisitId());

    assertThat(response)
            .isNotNull();

    assertThat(response.getVisitId())
            .isEqualTo(
                    visit.getVisitId());
}

@Test
void getVisit_notFound() {

    UUID id =
            UUID.randomUUID();

    when(visitRepository.findById(id))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.getVisit(id))
            .isInstanceOf(
                    ResourceNotFoundException.class)
            .hasMessage(
                    "Visit not found");
}

@Test
void startVisit_success() {

    VisitEntity visit =
            visit(
                    VisitStatus.SCHEDULED.name());

    visit.getAssignment()
            .setAssignmentStatus(
                    "ACCEPTED");

    when(visitRepository.findById(
            visit.getVisitId()))
            .thenReturn(Optional.of(visit));

    when(visitRepository.save(any()))
            .thenReturn(visit);

    VisitResponse response =
            service.startVisit(
                    visit.getVisitId());

    assertThat(response.getStatus())
            .isEqualTo(
                    VisitStatus.IN_PROGRESS.name());

    verify(assignmentRepository)
            .save(any());

    verify(visitRepository)
            .save(any());
}

@Test
void startVisit_notFound() {

    UUID id =
            UUID.randomUUID();

    when(visitRepository.findById(id))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.startVisit(id))
            .isInstanceOf(
                    ResourceNotFoundException.class)
            .hasMessage(
                    "Visit not found");
}

@Test
void completeVisit_success() {

    VisitEntity visit =
            visit(
                    VisitStatus.IN_PROGRESS.name());

    visit.getAssignment()
            .setAssignmentStatus(
                    "IN_PROGRESS");

    when(visitRepository.findById(
            visit.getVisitId()))
            .thenReturn(Optional.of(visit));

    when(visitRepository.save(any()))
            .thenReturn(visit);

    VisitResponse response =
            service.completeVisit(
                    visit.getVisitId());

    assertThat(response.getStatus())
            .isEqualTo(
                    VisitStatus.COMPLETED.name());

    verify(assignmentRepository)
            .save(any());

    verify(visitRepository)
            .save(any());
}

@Test
void completeVisit_invalidStatus() {

    VisitEntity visit =
            visit(
                    VisitStatus.SCHEDULED.name());

    when(visitRepository.findById(
            visit.getVisitId()))
            .thenReturn(Optional.of(visit));

    assertThatThrownBy(() ->
            service.completeVisit(
                    visit.getVisitId()))
            .isInstanceOf(
                    IllegalStateException.class)
            .hasMessageContaining(
                    "Visit must be in progress");
}

@Test
void cancelVisit_success() {

    VisitEntity visit =
            visit(
                    VisitStatus.SCHEDULED.name());

    when(visitRepository.findById(
            visit.getVisitId()))
            .thenReturn(Optional.of(visit));

    when(visitRepository.save(any()))
            .thenReturn(visit);

    VisitResponse response =
            service.cancelVisit(
                    visit.getVisitId(),
                    "Customer cancelled");

    assertThat(response.getStatus())
            .isEqualTo(
                    VisitStatus.CANCELLED.name());

    verify(visitRepository)
            .save(any());
}

@Test
void cancelVisit_notFound() {

    UUID id =
            UUID.randomUUID();

    when(visitRepository.findById(id))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.cancelVisit(
                    id,
                    "Reason"))
            .isInstanceOf(
                    ResourceNotFoundException.class)
            .hasMessage(
                    "Visit not found");
}



    
}
