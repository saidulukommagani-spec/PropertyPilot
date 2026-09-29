package com.propertypilot.application.service;

import com.propertypilot.application.dto.ServiceRequestResponse;

import java.util.UUID;

public interface ServiceRequestWorkflowService {

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
}