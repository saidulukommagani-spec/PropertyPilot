package com.propertypilot.application.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record EvidenceValidationHistoryResponse(UUID historyId, String status, String comment,
                                                 String userName, OffsetDateTime occurredAt) { }
