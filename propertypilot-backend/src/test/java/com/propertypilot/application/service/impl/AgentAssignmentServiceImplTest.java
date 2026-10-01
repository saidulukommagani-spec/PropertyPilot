package com.propertypilot.application.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.propertypilot.application.dto.AgentAssignmentResponse;
import com.propertypilot.application.dto.CreateAgentAssignmentRequest;
import com.propertypilot.application.dto.RejectAgentAssignmentRequest;

import com.propertypilot.infrastructure.persistence.entity.AgentAssignmentEntity;
import com.propertypilot.infrastructure.persistence.entity.AgentEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;
import com.propertypilot.infrastructure.persistence.entity.UserEntity;

import com.propertypilot.infrastructure.persistence.repository.AgentAssignmentRepository;
import com.propertypilot.infrastructure.persistence.repository.AgentRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestRepository;

import com.propertypilot.security.SecurityService;

import com.propertypilot.web.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class AgentAssignmentServiceImplTest {

    @Mock
private AgentAssignmentRepository agentAssignmentRepository;

@Mock
private AgentRepository agentRepository;

@Mock
private ServiceRequestRepository serviceRequestRepository;

@Mock
private SecurityService securityService;

@InjectMocks
private AgentAssignmentServiceImpl service;

    private UserEntity user() {

    UserEntity user = new UserEntity();
    user.setFullName("Test Agent");

    return user;
}

private AgentEntity agent() {

    AgentEntity agent = new AgentEntity();

    agent.setAgentId(UUID.randomUUID());

    agent.setUser(user());

    return agent;
}

private ServiceRequestEntity serviceRequest() {

    ServiceRequestEntity request =
            new ServiceRequestEntity();

    request.setServiceRequestId(
            UUID.randomUUID());

    return request;
}

private AgentAssignmentEntity assignment() {

    AgentAssignmentEntity assignment =
            new AgentAssignmentEntity();

    assignment.setAssignmentId(
            UUID.randomUUID());

    assignment.setServiceRequest(
            serviceRequest());

    assignment.setAgent(
            agent());

    assignment.setAssignmentStatus(
            "ASSIGNED");

    assignment.setDistanceKm(
        BigDecimal.valueOf(10.0));

    assignment.setEtaMinutes(30);

    assignment.setAssignedAt(
            OffsetDateTime.now());

    return assignment;
}

@Test
void createAssignment_success() {

    ServiceRequestEntity requestEntity =
            serviceRequest();

    AgentEntity agent =
            agent();

    CreateAgentAssignmentRequest request =
            new CreateAgentAssignmentRequest();

    request.setServiceRequestId(
            requestEntity.getServiceRequestId());

    request.setAgentId(
            agent.getAgentId());

    request.setDistanceKm(
        BigDecimal.valueOf(12.5));

    request.setEtaMinutes(20);

    when(
            serviceRequestRepository.findById(
                    requestEntity.getServiceRequestId()))
            .thenReturn(
                    Optional.of(requestEntity));

    when(
            agentRepository.findById(
                    agent.getAgentId()))
            .thenReturn(
                    Optional.of(agent));

    when(
            agentAssignmentRepository
                    .findByServiceRequest_ServiceRequestIdAndAssignmentStatusIn(
                            any(),
                            any()))
            .thenReturn(
                    Optional.empty());

    when(
            agentAssignmentRepository.save(any()))
            .thenAnswer(invocation -> {

                AgentAssignmentEntity saved =
                        invocation.getArgument(0);

                saved.setAssignmentId(
                        UUID.randomUUID());

                return saved;
            });

    AgentAssignmentResponse response =
            service.createAssignment(
                    request);

    assertThat(response)
            .isNotNull();

    assertThat(response.getAssignmentStatus())
            .isEqualTo("ASSIGNED");

    verify(securityService)
            .validateAdminAccess();
}
@Test
void createAssignment_serviceRequestNotFound() {

    CreateAgentAssignmentRequest request =
            new CreateAgentAssignmentRequest();

    UUID requestId =
            UUID.randomUUID();

    request.setServiceRequestId(
            requestId);

    when(
            serviceRequestRepository.findById(
                    requestId))
            .thenReturn(
                    Optional.empty());

    assertThatThrownBy(() ->
            service.createAssignment(
                    request))
            .isInstanceOf(
                    ResourceNotFoundException.class)
            .hasMessage(
                    "Service request not found");
}

@Test
void createAssignment_agentNotFound() {

    ServiceRequestEntity requestEntity =
            serviceRequest();

    UUID agentId =
            UUID.randomUUID();

    CreateAgentAssignmentRequest request =
            new CreateAgentAssignmentRequest();

    request.setServiceRequestId(
            requestEntity.getServiceRequestId());

    request.setAgentId(
            agentId);

    when(
            serviceRequestRepository.findById(
                    requestEntity.getServiceRequestId()))
            .thenReturn(
                    Optional.of(requestEntity));

    when(
            agentRepository.findById(
                    agentId))
            .thenReturn(
                    Optional.empty());

    assertThatThrownBy(() ->
            service.createAssignment(
                    request))
            .isInstanceOf(
                    ResourceNotFoundException.class)
            .hasMessage(
                    "Agent not found");
}

@Test
void createAssignment_activeAssignmentExists() {

    ServiceRequestEntity requestEntity =
            serviceRequest();

    AgentEntity agent =
            agent();

    CreateAgentAssignmentRequest request =
            new CreateAgentAssignmentRequest();

    request.setServiceRequestId(
            requestEntity.getServiceRequestId());

    request.setAgentId(
            agent.getAgentId());

    when(
            serviceRequestRepository.findById(
                    requestEntity.getServiceRequestId()))
            .thenReturn(
                    Optional.of(requestEntity));

    when(
            agentRepository.findById(
                    agent.getAgentId()))
            .thenReturn(
                    Optional.of(agent));

    when(
            agentAssignmentRepository
                    .findByServiceRequest_ServiceRequestIdAndAssignmentStatusIn(
                            any(),
                            any()))
            .thenReturn(
                    Optional.of(
                            assignment()));

    assertThatThrownBy(() ->
            service.createAssignment(
                    request))
            .isInstanceOf(
                    IllegalStateException.class)
            .hasMessageContaining(
                    "Active assignment already exists");
}

@Test
void getAssignment_success() {

    AgentAssignmentEntity assignment =
            assignment();

    when(
            agentAssignmentRepository.findById(
                    assignment.getAssignmentId()))
            .thenReturn(
                    Optional.of(
                            assignment));

    AgentAssignmentResponse response =
            service.getAssignment(
                    assignment.getAssignmentId());

    assertThat(response)
            .isNotNull();

    assertThat(response.getAssignmentId())
            .isEqualTo(
                    assignment.getAssignmentId());

    assertThat(response.getAgentName())
            .isEqualTo(
                    "Test Agent");
}

@Test
void getAssignment_notFound() {

    UUID id =
            UUID.randomUUID();

    when(
            agentAssignmentRepository.findById(
                    id))
            .thenReturn(
                    Optional.empty());

    assertThatThrownBy(() ->
            service.getAssignment(id))
            .isInstanceOf(
                    ResourceNotFoundException.class)
            .hasMessage(
                    "Assignment not found");
}

@Test
void acceptAssignment_throwsException() {

    assertThatThrownBy(() ->
            service.acceptAssignment(
                    UUID.randomUUID()))
            .isInstanceOf(
                    IllegalStateException.class)
            .hasMessageContaining(
                    "Assignment lifecycle");
}

@Test
void rejectAssignment_throwsException() {

    assertThatThrownBy(() ->
            service.rejectAssignment(
                    UUID.randomUUID(),
                    new RejectAgentAssignmentRequest()))
            .isInstanceOf(
                    IllegalStateException.class);
}

@Test
void startAssignment_throwsException() {

    assertThatThrownBy(() ->
            service.startAssignment(
                    UUID.randomUUID()))
            .isInstanceOf(
                    IllegalStateException.class);
}

@Test
void completeAssignment_throwsException() {

    assertThatThrownBy(() ->
            service.completeAssignment(
                    UUID.randomUUID()))
            .isInstanceOf(
                    IllegalStateException.class);
}



    
    
}
