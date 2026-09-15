package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "tasks")
public class TaskEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "task_id",
            nullable = false,
            updatable = false
    )
    private UUID taskId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "service_request_id",
            nullable = false
    )
    private ServiceRequestEntity serviceRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_user_id")
    private UserEntity assigneeUser;

    @Column(
            name = "task_name",
            nullable = false,
            length = 150
    )
    private String taskName;

    @Column(
            name = "task_status",
            nullable = false,
            length = 30
    )
    private String taskStatus;

    @Column(name = "due_at")
    private OffsetDateTime dueAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Version
    @Column(
            name = "version",
            nullable = false
    )
    private Long version = 0L;

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public ServiceRequestEntity getServiceRequest() {
        return serviceRequest;
    }

    public void setServiceRequest(
            ServiceRequestEntity serviceRequest) {
        this.serviceRequest = serviceRequest;
    }

    public UserEntity getAssigneeUser() {
        return assigneeUser;
    }

    public void setAssigneeUser(
            UserEntity assigneeUser) {
        this.assigneeUser = assigneeUser;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(
            String taskName) {
        this.taskName = taskName;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(
            String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public OffsetDateTime getDueAt() {
        return dueAt;
    }

    public void setDueAt(
            OffsetDateTime dueAt) {
        this.dueAt = dueAt;
    }

    public OffsetDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(
            OffsetDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(
            Long version) {
        this.version = version;
    }
}