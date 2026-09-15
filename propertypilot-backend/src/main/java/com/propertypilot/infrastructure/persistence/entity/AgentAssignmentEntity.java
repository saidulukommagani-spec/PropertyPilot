package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "agent_assignments")
public class AgentAssignmentEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            name = "assignment_id",
            nullable = false,
            updatable = false
    )
    private UUID assignmentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "service_request_id",
            nullable = false
    )
    private ServiceRequestEntity serviceRequest;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "agent_id",
            nullable = false
    )
    private AgentEntity agent;

    @Column(
            name = "assignment_status",
            nullable = false,
            length = 30
    )
    private String assignmentStatus;

    @Column(
            name = "distance_km",
            precision = 8,
            scale = 2
    )
    private BigDecimal distanceKm;

    @Column(name = "eta_minutes")
    private Integer etaMinutes;

    @Column(
            name = "assigned_at",
            nullable = false
    )
    private OffsetDateTime assignedAt;

    @Column(name = "responded_at")
    private OffsetDateTime respondedAt;

    @Column(name = "ended_at")
    private OffsetDateTime endedAt;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Version
    @Column(
            name = "version",
            nullable = false
    )
    private Long version = 0L;

    public UUID getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(UUID assignmentId) {
        this.assignmentId = assignmentId;
    }

    public ServiceRequestEntity getServiceRequest() {
        return serviceRequest;
    }

    public void setServiceRequest(ServiceRequestEntity serviceRequest) {
        this.serviceRequest = serviceRequest;
    }

    public AgentEntity getAgent() {
        return agent;
    }

    public void setAgent(AgentEntity agent) {
        this.agent = agent;
    }

    public String getAssignmentStatus() {
        return assignmentStatus;
    }

    public void setAssignmentStatus(String assignmentStatus) {
        this.assignmentStatus = assignmentStatus;
    }

    public BigDecimal getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(BigDecimal distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Integer getEtaMinutes() {
        return etaMinutes;
    }

    public void setEtaMinutes(Integer etaMinutes) {
        this.etaMinutes = etaMinutes;
    }

    public OffsetDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(OffsetDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public OffsetDateTime getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(OffsetDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }

    public OffsetDateTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(OffsetDateTime endedAt) {
        this.endedAt = endedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}