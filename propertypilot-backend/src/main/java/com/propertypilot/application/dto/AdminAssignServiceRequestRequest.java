package com.propertypilot.application.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class AdminAssignServiceRequestRequest {

    @NotNull(message = "Assigned user is required")
    private UUID assignedTo;

    public UUID getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(UUID assignedTo) {
        this.assignedTo = assignedTo;
    }
}