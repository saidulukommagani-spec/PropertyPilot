package com.propertypilot.application.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class CreateVisitRequest {

    private UUID assignmentId;

    private OffsetDateTime scheduledAt;

    private String notes;

    public UUID getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(UUID assignmentId) {
        this.assignmentId = assignmentId;
    }

    public OffsetDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(OffsetDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}