package com.propertypilot.application.dto;

import java.util.UUID;

public class AssignAgentRequest {

    private UUID agentId;

    public UUID getAgentId() {
        return agentId;
    }

    public void setAgentId(UUID agentId) {
        this.agentId = agentId;
    }
}