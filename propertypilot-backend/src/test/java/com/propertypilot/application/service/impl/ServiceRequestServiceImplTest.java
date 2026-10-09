package com.propertypilot.application.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.never;
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
import com.propertypilot.application.service.CompletionValidationService;
import com.propertypilot.security.SecurityService;

import com.propertypilot.domain.enums.BillingEntityType;
import com.propertypilot.domain.enums.InvoiceType;
import com.propertypilot.domain.enums.ServiceRequestStatus;

import com.propertypilot.infrastructure.persistence.entity.AgentAssignmentEntity;
import com.propertypilot.infrastructure.persistence.entity.AgentEntity;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;

import com.propertypilot.infrastructure.persistence.entity.ServiceEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;
import com.propertypilot.infrastructure.persistence.entity.PricingEstimateEntity;
import com.propertypilot.infrastructure.persistence.entity.LeadEntity;

import com.propertypilot.infrastructure.persistence.repository.AgentAssignmentRepository;
import com.propertypilot.infrastructure.persistence.repository.AgentRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestStatusHistoryRepository;
import com.propertypilot.infrastructure.persistence.repository.PricingEstimateRepository;
import com.propertypilot.infrastructure.persistence.repository.LeadRepository;
import com.propertypilot.infrastructure.persistence.repository.LeadActivityRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceSlaPolicyRepository;
import com.propertypilot.infrastructure.persistence.repository.UserRepository;
import com.propertypilot.web.exception.BusinessException;
import com.propertypilot.web.exception.ResourceNotFoundException;

import java.util.Optional;
import java.util.List;
import java.util.UUID;


    
    @ExtendWith(MockitoExtension.class)
class ServiceRequestServiceImplTest {

    @Mock
    private ServiceRequestRepository serviceRequestRepository;

    @Mock
    private ServiceRequestStatusHistoryRepository statusHistoryRepository;

    @Mock
    private PricingEstimateRepository pricingEstimateRepository;

    @Mock
    private EligibilityEngine eligibilityEngine;

    @Mock
    private AgentRepository agentRepository;

    @Mock
    private AgentAssignmentRepository agentAssignmentRepository;

    @Mock
    private BillingService billingService;

    @Mock
    private CompletionValidationService completionValidationService;

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private LeadActivityRepository leadActivityRepository;

    @Mock
    private ServiceSlaPolicyRepository slaPolicyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private ServiceRequestServiceImpl service;

