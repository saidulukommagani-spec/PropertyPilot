package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreateVisitRequest;
import com.propertypilot.application.dto.VisitResponse;

import java.util.UUID;

public interface VisitService {

    VisitResponse createVisit(
            CreateVisitRequest request);

    VisitResponse getVisit(
            UUID visitId);

    VisitResponse startVisit(
            UUID visitId);

    VisitResponse completeVisit(
            UUID visitId);

    VisitResponse cancelVisit(
            UUID visitId,
            String reason);
}