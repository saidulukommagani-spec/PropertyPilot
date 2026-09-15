package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "service_assignments")
public class ServiceAssignmentEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "service_assignment_id",
            nullable = false,
            updatable = false
    )
    private UUID serviceAssignmentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "service_request_id",
            nullable = false
    )
    private ServiceRequestEntity serviceRequest;

    @Column(
            name = "assignee_type",
            nullable = false,
            length = 20
    )
    private String assigneeType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private AgentEntity agent;

    @Column(name = "vendor_id")
    private UUID vendorId;

    @Column(
            name = "assigned_at",
            nullable = false
    )
    private OffsetDateTime assignedAt;

    @Column(name = "unassigned_at")
    private OffsetDateTime unassignedAt;

    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private String status;

    @Column(name = "assignment_reason")
    private String assignmentReason;

    @Column(name = "scheduled_start")
    private OffsetDateTime scheduledStart;

    @Column(name = "scheduled_end")
    private OffsetDateTime scheduledEnd;

    @Column(name = "accepted_at")
    private OffsetDateTime acceptedAt;

    @Version
    @Column(
            name = "version",
            nullable = false
    )
    private Long version = 0L;

    public UUID getServiceAssignmentId() {
        return serviceAssignmentId;
    }

    public void setServiceAssignmentId(UUID serviceAssignmentId) {
        this.serviceAssignmentId = serviceAssignmentId;
    }

    public ServiceRequestEntity getServiceRequest() {
        return serviceRequest;
    }

    public void setServiceRequest(ServiceRequestEntity serviceRequest) {
        this.serviceRequest = serviceRequest;
    }

    public String getAssigneeType() {
        return assigneeType;
    }

    public void setAssigneeType(String assigneeType) {
        this.assigneeType = assigneeType;
    }

    public AgentEntity getAgent() {
        return agent;
    }

    public void setAgent(AgentEntity agent) {
        this.agent = agent;
    }

    public UUID getVendorId() {
        return vendorId;
    }

    public void setVendorId(UUID vendorId) {
        this.vendorId = vendorId;
    }

    public OffsetDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(OffsetDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public OffsetDateTime getUnassignedAt() {
        return unassignedAt;
    }

    public void setUnassignedAt(OffsetDateTime unassignedAt) {
        this.unassignedAt = unassignedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssignmentReason() {
        return assignmentReason;
    }

    public void setAssignmentReason(String assignmentReason) {
        this.assignmentReason = assignmentReason;
    }

    public OffsetDateTime getScheduledStart() {
        return scheduledStart;
    }

    public void setScheduledStart(OffsetDateTime scheduledStart) {
        this.scheduledStart = scheduledStart;
    }

    public OffsetDateTime getScheduledEnd() {
        return scheduledEnd;
    }

    public void setScheduledEnd(OffsetDateTime scheduledEnd) {
        this.scheduledEnd = scheduledEnd;
    }

    public OffsetDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(OffsetDateTime acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}