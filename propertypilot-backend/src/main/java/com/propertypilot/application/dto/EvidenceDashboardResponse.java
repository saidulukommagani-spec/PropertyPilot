package com.propertypilot.application.dto;

import java.util.Map;

public record EvidenceDashboardResponse(long totalEvidence, long pendingValidation, long approved,
                                        long rejected, long missingEvidence, long todaysUploads,
                                        Map<String, Long> byType, Map<String, Long> byService) { }
