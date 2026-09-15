package com.propertypilot.application.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class TaskResponse {

    private UUID taskId;

    private UUID serviceRequestId;

    private UUID assigneeUserId;

    private String taskName;

    private String taskStatus;

    private OffsetDateTime dueAt;

    private OffsetDateTime completedAt;

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

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

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public OffsetDateTime getDueAt() {
        return dueAt;
    }

    public void setDueAt(OffsetDateTime dueAt) {
        this.dueAt = dueAt;
    }

    public OffsetDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(OffsetDateTime completedAt) {
        this.completedAt = completedAt;
    }
}