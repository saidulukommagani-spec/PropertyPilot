package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "deal_service_request_links")
public class DealServiceRequestLinkEntity
        extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "deal_service_request_link_id")
    private UUID dealServiceRequestLinkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "deal_id",
            nullable = false)
    private DealEntity deal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "service_request_id",
            nullable = false)
    private ServiceRequestEntity serviceRequest;

    @Column(name = "link_reason")
    private String linkReason;

    @Column(name = "linked_at")
    private OffsetDateTime linkedAt;

    @Version
    @Column(name = "version")
    private Long version;

    public UUID getDealServiceRequestLinkId() {
        return dealServiceRequestLinkId;
    }

    public void setDealServiceRequestLinkId(
            UUID dealServiceRequestLinkId) {
        this.dealServiceRequestLinkId =
                dealServiceRequestLinkId;
    }

    public DealEntity getDeal() {
        return deal;
    }

    public void setDeal(
            DealEntity deal) {
        this.deal = deal;
    }

    public ServiceRequestEntity getServiceRequest() {
        return serviceRequest;
    }

    public void setServiceRequest(
            ServiceRequestEntity serviceRequest) {
        this.serviceRequest =
                serviceRequest;
    }

    public String getLinkReason() {
        return linkReason;
    }

    public void setLinkReason(
            String linkReason) {
        this.linkReason =
                linkReason;
    }

    public OffsetDateTime getLinkedAt() {
        return linkedAt;
    }

    public void setLinkedAt(
            OffsetDateTime linkedAt) {
        this.linkedAt =
                linkedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(
            Long version) {
        this.version =
                version;
    }
}