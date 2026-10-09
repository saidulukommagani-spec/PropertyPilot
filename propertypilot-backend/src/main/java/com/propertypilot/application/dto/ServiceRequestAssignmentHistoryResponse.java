package com.propertypilot.application.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ServiceRequestAssignmentHistoryResponse(
        UUID assignmentId,
        String action,
        UUID agentId,
        String agentName,
        String agentCode,
        String assignedByName,
        UUID assignedByUserId,
        String assignmentStatus,
        String notes,
        OffsetDateTime assignedAt,
        OffsetDateTime respondedAt) { }
