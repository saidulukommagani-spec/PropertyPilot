package com.propertypilot.application.dto;

import java.util.UUID;

public record LeadAssignmentResponse(

        UUID leadId,

        UUID agentId,

        String agentCode,

        String status

) {
}