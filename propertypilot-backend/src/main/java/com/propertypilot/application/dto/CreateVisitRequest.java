package com.propertypilot.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.UUID;

public class CreateVisitRequest {

    @NotNull(message = "Assignment Id is required")
    private UUID assignmentId;

    @NotNull(message = "Scheduled date is required")
    private OffsetDateTime scheduledAt;

    @Size(
            max = 1000,
            message = "Notes cannot exceed 1000 characters")
    private String notes;

    public UUID getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(
            UUID assignmentId) {

        this.assignmentId = assignmentId;
    }

    public OffsetDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(
            OffsetDateTime scheduledAt) {

        this.scheduledAt = scheduledAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(
            String notes) {

        this.notes = notes;
    }
}