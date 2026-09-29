package com.propertypilot.infrastructure.persistence.entity;

import com.propertypilot.domain.enums.ContactVisibility;
import com.propertypilot.domain.enums.ParticipantRole;
import com.propertypilot.domain.enums.ParticipantStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "deal_participants")
public class DealParticipantEntity
        extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "deal_participant_id")
    private UUID dealParticipantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "deal_id",
            nullable = false)
    private DealEntity deal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "customer_id",
            nullable = false)
    private CustomerEntity customer;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "participant_role",
            nullable = false)
    private ParticipantRole participantRole;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "participant_status",
            nullable = false)
    private ParticipantStatus participantStatus;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "contact_visibility",
            nullable = false)
    private ContactVisibility contactVisibility;

    @Column(name = "offered_amount")
    private BigDecimal offeredAmount;

    @Column(name = "offer_date")
    private OffsetDateTime offerDate;

    @Column(name = "selected_flag")
    private Boolean selectedFlag;

    @Column(name = "rejected_reason")
    private String rejectedReason;

    @Column(name = "remarks")
    private String remarks;

    @Version
    @Column(name = "version")
    private Long version;

    public UUID getDealParticipantId() {
        return dealParticipantId;
    }

    public void setDealParticipantId(
            UUID dealParticipantId) {
        this.dealParticipantId =
                dealParticipantId;
    }

    public DealEntity getDeal() {
        return deal;
    }

    public void setDeal(
            DealEntity deal) {
        this.deal = deal;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(
            CustomerEntity customer) {
        this.customer = customer;
    }

    public ParticipantRole getParticipantRole() {
        return participantRole;
    }

    public void setParticipantRole(
            ParticipantRole participantRole) {
        this.participantRole =
                participantRole;
    }

    public ParticipantStatus getParticipantStatus() {
        return participantStatus;
    }

    public void setParticipantStatus(
            ParticipantStatus participantStatus) {
        this.participantStatus =
                participantStatus;
    }

    public ContactVisibility getContactVisibility() {
        return contactVisibility;
    }

    public void setContactVisibility(
            ContactVisibility contactVisibility) {
        this.contactVisibility =
                contactVisibility;
    }

    public BigDecimal getOfferedAmount() {
        return offeredAmount;
    }

    public void setOfferedAmount(
            BigDecimal offeredAmount) {
        this.offeredAmount =
                offeredAmount;
    }

    public OffsetDateTime getOfferDate() {
        return offerDate;
    }

    public void setOfferDate(
            OffsetDateTime offerDate) {
        this.offerDate =
                offerDate;
    }

    public Boolean getSelectedFlag() {
        return selectedFlag;
    }

    public void setSelectedFlag(
            Boolean selectedFlag) {
        this.selectedFlag =
                selectedFlag;
    }

    public String getRejectedReason() {
        return rejectedReason;
    }

    public void setRejectedReason(
            String rejectedReason) {
        this.rejectedReason =
                rejectedReason;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(
            String remarks) {
        this.remarks = remarks;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(
            Long version) {
        this.version = version;
    }
}