package com.propertypilot.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(
        description =
                "Request used to create a task"
)
public class CreateTaskRequest {

    @Schema(
            description =
                    "Associated service request id"
    )
    private UUID serviceRequestId;

    @Schema(
            description =
                    "Assigned user id"
    )
    private UUID assigneeUserId;

    @Schema(
            description =
                    "Task name"
    )
    private String taskName;

    @Schema(
            description =
                    "Task due date/time"
    )
    private OffsetDateTime dueAt;

    public UUID getServiceRequestId() {
        return serviceRequestId;
    }

    public void setServiceRequestId(UUID serviceRequestId) {
        this.serviceRequestId = serviceRequestId;
    }

    public UUID getAssigneeUserId() {
        return assigneeUserId;
    }

    public void setAssigneeUserId(UUID assigneeUserId) {
        this.assigneeUserId = assigneeUserId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public OffsetDateTime getDueAt() {
        return dueAt;
    }

    public void setDueAt(OffsetDateTime dueAt) {
        this.dueAt = dueAt;
    }
}