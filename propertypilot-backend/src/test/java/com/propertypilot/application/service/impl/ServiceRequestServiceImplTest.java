package com.propertypilot.application.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import com.propertypilot.application.dto.UpdateServiceRequestRequest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

import com.propertypilot.infrastructure.persistence.entity.Property;
import java.math.BigDecimal;

import com.propertypilot.application.dto.CreateServiceRequestRequest;
import com.propertypilot.application.dto.ServiceRequestResponse;

import com.propertypilot.application.service.BillingService;
import com.propertypilot.application.service.EligibilityEngine;

import com.propertypilot.domain.enums.BillingEntityType;
import com.propertypilot.domain.enums.InvoiceType;
import com.propertypilot.domain.enums.ServiceRequestStatus;

import com.propertypilot.infrastructure.persistence.entity.AgentAssignmentEntity;
import com.propertypilot.infrastructure.persistence.entity.AgentEntity;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;

import com.propertypilot.infrastructure.persistence.entity.ServiceEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;

import com.propertypilot.infrastructure.persistence.repository.AgentAssignmentRepository;
import com.propertypilot.infrastructure.persistence.repository.AgentRepository;
import com.propertypilot.infrastructure.persistence.repository.CustomerJpaRepository;
import com.propertypilot.infrastructure.persistence.repository.PropertyRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestStatusHistoryRepository;
import com.propertypilot.web.exception.BusinessException;
import com.propertypilot.web.exception.ResourceNotFoundException;

import java.util.Optional;
import java.util.UUID;


    
    @ExtendWith(MockitoExtension.class)
class ServiceRequestServiceImplTest {

    @Mock
    private ServiceRequestRepository serviceRequestRepository;

    @Mock
    private CustomerJpaRepository customerRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private ServiceRequestStatusHistoryRepository statusHistoryRepository;

    @Mock
    private EligibilityEngine eligibilityEngine;

    @Mock
    private AgentRepository agentRepository;

    @Mock
    private AgentAssignmentRepository agentAssignmentRepository;

    @Mock
    private BillingService billingService;

    @InjectMocks
    private ServiceRequestServiceImpl service;

    private CustomerEntity customer() {
    CustomerEntity customer = new CustomerEntity();
    customer.setCustomerId(UUID.randomUUID());
    return customer;
}

private Property property() {
    Property property = new Property();
    property.setPropertyId(UUID.randomUUID());
    return property;
}

private ServiceEntity serviceEntity() {
    ServiceEntity service = new ServiceEntity();
    service.setServiceId(UUID.randomUUID());
    return service;
}

private ServiceRequestEntity requestEntity() {

    ServiceRequestEntity entity =
            new ServiceRequestEntity();

    entity.setServiceRequestId(
            UUID.randomUUID());

    entity.setStatus(
            ServiceRequestStatus.NEW.name());

    entity.setCustomer(
            customer());

    entity.setProperty(
            property());

    entity.setService(
            serviceEntity());

    entity.setAmount(
            new BigDecimal("500"));

    return entity;
}

private AgentEntity agent() {

    AgentEntity agent =
            new AgentEntity();

    agent.setAgentId(
            UUID.randomUUID());

    agent.setStatus(
            "ACTIVE");

    return agent;
}

private AgentAssignmentEntity assignment() {

    AgentAssignmentEntity assignment =
            new AgentAssignmentEntity();

    assignment.setAssignmentId(
            UUID.randomUUID());

    assignment.setAssignmentStatus(
            "ASSIGNED");

    return assignment;
}

@Test
void createRequest_success_withInvoice() {

    CustomerEntity customer =
            customer();

    Property property =
            property();

    ServiceEntity serviceEntity =
            serviceEntity();

    CreateServiceRequestRequest request =
            new CreateServiceRequestRequest();

    request.setCustomerId(
            customer.getCustomerId());

    request.setPropertyId(
            property.getPropertyId());

    request.setServiceId(
            serviceEntity.getServiceId());

    request.setAmount(
            new BigDecimal("5000"));

    request.setPriority("HIGH");

    request.setRequestType(
            "DOCUMENT");

    when(customerRepository.findById(
            customer.getCustomerId()))
            .thenReturn(Optional.of(customer));

    when(propertyRepository.findById(
            property.getPropertyId()))
            .thenReturn(Optional.of(property));

    when(serviceRepository.findById(
            serviceEntity.getServiceId()))
            .thenReturn(Optional.of(serviceEntity));

when(serviceRequestRepository.save(any()))
        .thenAnswer(invocation -> {
            ServiceRequestEntity entity =
                    invocation.getArgument(0);

            entity.setServiceRequestId(
                    UUID.randomUUID());

            return entity;
        });

    service.createRequest(request);

    verify(eligibilityEngine)
            .validateEligibility(
                    serviceEntity.getServiceId(),
                    property.getPropertyId());

    verify(billingService)
            .createInvoice(
                    eq(BillingEntityType.SERVICE_REQUEST),
                    any(UUID.class),
                    eq(customer.getCustomerId()),
                    eq(InvoiceType.SERVICE_CHARGE),
                    eq(new BigDecimal("5000")),
                    eq("Service Request Invoice"));

    verify(statusHistoryRepository)
            .save(any());
}

@Test
void createRequest_customerNotFound() {

    CreateServiceRequestRequest request =
            new CreateServiceRequestRequest();

    UUID customerId =
            UUID.randomUUID();

    request.setCustomerId(customerId);

    when(customerRepository.findById(
            customerId))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.createRequest(request))
            .isInstanceOf(
                    ResourceNotFoundException.class)
            .hasMessage(
                    "Customer not found");
}

