package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "service_ratings")
public class ServiceRatingEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "service_rating_id")
    private UUID serviceRatingId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_request_id", nullable = false)
    private ServiceRequestEntity serviceRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @Column(name = "rating", nullable = false)
    private Short rating;

    @Column(name = "review_text")
    private String reviewText;

    @Column(name = "moderation_status", nullable = false, length = 20)
    private String moderationStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderated_by")
    private UserEntity moderatedBy;

    @Column(name = "moderated_at")
    private OffsetDateTime moderatedAt;

    /**
     * DB Constraint:
     * agent_id and vendor_id cannot both be populated.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private AgentEntity agent;

    /**
     * DB Constraint:
     * agent_id and vendor_id cannot both be populated.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private VendorEntity vendor;

    @Column(name = "submitted_at", nullable = false)
    private OffsetDateTime submittedAt;

    @Version
    @Column(name = "version")
    private Long version;

    public UUID getServiceRatingId() {
        return serviceRatingId;
    }

    public void setServiceRatingId(UUID serviceRatingId) {
        this.serviceRatingId = serviceRatingId;
    }

    public ServiceRequestEntity getServiceRequest() {
        return serviceRequest;
    }

    public void setServiceRequest(ServiceRequestEntity serviceRequest) {
        this.serviceRequest = serviceRequest;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public Short getRating() {
        return rating;
    }

    public void setRating(Short rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getModerationStatus() {
        return moderationStatus;
    }

    public void setModerationStatus(String moderationStatus) {
        this.moderationStatus = moderationStatus;
    }

    public UserEntity getModeratedBy() {
        return moderatedBy;
    }

    public void setModeratedBy(UserEntity moderatedBy) {
        this.moderatedBy = moderatedBy;
    }

    public OffsetDateTime getModeratedAt() {
        return moderatedAt;
    }

    public void setModeratedAt(OffsetDateTime moderatedAt) {
        this.moderatedAt = moderatedAt;
    }

    public AgentEntity getAgent() {
        return agent;
    }

    public void setAgent(AgentEntity agent) {
        this.agent = agent;
    }

    public VendorEntity getVendor() {
        return vendor;
    }

    public void setVendor(VendorEntity vendor) {
        this.vendor = vendor;
    }

    public OffsetDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(OffsetDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}