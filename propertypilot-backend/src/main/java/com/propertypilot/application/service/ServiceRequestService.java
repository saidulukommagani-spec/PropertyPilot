package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreateServiceRequestRequest;
import com.propertypilot.application.dto.ServiceRequestResponse;
import com.propertypilot.application.dto.UpdateServiceRequestRequest;

import java.util.List;
import java.util.UUID;

public interface ServiceRequestService {

    ServiceRequestResponse createRequest(
            CreateServiceRequestRequest request);

    ServiceRequestResponse getRequest(
            UUID serviceRequestId);

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

ServiceRequestResponse startRequest(
        UUID serviceRequestId,
        String reason);

ServiceRequestResponse completeRequest(
        UUID serviceRequestId,
        String reason);

ServiceRequestResponse cancelRequest(
        UUID serviceRequestId,
        String reason);
        ServiceRequestResponse assignAgent(
        UUID serviceRequestId,
        UUID agentId);
}