@Test
void createRequest_propertyNotFound() {

    CustomerEntity customer =
            customer();

    UUID propertyId =
            UUID.randomUUID();

    CreateServiceRequestRequest request =
            new CreateServiceRequestRequest();

    request.setCustomerId(
            customer.getCustomerId());

    request.setPropertyId(
            propertyId);

    when(customerRepository.findById(
            customer.getCustomerId()))
            .thenReturn(Optional.of(customer));

    when(propertyRepository.findById(
            propertyId))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.createRequest(request))
            .isInstanceOf(
                    ResourceNotFoundException.class)
            .hasMessage(
                    "Property not found");
}

@Test
void createRequest_serviceNotFound() {

    CustomerEntity customer =
            customer();

    Property property =
            property();

    UUID serviceId =
            UUID.randomUUID();

    CreateServiceRequestRequest request =
            new CreateServiceRequestRequest();

    request.setCustomerId(
            customer.getCustomerId());

    request.setPropertyId(
            property.getPropertyId());

    request.setServiceId(
            serviceId);

    when(customerRepository.findById(
            customer.getCustomerId()))
            .thenReturn(Optional.of(customer));

    when(propertyRepository.findById(
            property.getPropertyId()))
            .thenReturn(Optional.of(property));

    when(serviceRepository.findById(
            serviceId))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.createRequest(request))
            .isInstanceOf(
                    ResourceNotFoundException.class)
            .hasMessage(
                    "Service not found");
}


@Test
void getRequest_success() {

    ServiceRequestEntity entity =
            requestEntity();

    when(serviceRequestRepository.findById(
            entity.getServiceRequestId()))
            .thenReturn(Optional.of(entity));

    ServiceRequestResponse response =
            service.getRequest(
                    entity.getServiceRequestId());

    assertThat(response)
            .isNotNull();

    assertThat(response.getServiceRequestId())
            .isEqualTo(
                    entity.getServiceRequestId());
}

@Test
void getRequest_notFound() {

    UUID id =
            UUID.randomUUID();

    when(serviceRequestRepository.findById(id))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.getRequest(id))
            .isInstanceOf(
                    ResourceNotFoundException.class)
            .hasMessage(
                    "Service request not found");
}

@Test
void getCustomerRequests_success() {

    UUID customerId =
            UUID.randomUUID();

    ServiceRequestEntity entity =
            requestEntity();

    when(serviceRequestRepository
            .findByCustomerWithProperty(customerId))
            .thenReturn(
                    java.util.List.of(entity));

    var responses =
            service.getCustomerRequests(customerId);

    assertThat(responses)
            .hasSize(1);

    assertThat(responses.get(0)
            .getServiceRequestId())
            .isEqualTo(
                    entity.getServiceRequestId());
}

@Test
void updateRequest_success() {

    ServiceRequestEntity entity =
            requestEntity();

    when(serviceRequestRepository.findById(
            entity.getServiceRequestId()))
            .thenReturn(Optional.of(entity));

    when(serviceRequestRepository.save(any()))
            .thenAnswer(i -> i.getArgument(0));

    UpdateServiceRequestRequest request =
            new UpdateServiceRequestRequest();

    UUID assignedTo =
            UUID.randomUUID();

    request.setAssignedTo(assignedTo);

    ServiceRequestResponse response =
            service.updateRequest(
                    entity.getServiceRequestId(),
                    request);

    assertThat(response.getAssignedTo())
            .isEqualTo(assignedTo);
}