    @Test
    void getRequestsForCurrentAgentReturnsEmptyForAgentWithoutAssignments() {
        UUID userId = UUID.randomUUID();
        AgentEntity agent = new AgentEntity();
        agent.setAgentId(UUID.randomUUID());
        when(securityService.hasRole("AGENT")).thenReturn(true);
        when(securityService.getCurrentUserId()).thenReturn(userId);
        when(agentRepository.findByUser_UserIdAndStatus(userId, "ACTIVE")).thenReturn(Optional.of(agent));
        when(agentAssignmentRepository.findByAgent_AgentId(agent.getAgentId())).thenReturn(List.of());

        assertThat(service.getRequestsForCurrentAgent()).isEmpty();
    }

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

private PricingEstimateEntity acceptedQuote() {
    CustomerEntity customer = customer();
    Property property = property();
    property.setCustomer(customer);
    ServiceEntity catalogService = serviceEntity();
    catalogService.setServiceCode("PROPERTY_INSPECTION");
    catalogService.setServiceName("Property inspection");
    catalogService.setActiveFlag(true);
    PricingEstimateEntity quote = new PricingEstimateEntity();
    quote.setPricingEstimateId(UUID.randomUUID());
    quote.setCustomer(customer);
    quote.setProperty(property);
    quote.setService(catalogService);
    quote.setStatus("ACCEPTED");
    quote.setAcceptedAt(java.time.OffsetDateTime.now().minusMinutes(1));
    quote.setExpiresAt(java.time.OffsetDateTime.now().plusMinutes(15));
    quote.setTotalAmount(new BigDecimal("1250.75"));
    return quote;
}

@Test
void createRequest_success_usesAcceptedQuoteAndDoesNotCreateInvoice() {
    PricingEstimateEntity quote = acceptedQuote();
    CreateServiceRequestRequest request = new CreateServiceRequestRequest();
    request.setQuoteId(quote.getPricingEstimateId());
    request.setPriority("HIGH");
    request.setDescription("Please inspect the property");
    when(pricingEstimateRepository.findByIdForUpdate(quote.getPricingEstimateId()))
            .thenReturn(Optional.of(quote));
    when(serviceRequestRepository.existsByPricingEstimate_PricingEstimateId(quote.getPricingEstimateId()))
            .thenReturn(false);
    when(serviceRequestRepository.saveAndFlush(any())).thenAnswer(invocation -> {
        ServiceRequestEntity entity = invocation.getArgument(0);
        entity.setServiceRequestId(UUID.randomUUID());
        return entity;
    });

    ServiceRequestResponse response = service.createRequest(request);

    assertThat(response.getQuoteId()).isEqualTo(quote.getPricingEstimateId());
    assertThat(response.getCustomerId()).isEqualTo(quote.getCustomer().getCustomerId());
    assertThat(response.getPropertyId()).isEqualTo(quote.getProperty().getPropertyId());
    assertThat(response.getServiceId()).isEqualTo(quote.getService().getServiceId());
    assertThat(response.getAmount()).isEqualByComparingTo("1250.75");
    assertThat(response.getRequestType()).isEqualTo("PROPERTY_INSPECTION");
    verify(serviceRequestRepository).saveAndFlush(any(ServiceRequestEntity.class));
    verify(eligibilityEngine).validateEligibility(quote.getService().getServiceId(), quote.getProperty().getPropertyId());
    verify(statusHistoryRepository).save(any());
    verifyNoInteractions(billingService);
}

@Test
void createRequest_capturesTheActiveSlaDeadlineAtRequestCreation() {
    PricingEstimateEntity quote = acceptedQuote();
    var policy = new com.propertypilot.infrastructure.persistence.entity.ServiceSlaPolicyEntity();
    UUID policyId = UUID.randomUUID();
    policy.setSlaPolicyId(policyId);
    policy.setCompletionMinutes(90);
    when(pricingEstimateRepository.findByIdForUpdate(quote.getPricingEstimateId())).thenReturn(Optional.of(quote));
    when(serviceRequestRepository.existsByPricingEstimate_PricingEstimateId(quote.getPricingEstimateId())).thenReturn(false);
    when(slaPolicyRepository.findByService_ServiceIdAndStatusIgnoreCaseOrderByCreatedAtDesc(
            quote.getService().getServiceId(), "ACTIVE")).thenReturn(List.of(policy));
    when(serviceRequestRepository.saveAndFlush(any())).thenAnswer(invocation -> {
        ServiceRequestEntity entity = invocation.getArgument(0);
        entity.setServiceRequestId(UUID.randomUUID());
        return entity;
    });

    CreateServiceRequestRequest request = new CreateServiceRequestRequest();
    request.setQuoteId(quote.getPricingEstimateId());
    request.setPriority("MEDIUM");
    request.setDescription("Please inspect the property");
    ServiceRequestResponse response = service.createRequest(request);

    assertThat(response.getSlaDueAt()).isNotNull();
    assertThat(response.getSlaStatus()).isEqualTo("ON_TRACK");
    assertThat(java.time.Duration.between(java.time.Instant.now().plusSeconds(90 * 60), response.getSlaDueAt()).abs().toSeconds())
            .isLessThan(5);
    org.mockito.ArgumentCaptor<ServiceRequestEntity> saved = org.mockito.ArgumentCaptor.forClass(ServiceRequestEntity.class);
    verify(serviceRequestRepository).saveAndFlush(saved.capture());
    assertThat(saved.getValue().isSlaSnapshotCaptured()).isTrue();
    assertThat(saved.getValue().getSlaPolicyIdSnapshot()).isEqualTo(policyId);

    when(serviceRequestRepository.findById(response.getServiceRequestId())).thenReturn(Optional.of(saved.getValue()));
    ServiceRequestResponse reloaded = service.getRequest(response.getServiceRequestId());
    assertThat(reloaded.getSlaDueAt()).isEqualTo(response.getSlaDueAt());
    verify(slaPolicyRepository).findByService_ServiceIdAndStatusIgnoreCaseOrderByCreatedAtDesc(
            quote.getService().getServiceId(), "ACTIVE");
}

@Test
void createRequest_withLeadPersistsLinkAndAddsLeadAuditActivity() {
    PricingEstimateEntity quote = acceptedQuote();
    LeadEntity lead = new LeadEntity();
    UUID leadId = UUID.randomUUID();
    lead.setLeadId(leadId);
    lead.setCustomer(quote.getCustomer());
    lead.setNotes("Need title verification");
    lead.setSource("CUSTOMER");
    lead.setStatus(com.propertypilot.domain.enums.LeadStatus.NEW);
    lead.setPropertyType("Apartment");
    lead.setLocation("Pune");
    CreateServiceRequestRequest request = new CreateServiceRequestRequest();
    request.setQuoteId(quote.getPricingEstimateId());
    request.setLeadId(leadId);
    request.setPriority("HIGH");
    request.setDescription("Please inspect the property");
    when(pricingEstimateRepository.findByIdForUpdate(quote.getPricingEstimateId())).thenReturn(Optional.of(quote));
    when(serviceRequestRepository.existsByPricingEstimate_PricingEstimateId(quote.getPricingEstimateId())).thenReturn(false);
    when(leadRepository.findByLeadId(leadId)).thenReturn(Optional.of(lead));
    when(serviceRequestRepository.saveAndFlush(any())).thenAnswer(invocation -> {
        ServiceRequestEntity entity = invocation.getArgument(0);
        entity.setServiceRequestId(UUID.randomUUID());
        return entity;
    });

    ServiceRequestResponse response = service.createRequest(request);

    assertThat(response.getLeadId()).isEqualTo(leadId);
    verify(leadActivityRepository).save(any());
}

@Test
void createRequest_rejectsQuoteThatIsNotAccepted() {
    PricingEstimateEntity quote = acceptedQuote();
    quote.setStatus("ACTIVE");
    CreateServiceRequestRequest request = new CreateServiceRequestRequest();
    request.setQuoteId(quote.getPricingEstimateId());
    when(pricingEstimateRepository.findByIdForUpdate(quote.getPricingEstimateId()))
            .thenReturn(Optional.of(quote));

    assertThatThrownBy(() -> service.createRequest(request))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("accepted quote");
    verify(serviceRequestRepository, never()).saveAndFlush(any());
}

@Test
void createRequest_preventsDuplicateForAcceptedQuote() {
    PricingEstimateEntity quote = acceptedQuote();
    CreateServiceRequestRequest request = new CreateServiceRequestRequest();
    request.setQuoteId(quote.getPricingEstimateId());
    when(pricingEstimateRepository.findByIdForUpdate(quote.getPricingEstimateId()))
            .thenReturn(Optional.of(quote));
    when(serviceRequestRepository.existsByPricingEstimate_PricingEstimateId(quote.getPricingEstimateId()))
            .thenReturn(true);

    assertThatThrownBy(() -> service.createRequest(request))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("already exists");
    verify(serviceRequestRepository, never()).saveAndFlush(any());
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

    when(serviceRequestRepository.findByIdForUpdate(
            entity.getServiceRequestId()))
            .thenReturn(Optional.of(entity));

    when(serviceRequestRepository.save(any()))
            .thenAnswer(i -> i.getArgument(0));

    UpdateServiceRequestRequest request =
            new UpdateServiceRequestRequest();

    request.setPriority("HIGH");
    request.setDescription("Updated service details");

    ServiceRequestResponse response =
            service.updateRequest(
                    entity.getServiceRequestId(),
                    request);

    assertThat(response.getPriority()).isEqualTo("HIGH");
    assertThat(response.getDescription()).isEqualTo("Updated service details");
    verify(serviceRequestRepository).findByIdForUpdate(entity.getServiceRequestId());
}

@Test
void updateRequest_rejectsWorkflowChangesOutsideWorkflowActions() {
    ServiceRequestEntity entity = requestEntity();
    UpdateServiceRequestRequest request = new UpdateServiceRequestRequest();
    request.setAssignedTo(UUID.randomUUID());

    assertThatThrownBy(() -> service.updateRequest(entity.getServiceRequestId(), request))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Use the service request workflow actions");

    verify(serviceRequestRepository, never()).save(any());
    verify(serviceRequestRepository, never()).findByIdForUpdate(any());
}

@Test
void updateRequest_notFound() {

    UUID id =
            UUID.randomUUID();

    when(serviceRequestRepository.findByIdForUpdate(id))
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

    when(serviceRequestRepository.findByIdForUpdate(
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

    when(serviceRequestRepository.findByIdForUpdate(id))
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

    when(serviceRequestRepository.findByIdForUpdate(
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

    when(serviceRequestRepository.findByIdForUpdate(
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

    when(serviceRequestRepository.findByIdForUpdate(
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
void rejectAssignment_returnsRequestToPendingQueueAndRecordsHistory() {
    ServiceRequestEntity request = requestEntity();
    request.setStatus(ServiceRequestStatus.ASSIGNED.name());
    request.setAssignedTo(UUID.randomUUID());
    AgentAssignmentEntity activeAssignment = assignment();
    when(serviceRequestRepository.findByIdForUpdate(request.getServiceRequestId())).thenReturn(Optional.of(request));
    when(agentAssignmentRepository.findByServiceRequest_ServiceRequestIdAndAssignmentStatus(
            request.getServiceRequestId(), "ASSIGNED")).thenReturn(Optional.of(activeAssignment));
    when(serviceRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    ServiceRequestResponse response = service.rejectAssignment(request.getServiceRequestId(), "Agent unavailable");

    assertThat(response.getStatus()).isEqualTo(ServiceRequestStatus.PENDING_ASSIGNMENT.name());
    assertThat(request.getAssignedTo()).isNull();
    assertThat(activeAssignment.getAssignmentStatus()).isEqualTo("REJECTED");
    verify(statusHistoryRepository).save(any());
}

@Test
void acceptRequest_success() {

    ServiceRequestEntity request =
            requestEntity();

    request.setStatus(
            ServiceRequestStatus.ASSIGNED.name());

    AgentAssignmentEntity assignment =
            assignment();

    when(serviceRequestRepository.findByIdForUpdate(
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

    when(serviceRequestRepository.findByIdForUpdate(
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

    when(serviceRequestRepository.findByIdForUpdate(
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

    when(serviceRequestRepository.findByIdForUpdate(
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

    AgentAssignmentEntity activeAssignment = assignment();

    when(serviceRequestRepository.findByIdForUpdate(
            request.getServiceRequestId()))
            .thenReturn(Optional.of(request));

    when(agentAssignmentRepository
            .findByServiceRequest_ServiceRequestIdAndAssignmentStatus(
                    request.getServiceRequestId(), "IN_PROGRESS"))
            .thenReturn(Optional.of(activeAssignment));

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
    verify(serviceRequestRepository).findByIdForUpdate(request.getServiceRequestId());
    verify(securityService).validateAssignmentAccess(activeAssignment);
    verify(securityService, never()).validateOperationsAccess();
}

@Test
void completeBillableQuotedRequest_CreatesInvoiceFromCompletedRequest() {
    ServiceRequestEntity request = requestEntity();
    request.setStatus(ServiceRequestStatus.IN_PROGRESS.name());
    request.setPricingEstimate(new PricingEstimateEntity());
    request.getService().setBillable(true);
    AgentAssignmentEntity activeAssignment = assignment();
    when(serviceRequestRepository.findByIdForUpdate(request.getServiceRequestId())).thenReturn(Optional.of(request));
    when(agentAssignmentRepository.findByServiceRequest_ServiceRequestIdAndAssignmentStatus(
            request.getServiceRequestId(), "IN_PROGRESS")).thenReturn(Optional.of(activeAssignment));
    when(serviceRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    service.completeRequest(request.getServiceRequestId(), "Completed with verified evidence");

    verify(billingService).createInvoiceFromCompletedServiceRequest(request);
}

@Test
void completeRequest_invalidStatus() {

    ServiceRequestEntity request =
            requestEntity();

    request.setStatus(
            ServiceRequestStatus.NEW.name());

    when(serviceRequestRepository.findByIdForUpdate(
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
void completeRequestIfReady_doesNotCompleteWhenEvidenceRequirementsArePending() {
    ServiceRequestEntity request = requestEntity();
    request.setStatus(ServiceRequestStatus.IN_PROGRESS.name());
    when(serviceRequestRepository.findById(request.getServiceRequestId())).thenReturn(Optional.of(request));
    when(completionValidationService.isCompletionReady(request)).thenReturn(false);

    service.completeRequestIfReady(request.getServiceRequestId(), "Visit completed");

    verify(completionValidationService).isCompletionReady(request);
    verify(completionValidationService, never()).validateCompletionRequirements(any());
    verify(agentAssignmentRepository, never())
            .findByServiceRequest_ServiceRequestIdAndAssignmentStatus(any(), any());
    verify(serviceRequestRepository, never()).save(any());
}

@Test
void completeRequestIfReady_ignoresRequestsOutsideInProgress() {
    ServiceRequestEntity request = requestEntity();
    request.setStatus(ServiceRequestStatus.ACCEPTED.name());
    when(serviceRequestRepository.findById(request.getServiceRequestId())).thenReturn(Optional.of(request));

    service.completeRequestIfReady(request.getServiceRequestId(), "Visit completed");

    verify(completionValidationService, never()).isCompletionReady(any());
    verify(agentAssignmentRepository, never())
            .findByServiceRequest_ServiceRequestIdAndAssignmentStatus(any(), any());
    verify(serviceRequestRepository, never()).save(any());
}

@Test
void cancelRequest_success() {

    ServiceRequestEntity request =
            requestEntity();

    when(serviceRequestRepository.findByIdForUpdate(
            request.getServiceRequestId()))
            .thenReturn(Optional.of(request));
    when(securityService.hasRole("CUSTOMER")).thenReturn(true);

    when(serviceRequestRepository.save(any()))
            .thenAnswer(i -> i.getArgument(0));

    ServiceRequestResponse response =
            service.cancelRequest(
                    request.getServiceRequestId(),
                    "Cancelled");

    assertThat(response.getStatus())
            .isEqualTo(
                    ServiceRequestStatus.CANCELLED.name());
    verify(serviceRequestRepository).findByIdForUpdate(request.getServiceRequestId());
}

@Test
void cancelRequest_rejectsAgent() {
    ServiceRequestEntity request = requestEntity();
    when(serviceRequestRepository.findByIdForUpdate(request.getServiceRequestId()))
            .thenReturn(Optional.of(request));
    when(securityService.hasRole("CUSTOMER")).thenReturn(false);

    assertThatThrownBy(() -> service.cancelRequest(request.getServiceRequestId(), "Cancel"))
            .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);

    verify(serviceRequestRepository, never()).save(any());
}





    
}



