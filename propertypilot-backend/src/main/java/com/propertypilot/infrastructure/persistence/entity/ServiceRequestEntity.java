package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "service_requests")
public class ServiceRequestEntity
        extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "service_request_id")
    private UUID serviceRequestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "customer_id",
            nullable = false
    )
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "property_id",
            nullable = false
    )
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "service_id"
    )
    private ServiceEntity service;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pricing_estimate_id")
    private PricingEstimateEntity pricingEstimate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id")
    private LeadEntity lead;

    @Column(
            name = "request_type",
            nullable = false,
            length = 100
    )
    private String requestType;

    @Column(
            name = "priority",
            nullable = false,
            length = 30
    )
    private String priority;

    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private String status;

    @Column(name = "coverage_zone_id")
    private UUID coverageZoneId;

    @Column(name = "cluster_id")
    private UUID clusterId;

    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    @Column(name = "sla_due_at")
    private Instant slaDueAt;

    @Column(name = "sla_policy_id_snapshot")
    private UUID slaPolicyIdSnapshot;

    @Column(name = "sla_snapshot_captured", nullable = false)
    private boolean slaSnapshotCaptured;

    @Column(name = "scheduled_at")
    private Instant scheduledAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "cancellation_reason_code")
    private String cancellationReasonCode;

    @Column(name = "assigned_to")
    private UUID assignedTo;

    @Column(name = "assigned_at")
    private Instant assignedAt;

    @Column(name = "assigned_by")
    private UUID assignedBy;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    public UUID getServiceRequestId() {
        return serviceRequestId;
    }

    public void setServiceRequestId(UUID serviceRequestId) {
        this.serviceRequestId = serviceRequestId;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public ServiceEntity getService() {
        return service;
    }

    public void setService(ServiceEntity service) {
        this.service = service;
    }

    public PricingEstimateEntity getPricingEstimate() { return pricingEstimate; }
    public void setPricingEstimate(PricingEstimateEntity pricingEstimate) { this.pricingEstimate = pricingEstimate; }
    public LeadEntity getLead() { return lead; }
    public void setLead(LeadEntity lead) { this.lead = lead; }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UUID getCoverageZoneId() {
        return coverageZoneId;
    }

    public void setCoverageZoneId(UUID coverageZoneId) {
        this.coverageZoneId = coverageZoneId;
    }

    public UUID getClusterId() {
        return clusterId;
    }

    public void setClusterId(UUID clusterId) {
        this.clusterId = clusterId;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Instant requestedAt) {
        this.requestedAt = requestedAt;
    }

    public Instant getSlaDueAt() { return slaDueAt; }
    public void setSlaDueAt(Instant slaDueAt) { this.slaDueAt = slaDueAt; }
    public UUID getSlaPolicyIdSnapshot() { return slaPolicyIdSnapshot; }
    public void setSlaPolicyIdSnapshot(UUID slaPolicyIdSnapshot) { this.slaPolicyIdSnapshot = slaPolicyIdSnapshot; }
    public boolean isSlaSnapshotCaptured() { return slaSnapshotCaptured; }
    public void setSlaSnapshotCaptured(boolean slaSnapshotCaptured) { this.slaSnapshotCaptured = slaSnapshotCaptured; }

    public Instant getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(Instant scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCancellationReasonCode() {
        return cancellationReasonCode;
    }

    public void setCancellationReasonCode(String cancellationReasonCode) {
        this.cancellationReasonCode = cancellationReasonCode;
    }

    public UUID getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(UUID assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Instant getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Instant assignedAt) {
        this.assignedAt = assignedAt;
    }

    public UUID getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(UUID assignedBy) {
        this.assignedBy = assignedBy;
    }
}
