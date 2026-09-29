package com.propertypilot.application.service.impl;

import com.propertypilot.domain.enums.BillingEntityType;
import com.propertypilot.domain.enums.InvoiceType;
import com.propertypilot.application.dto.CreateServiceRequestRequest;
import com.propertypilot.application.dto.ServiceRequestResponse;
import com.propertypilot.application.dto.UpdateServiceRequestRequest;
import com.propertypilot.application.service.BillingService;
import com.propertypilot.application.service.EligibilityEngine;
import com.propertypilot.application.service.ServiceRequestService;
import com.propertypilot.domain.enums.ServiceRequestStatus;
import com.propertypilot.infrastructure.persistence.entity.AgentAssignmentEntity;
import com.propertypilot.infrastructure.persistence.entity.AgentEntity;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.Property;
import com.propertypilot.infrastructure.persistence.entity.ServiceEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;
import com.propertypilot.infrastructure.persistence.repository.AgentAssignmentRepository;
import com.propertypilot.infrastructure.persistence.repository.AgentRepository;
import com.propertypilot.infrastructure.persistence.repository.CustomerJpaRepository;
import com.propertypilot.infrastructure.persistence.repository.PropertyRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.propertypilot.infrastructure.persistence.entity.ServiceRequestStatusHistoryEntity;
import com.propertypilot.infrastructure.persistence.repository.ServiceRequestStatusHistoryRepository;
import com.propertypilot.web.exception.BusinessException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;



