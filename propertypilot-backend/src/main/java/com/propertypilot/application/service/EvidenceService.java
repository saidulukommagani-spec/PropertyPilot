package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreateEvidenceRequest;
import com.propertypilot.application.dto.EvidenceResponse;
import com.propertypilot.application.dto.EvidenceTypeResponse;
import com.propertypilot.application.dto.EvidenceValidationRequest;
import com.propertypilot.application.dto.VisitEvidenceSummaryResponse;
import com.propertypilot.application.dto.EvidenceDashboardResponse;

import java.util.UUID;
import java.util.List;

public interface EvidenceService {

    EvidenceResponse createEvidence(
            CreateEvidenceRequest request);

    EvidenceResponse getEvidence(
            UUID evidenceId);

    List<EvidenceResponse> getEvidenceForServiceRequest(UUID serviceRequestId);
    List<EvidenceResponse> getAllEvidenceForOperations();
    List<EvidenceTypeResponse> getActiveEvidenceTypes();
    List<EvidenceResponse> getEvidenceForVisit(UUID visitId);
    List<EvidenceResponse> getValidationQueue();
    EvidenceResponse validateEvidence(UUID evidenceId, EvidenceValidationRequest request);
    VisitEvidenceSummaryResponse getVisitEvidenceSummary(UUID visitId);
    String getFileStorageKey(UUID evidenceId);
    EvidenceDashboardResponse getDashboard();
}
