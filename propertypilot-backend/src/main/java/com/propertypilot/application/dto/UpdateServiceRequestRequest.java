package com.propertypilot.application.dto;

import java.time.Instant;
import java.util.UUID;

public class UpdateServiceRequestRequest {

    private String status;

    private Instant scheduledAt;

    private Instant completedAt;

    private UUID assignedTo;

    private UUID assignedBy;

    private String cancellationReasonCode;

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status) {
        this.status = status;
    }

    public Instant getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(
            Instant scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(
            Instant completedAt) {
        this.completedAt = completedAt;
    }

    public UUID getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(
            UUID assignedTo) {
        this.assignedTo = assignedTo;
    }

    public UUID getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(
            UUID assignedBy) {
        this.assignedBy = assignedBy;
    }

    public String getCancellationReasonCode() {
        return cancellationReasonCode;
    }

    public void setCancellationReasonCode(
            String cancellationReasonCode) {
        this.cancellationReasonCode =
                cancellationReasonCode;
    }
}