@Service
@Transactional
public class ServiceRequestServiceImpl
        implements ServiceRequestService {

    private final ServiceRequestRepository
            serviceRequestRepository;

    private final CustomerJpaRepository
            customerRepository;

    private final PropertyRepository
            propertyRepository;

    private final ServiceRepository
            serviceRepository;

    private final ServiceRequestStatusHistoryRepository
            statusHistoryRepository;
private final EligibilityEngine
        eligibilityEngine;

        private final AgentRepository
        agentRepository;

private final AgentAssignmentRepository
        agentAssignmentRepository;

        private final BillingService billingService;

    public ServiceRequestServiceImpl(
        ServiceRequestRepository serviceRequestRepository,
        CustomerJpaRepository customerRepository,
        PropertyRepository propertyRepository,
        ServiceRepository serviceRepository,
        ServiceRequestStatusHistoryRepository statusHistoryRepository,
        EligibilityEngine eligibilityEngine,
AgentRepository agentRepository,
AgentAssignmentRepository agentAssignmentRepository,
BillingService billingService) {

        this.serviceRequestRepository =
                serviceRequestRepository;

        this.customerRepository =
                customerRepository;

        this.propertyRepository =
                propertyRepository;

        this.serviceRepository =
                serviceRepository;

        this.statusHistoryRepository =
                statusHistoryRepository;

                this.eligibilityEngine =
        eligibilityEngine;

        this.agentRepository =
        agentRepository;

this.agentAssignmentRepository =
        agentAssignmentRepository;

        this.billingService = billingService;
             
    }

    @Override
    public ServiceRequestResponse createRequest(
            CreateServiceRequestRequest request) {
                

        CustomerEntity customer =
                customerRepository
                        .findById(request.getCustomerId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found"));

        Property property =
                propertyRepository
                        .findById(request.getPropertyId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Property not found"));

        ServiceEntity service =
                serviceRepository
                        .findById(request.getServiceId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Service not found"));

        ServiceRequestEntity entity =
                new ServiceRequestEntity();

        entity.setCustomer(customer);
        entity.setProperty(property);
        entity.setService(service);

        entity.setRequestType(
                request.getRequestType());

        entity.setPriority(
                request.getPriority());

        entity.setAmount(
                request.getAmount());

        entity.setDescription(
                request.getDescription());

        entity.setStatus(
        ServiceRequestStatus.NEW.name());

entity.setRequestedAt(
        Instant.now());
         
        eligibilityEngine.validateEligibility(
        service.getServiceId(),
        property.getPropertyId());

ServiceRequestEntity saved =
        serviceRequestRepository.save(
                entity);

                if (saved.getAmount() != null
        && saved.getAmount().compareTo(BigDecimal.ZERO) > 0) {

    billingService.createInvoice(
            BillingEntityType.SERVICE_REQUEST,
            saved.getServiceRequestId(),
            saved.getCustomer().getCustomerId(),
            InvoiceType.SERVICE_CHARGE,
            saved.getAmount(),
            "Service Request Invoice");
}


createStatusHistory(
        saved,
        null,
        ServiceRequestStatus.NEW.name(),
        "Request created");

return buildResponse(saved);
}

    @Override
    public ServiceRequestResponse getRequest(
            UUID serviceRequestId) {

        return buildResponse(
                serviceRequestRepository
                        .findById(serviceRequestId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Service request not found")));
    }

    @Override
    public List<ServiceRequestResponse>
    getCustomerRequests(
            UUID customerId) {

        return serviceRequestRepository
                .findByCustomerWithProperty(customerId)
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    @Override
    public ServiceRequestResponse updateRequest(
            UUID serviceRequestId,
            UpdateServiceRequestRequest request) {

        ServiceRequestEntity entity =
                serviceRequestRepository
                        .findById(serviceRequestId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Service request not found"));

    

        entity.setScheduledAt(
                request.getScheduledAt());

        entity.setCompletedAt(
                request.getCompletedAt());

        entity.setAssignedTo(
                request.getAssignedTo());

        entity.setAssignedBy(
                request.getAssignedBy());

        entity.setCancellationReasonCode(
                request.getCancellationReasonCode());

        if (request.getAssignedTo() != null) {
            entity.setAssignedAt(
                    Instant.now());
        }

        return buildResponse(
                serviceRequestRepository.save(entity));
    }
    private void validateTransition(
        String currentStatus,
        String targetStatus) {

    if (ServiceRequestStatus.COMPLETED.name()
            .equals(currentStatus)
            ||
            ServiceRequestStatus.CANCELLED.name()
                    .equals(currentStatus)) {

        throw new BusinessException(
                "Terminal state reached: "
                        + currentStatus);
    }

    boolean valid =

            (ServiceRequestStatus.NEW.name()
                    .equals(currentStatus)
                    &&
                    (
                            ServiceRequestStatus.ASSIGNED.name()
                                    .equals(targetStatus)
                                    ||
                            ServiceRequestStatus.CANCELLED.name()
                                    .equals(targetStatus)
                    ))

            ||

            (ServiceRequestStatus.ASSIGNED.name()
                    .equals(currentStatus)
                    &&
                    (
                            ServiceRequestStatus.ACCEPTED.name()
                                    .equals(targetStatus)
                                    ||
                            ServiceRequestStatus.CANCELLED.name()
                                    .equals(targetStatus)
                    ))

            ||

            (ServiceRequestStatus.ACCEPTED.name()
                    .equals(currentStatus)
                    &&
                    (
                            ServiceRequestStatus.IN_PROGRESS.name()
                                    .equals(targetStatus)
                                    ||
                            ServiceRequestStatus.CANCELLED.name()
                                    .equals(targetStatus)
                    ))

            ||

            (ServiceRequestStatus.IN_PROGRESS.name()
                    .equals(currentStatus)
                    &&
                    (
                            ServiceRequestStatus.COMPLETED.name()
                                    .equals(targetStatus)
                                    ||
                            ServiceRequestStatus.CANCELLED.name()
                                    .equals(targetStatus)
                    ));

    if (!valid) {

        throw new BusinessException(
                "Invalid transition from "
                        + currentStatus
                        + " to "
                        + targetStatus);
    }
}
@Override
public ServiceRequestResponse assignRequest(
        UUID serviceRequestId,
        UUID assignedTo,
        UUID assignedBy,
        String reason) {

    ServiceRequestEntity entity =
            serviceRequestRepository
                    .findById(serviceRequestId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Service request not found"));

    validateTransition(
            entity.getStatus(),
            ServiceRequestStatus.ASSIGNED.name());

    entity.setAssignedTo(
            assignedTo);

    entity.setAssignedBy(
            assignedBy);

    entity.setAssignedAt(
            Instant.now());

    String previousStatus =
            entity.getStatus();

    entity.setStatus(
            ServiceRequestStatus.ASSIGNED.name());

    createStatusHistory(
            entity,
            previousStatus,
            ServiceRequestStatus.ASSIGNED.name(),
            reason);

    return buildResponse(
            serviceRequestRepository.save(entity));
}

@Override
public ServiceRequestResponse completeRequest(
        UUID serviceRequestId,
        String reason) {

    ServiceRequestEntity request =
            serviceRequestRepository
                    .findById(serviceRequestId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Service request not found"));

    if (!ServiceRequestStatus.IN_PROGRESS.name()
            .equals(request.getStatus())) {

        throw new BusinessException(
                "Only IN_PROGRESS requests can be completed");
    }

    return changeStatus(
            serviceRequestId,
            ServiceRequestStatus.COMPLETED.name(),
            reason);
}
@Override
public ServiceRequestResponse cancelRequest(
        UUID serviceRequestId,
        String reason) {

    return changeStatus(
            serviceRequestId,
            ServiceRequestStatus.CANCELLED.name(),
            reason);
}
@Override
public ServiceRequestResponse assignAgent(
        UUID serviceRequestId,
        UUID agentId) {

               
    ServiceRequestEntity request =
            serviceRequestRepository
                    .findById(serviceRequestId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Service request not found"));
 validateTransition(
    request.getStatus(),
    ServiceRequestStatus.ASSIGNED.name());

    AgentEntity agent =
            agentRepository
                    .findByAgentIdAndStatus(
                            agentId,
                            "ACTIVE")
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Active agent not found"));
Optional<AgentAssignmentEntity> existingAssignment =
        agentAssignmentRepository
                .findByServiceRequest_ServiceRequestIdAndAssignmentStatusIn(
                        serviceRequestId,
                        List.of(
                                "ASSIGNED",
                                "ACCEPTED",
                                "IN_PROGRESS"));

if (existingAssignment.isPresent()) {
    throw new IllegalStateException(
            "Service request already has an active assignment");
}
    AgentAssignmentEntity assignment =
            new AgentAssignmentEntity();

    assignment.setServiceRequest(
            request);

    assignment.setAgent(
            agent);

    assignment.setAssignmentStatus(
            "ASSIGNED");

    assignment.setAssignedAt(
            OffsetDateTime.now());

    agentAssignmentRepository.save(
            assignment);

    String oldStatus =
            request.getStatus();

    request.setStatus(
            ServiceRequestStatus.ASSIGNED.name());

    serviceRequestRepository.save(
            request);

    createStatusHistory(
            request,
            oldStatus,
            ServiceRequestStatus.ASSIGNED.name(),
            "Agent assigned");

    return buildResponse(
            request);
}

@Override
public ServiceRequestResponse acceptRequest(
        UUID serviceRequestId,
        String reason) {

    ServiceRequestEntity request =
            serviceRequestRepository
                    .findById(serviceRequestId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Service request not found"));
validateTransition(
        request.getStatus(),
        ServiceRequestStatus.ACCEPTED.name());
    AgentAssignmentEntity assignment =
            agentAssignmentRepository
                    .findByServiceRequest_ServiceRequestIdAndAssignmentStatus(
                            serviceRequestId,
                            "ASSIGNED")
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "No active assignment found"));

    assignment.setAssignmentStatus(
            "ACCEPTED");

    assignment.setRespondedAt(
            OffsetDateTime.now());

    agentAssignmentRepository.save(
            assignment);

    String oldStatus =
            request.getStatus();

    request.setStatus(
            ServiceRequestStatus.ACCEPTED.name());

    serviceRequestRepository.save(
            request);

    createStatusHistory(
            request,
            oldStatus,
            ServiceRequestStatus.ACCEPTED.name(),
            reason);

    return buildResponse(
            request);
}

@Override
public ServiceRequestResponse startRequest(
        UUID serviceRequestId,
        String reason) {

    ServiceRequestEntity request =
            serviceRequestRepository
                    .findById(serviceRequestId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Service request not found"));
validateTransition(
        request.getStatus(),
        ServiceRequestStatus.ASSIGNED.name());
    AgentAssignmentEntity assignment =
            agentAssignmentRepository
                    .findByServiceRequest_ServiceRequestIdAndAssignmentStatus(
                            serviceRequestId,
                            "ACCEPTED")
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Accepted assignment not found"));

    assignment.setAssignmentStatus(
            "IN_PROGRESS");

    agentAssignmentRepository.save(
            assignment);

    String oldStatus =
            request.getStatus();

    request.setStatus(
            ServiceRequestStatus.IN_PROGRESS.name());

    serviceRequestRepository.save(
            request);

    createStatusHistory(
            request,
            oldStatus,
            ServiceRequestStatus.IN_PROGRESS.name(),
            reason);

    return buildResponse(
            request);
}

private ServiceRequestResponse
changeStatus(
        UUID serviceRequestId,
        String targetStatus,
        String reason) {

    ServiceRequestEntity entity =
            serviceRequestRepository
                    .findById(serviceRequestId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Service request not found"));

    String currentStatus =
            entity.getStatus();

    validateTransition(
            currentStatus,
            targetStatus);

    entity.setStatus(
            targetStatus);

    if (ServiceRequestStatus.COMPLETED.name().equals(targetStatus)) {
        entity.setCompletedAt(
                java.time.Instant.now());
    }

    createStatusHistory(
            entity,
            currentStatus,
            targetStatus,
            reason);

    return buildResponse(
            serviceRequestRepository.save(entity));
}

    private ServiceRequestResponse buildResponse(
        ServiceRequestEntity entity) {

    ServiceRequestResponse response =
            new ServiceRequestResponse();

    response.setServiceRequestId(
            entity.getServiceRequestId());

    response.setCustomerId(
            entity.getCustomer() != null
                    ? entity.getCustomer().getCustomerId()
                    : null);

    response.setPropertyId(
            entity.getProperty() != null
                    ? entity.getProperty().getPropertyId()
                    : null);

    response.setServiceId(
            entity.getService() != null
                    ? entity.getService().getServiceId()
                    : null);

    response.setRequestType(
            entity.getRequestType());

    response.setPriority(
            entity.getPriority());

    response.setStatus(
            entity.getStatus());

    response.setAmount(
            entity.getAmount());

    response.setDescription(
            entity.getDescription());

    response.setRequestedAt(
            entity.getRequestedAt());

    response.setScheduledAt(
            entity.getScheduledAt());

    response.setCompletedAt(
            entity.getCompletedAt());

    response.setAssignedTo(
            entity.getAssignedTo());

    return response;
}
    private void createStatusHistory(
        ServiceRequestEntity serviceRequest,
        String previousStatus,
        String newStatus,
        String reason) {

    ServiceRequestStatusHistoryEntity history =
            new ServiceRequestStatusHistoryEntity();

    history.setServiceRequest(
            serviceRequest);

    history.setPreviousStatus(
            previousStatus);

    history.setNewStatus(
            newStatus);

    history.setChangeReason(
            reason);

    history.setChangedAt(
            java.time.Instant.now());

    statusHistoryRepository.save(
            history);
}
}