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
import com.propertypilot.application.service.ServiceRequestService;

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

@Mock
private ServiceRequestService serviceRequestService;

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
    AgentAssignmentEntity assigned = assignment();
    assigned.setServiceRequest(requestEntity);
    assigned.setAgent(agent);
    when(serviceRequestService.assignAgent(request.getServiceRequestId(), request.getAgentId()))
            .thenReturn(null);
    when(agentAssignmentRepository.findByServiceRequest_ServiceRequestIdAndAssignmentStatus(
            request.getServiceRequestId(), "ASSIGNED")).thenReturn(Optional.of(assigned));

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
void acceptAssignmentDelegatesToCanonicalRequestWorkflow() {
    AgentAssignmentEntity entity = assignment();
    when(agentAssignmentRepository.findById(entity.getAssignmentId())).thenReturn(Optional.of(entity));

    AgentAssignmentResponse response = service.acceptAssignment(entity.getAssignmentId());

    assertThat(response.getAssignmentId()).isEqualTo(entity.getAssignmentId());
    verify(serviceRequestService).acceptRequest(entity.getServiceRequest().getServiceRequestId(), "Agent accepted assignment");
}



    
    
}
