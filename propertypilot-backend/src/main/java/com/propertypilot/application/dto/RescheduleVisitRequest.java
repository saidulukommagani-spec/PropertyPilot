package com.propertypilot.application.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

public class RescheduleVisitRequest {
    @NotNull
    @Future
    private OffsetDateTime scheduledAt;

    @Size(max = 1000)
    private String notes;

    @NotNull
    @jakarta.validation.constraints.NotBlank
    @Size(max = 1000)
    private String reason;

    public OffsetDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(OffsetDateTime scheduledAt) { this.scheduledAt = scheduledAt; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