@Test
void updateRequest_notFound() {

    UUID id =
            UUID.randomUUID();

    when(serviceRequestRepository.findById(id))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.updateRequest(
                    id,
                    new UpdateServiceRequestRequest()))
            .isInstanceOf(
                    ResourceNotFoundException.class)
            .hasMessage(
                    "Service request not found");
}

@Test
void assignRequest_success() {

    ServiceRequestEntity entity =
            requestEntity();

    UUID assignedTo =
            UUID.randomUUID();

    UUID assignedBy =
            UUID.randomUUID();

    when(serviceRequestRepository.findById(
            entity.getServiceRequestId()))
            .thenReturn(Optional.of(entity));

    when(serviceRequestRepository.save(any()))
            .thenAnswer(i -> i.getArgument(0));

    ServiceRequestResponse response =
            service.assignRequest(
                    entity.getServiceRequestId(),
                    assignedTo,
                    assignedBy,
                    "Assigned");

    assertThat(response.getStatus())
            .isEqualTo(
                    ServiceRequestStatus.ASSIGNED.name());

    verify(statusHistoryRepository)
            .save(any());
}

@Test
void assignRequest_notFound() {

    UUID id =
            UUID.randomUUID();

    when(serviceRequestRepository.findById(id))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.assignRequest(
                    id,
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    "Assign"))
            .isInstanceOf(
                    ResourceNotFoundException.class)
            .hasMessage(
                    "Service request not found");
}

@Test
void assignAgent_success() {

    ServiceRequestEntity request =
            requestEntity();

    AgentEntity agent =
            agent();

    when(serviceRequestRepository.findById(
            request.getServiceRequestId()))
            .thenReturn(Optional.of(request));

    when(agentRepository.findByAgentIdAndStatus(
            agent.getAgentId(),
            "ACTIVE"))
            .thenReturn(Optional.of(agent));

    when(agentAssignmentRepository
            .findByServiceRequest_ServiceRequestIdAndAssignmentStatusIn(
                    eq(request.getServiceRequestId()),
                    any()))
            .thenReturn(Optional.empty());

    when(agentAssignmentRepository.save(any()))
            .thenAnswer(i -> i.getArgument(0));

    when(serviceRequestRepository.save(any()))
            .thenAnswer(i -> i.getArgument(0));

    ServiceRequestResponse response =
            service.assignAgent(
                    request.getServiceRequestId(),
                    agent.getAgentId());

    assertThat(response.getStatus())
            .isEqualTo(
                    ServiceRequestStatus.ASSIGNED.name());
}

@Test
void assignAgent_agentNotFound() {

    ServiceRequestEntity request =
            requestEntity();

    when(serviceRequestRepository.findById(
            request.getServiceRequestId()))
            .thenReturn(Optional.of(request));

    when(agentRepository.findByAgentIdAndStatus(
            any(),
            eq("ACTIVE")))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.assignAgent(
                    request.getServiceRequestId(),
                    UUID.randomUUID()))
            .isInstanceOf(
                    ResourceNotFoundException.class)
            .hasMessage(
                    "Active agent not found");
}

@Test
void assignAgent_existingAssignment() {

    ServiceRequestEntity request =
            requestEntity();

    AgentEntity agent =
            agent();

    AgentAssignmentEntity assignment =
            assignment();

    when(serviceRequestRepository.findById(
            request.getServiceRequestId()))
            .thenReturn(Optional.of(request));

    when(agentRepository.findByAgentIdAndStatus(
            agent.getAgentId(),
            "ACTIVE"))
            .thenReturn(Optional.of(agent));

    when(agentAssignmentRepository
            .findByServiceRequest_ServiceRequestIdAndAssignmentStatusIn(
                    eq(request.getServiceRequestId()),
                    any()))
            .thenReturn(Optional.of(assignment));

    assertThatThrownBy(() ->
            service.assignAgent(
                    request.getServiceRequestId(),
                    agent.getAgentId()))
            .isInstanceOf(
                    IllegalStateException.class);
}

@Test
void acceptRequest_success() {

    ServiceRequestEntity request =
            requestEntity();

    request.setStatus(
            ServiceRequestStatus.ASSIGNED.name());

    AgentAssignmentEntity assignment =
            assignment();

    when(serviceRequestRepository.findById(
            request.getServiceRequestId()))
            .thenReturn(Optional.of(request));

    when(agentAssignmentRepository
            .findByServiceRequest_ServiceRequestIdAndAssignmentStatus(
                    request.getServiceRequestId(),
                    "ASSIGNED"))
            .thenReturn(Optional.of(assignment));

    when(serviceRequestRepository.save(any()))
            .thenAnswer(i -> i.getArgument(0));

    ServiceRequestResponse response =
            service.acceptRequest(
                    request.getServiceRequestId(),
                    "Accepted");

    assertThat(response.getStatus())
            .isEqualTo(
                    ServiceRequestStatus.ACCEPTED.name());
}

