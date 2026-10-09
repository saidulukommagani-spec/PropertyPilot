package com.propertypilot.application.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class AssignServiceRequestAgentRequest {
    @NotNull
    private UUID agentId;
    private String notes;

    public UUID getAgentId() { return agentId; }
    public void setAgentId(UUID agentId) { this.agentId = agentId; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
