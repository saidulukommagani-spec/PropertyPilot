package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreateEvidenceRequest;
import com.propertypilot.application.dto.EvidenceResponse;

import java.util.UUID;

public interface EvidenceService {

    EvidenceResponse createEvidence(
            CreateEvidenceRequest request);

    EvidenceResponse getEvidence(
            UUID evidenceId);
}