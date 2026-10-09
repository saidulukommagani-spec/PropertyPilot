package com.propertypilot.application.dto;

import java.util.UUID;
import java.util.List;

public record AgentDirectoryResponse(
        UUID agentId,
        String agentCode,
        String fullName,
        String phone,
        String status,
        List<String> skills,
        String availability,
        long activeAssignmentCount,
        String region) { }