@Test
void acceptRequest_assignmentNotFound() {

    ServiceRequestEntity request =
            requestEntity();

    request.setStatus(
            ServiceRequestStatus.ASSIGNED.name());

    when(serviceRequestRepository.findById(
            request.getServiceRequestId()))
            .thenReturn(Optional.of(request));

    when(agentAssignmentRepository
            .findByServiceRequest_ServiceRequestIdAndAssignmentStatus(
                    request.getServiceRequestId(),
                    "ASSIGNED"))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.acceptRequest(
                    request.getServiceRequestId(),
                    "Accepted"))
            .isInstanceOf(
                    ResourceNotFoundException.class);
}

@Test
void startRequest_success() {

    ServiceRequestEntity request =
            requestEntity();

    request.setStatus(
            ServiceRequestStatus.ACCEPTED.name());

    AgentAssignmentEntity assignment =
            assignment();

    assignment.setAssignmentStatus(
            "ACCEPTED");

    when(serviceRequestRepository.findById(
            request.getServiceRequestId()))
            .thenReturn(Optional.of(request));

    when(agentAssignmentRepository
            .findByServiceRequest_ServiceRequestIdAndAssignmentStatus(
                    request.getServiceRequestId(),
                    "ACCEPTED"))
            .thenReturn(Optional.of(assignment));

    when(serviceRequestRepository.save(any()))
            .thenAnswer(i -> i.getArgument(0));

    ServiceRequestResponse response =
            service.startRequest(
                    request.getServiceRequestId(),
                    "Started");

    assertThat(response.getStatus())
            .isEqualTo(
                    ServiceRequestStatus.IN_PROGRESS.name());
}

@Test
void startRequest_assignmentNotFound() {

    ServiceRequestEntity request =
            requestEntity();

    request.setStatus(
            ServiceRequestStatus.ACCEPTED.name());

    when(serviceRequestRepository.findById(
            request.getServiceRequestId()))
            .thenReturn(Optional.of(request));

    when(agentAssignmentRepository
            .findByServiceRequest_ServiceRequestIdAndAssignmentStatus(
                    request.getServiceRequestId(),
                    "ACCEPTED"))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.startRequest(
                    request.getServiceRequestId(),
                    "Start"))
            .isInstanceOf(
                    ResourceNotFoundException.class);
}

@Test
void completeRequest_success() {

    ServiceRequestEntity request =
            requestEntity();

    request.setStatus(
            ServiceRequestStatus.IN_PROGRESS.name());

    when(serviceRequestRepository.findById(
            request.getServiceRequestId()))
            .thenReturn(Optional.of(request));

    when(serviceRequestRepository.save(any()))
            .thenAnswer(i -> i.getArgument(0));

    ServiceRequestResponse response =
            service.completeRequest(
                    request.getServiceRequestId(),
                    "Completed");

    assertThat(response.getStatus())
            .isEqualTo(
                    ServiceRequestStatus.COMPLETED.name());

    assertThat(response.getCompletedAt())
            .isNotNull();
}

@Test
void completeRequest_invalidStatus() {

    ServiceRequestEntity request =
            requestEntity();

    request.setStatus(
            ServiceRequestStatus.NEW.name());

    when(serviceRequestRepository.findById(
            request.getServiceRequestId()))
            .thenReturn(Optional.of(request));

    assertThatThrownBy(() ->
            service.completeRequest(
                    request.getServiceRequestId(),
                    "Completed"))
            .isInstanceOf(
                    BusinessException.class)
            .hasMessage(
                    "Only IN_PROGRESS requests can be completed");
}

@Test
void cancelRequest_success() {

    ServiceRequestEntity request =
            requestEntity();

    when(serviceRequestRepository.findById(
            request.getServiceRequestId()))
            .thenReturn(Optional.of(request));

    when(serviceRequestRepository.save(any()))
            .thenAnswer(i -> i.getArgument(0));

    ServiceRequestResponse response =
            service.cancelRequest(
                    request.getServiceRequestId(),
                    "Cancelled");

    assertThat(response.getStatus())
            .isEqualTo(
                    ServiceRequestStatus.CANCELLED.name());
}





    
}



