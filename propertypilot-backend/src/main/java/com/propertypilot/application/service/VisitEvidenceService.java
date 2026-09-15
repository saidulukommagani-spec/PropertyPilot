package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreateVisitEvidenceRequest;
import com.propertypilot.application.dto.VisitEvidenceResponse;

import java.util.List;
import java.util.UUID;

public interface VisitEvidenceService {

    VisitEvidenceResponse uploadEvidence(
            CreateVisitEvidenceRequest request);

    List<VisitEvidenceResponse> getVisitEvidence(
            UUID visitId);
}