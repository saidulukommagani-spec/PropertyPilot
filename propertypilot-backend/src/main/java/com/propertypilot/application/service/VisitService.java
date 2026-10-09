package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreateVisitRequest;
import com.propertypilot.application.dto.VisitResponse;
import com.propertypilot.application.dto.RescheduleVisitRequest;

import java.util.UUID;
import java.util.List;

public interface VisitService {

    VisitResponse createVisit(
            CreateVisitRequest request);

    VisitResponse getVisit(
            UUID visitId);

    List<VisitResponse> getVisitsForServiceRequest(UUID serviceRequestId);
    List<VisitResponse> getAllVisitsForOperations();
    List<VisitResponse> getVisitsForCurrentAgent();
    List<VisitResponse> getVisitsForCurrentCustomer();

    VisitResponse startVisit(
            UUID visitId);

    VisitResponse completeVisit(
            UUID visitId);

    VisitResponse cancelVisit(
            UUID visitId,
            String reason);

    VisitResponse rescheduleVisit(UUID visitId, RescheduleVisitRequest request);
}
