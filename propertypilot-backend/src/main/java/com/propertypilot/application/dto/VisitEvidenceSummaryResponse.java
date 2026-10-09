package com.propertypilot.application.dto;

import java.util.List;
import java.util.UUID;

public record VisitEvidenceSummaryResponse(UUID visitId, List<String> requiredEvidenceTypes,
                                           List<EvidenceResponse> uploadedEvidence,
                                           List<String> missingEvidenceTypes,
                                           int completionPercentage) { }
