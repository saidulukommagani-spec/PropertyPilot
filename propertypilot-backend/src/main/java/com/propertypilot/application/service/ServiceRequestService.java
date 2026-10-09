package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreateServiceRequestRequest;
import com.propertypilot.application.dto.ServiceRequestResponse;
import com.propertypilot.application.dto.ServiceRequestAssignmentHistoryResponse;
import com.propertypilot.application.dto.UpdateServiceRequestRequest;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.Property;
import com.propertypilot.infrastructure.persistence.entity.ServiceEntity;

import java.util.List;
import java.util.UUID;


public interface ServiceRequestService {

    ServiceRequestResponse createRequest(
            CreateServiceRequestRequest request);

    /** Internal subscription-entitlement path used by MonitoringRunService only. */
    ServiceRequestResponse createEntitledMonitoringRequest(
            CustomerEntity customer, Property property, ServiceEntity service, String reason);

    ServiceRequestResponse getRequest(
            UUID serviceRequestId);

    List<ServiceRequestResponse> getAllRequestsForOperations();

    List<ServiceRequestResponse> getRequestsForCurrentAgent();

    ServiceRequestResponse getRequestForOperations(UUID serviceRequestId);

    List<ServiceRequestResponse> getCustomerRequests(
            UUID customerId);

    ServiceRequestResponse updateRequest(
            UUID serviceRequestId,
            UpdateServiceRequestRequest request);

            ServiceRequestResponse assignRequest(
        UUID serviceRequestId,
        UUID assignedTo,
        UUID assignedBy,
        String reason);

ServiceRequestResponse acceptRequest(
        UUID serviceRequestId,
        String reason);

ServiceRequestResponse rejectAssignment(UUID serviceRequestId, String reason);

ServiceRequestResponse startRequest(
        UUID serviceRequestId,
        String reason);

ServiceRequestResponse completeRequest(
        UUID serviceRequestId,
        String reason);

void completeRequestIfReady(UUID serviceRequestId, String reason);

ServiceRequestResponse cancelRequest(
        UUID serviceRequestId,
        String reason);
        ServiceRequestResponse assignAgent(
        UUID serviceRequestId,
        UUID agentId);

    ServiceRequestResponse assignAgentWithNotes(UUID serviceRequestId, UUID agentId, String notes);

    List<ServiceRequestAssignmentHistoryResponse> getAssignmentHistory(UUID serviceRequestId);
}
