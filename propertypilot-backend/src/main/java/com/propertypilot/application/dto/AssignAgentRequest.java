package com.propertypilot.application.dto;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;

public class AssignAgentRequest {

    @NotNull(message = "Agent is required")
    private UUID agentId;

    public UUID getAgentId() {
        return agentId;
    }

    public void setAgentId(UUID agentId) {
        this.agentId = agentId;
    }
}
