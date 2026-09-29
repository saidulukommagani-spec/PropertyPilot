package com.propertypilot.infrastructure.persistence.entity;

import com.propertypilot.domain.enums.MilestoneStatus;
import com.propertypilot.domain.enums.MilestoneType;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "deal_milestones")
public class DealMilestoneEntity
        extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "deal_milestone_id")
    private UUID dealMilestoneId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "deal_id",
            nullable = false)
    private DealEntity deal;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "milestone_type",
            nullable = false)
    private MilestoneType milestoneType;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "milestone_status",
            nullable = false)
    private MilestoneStatus milestoneStatus;

    @Column(name = "planned_date")
    private OffsetDateTime plannedDate;

    @Column(name = "completed_date")
    private OffsetDateTime completedDate;

    @Column(name = "remarks")
    private String remarks;

    @Version
    @Column(name = "version")
    private Long version;

    public UUID getDealMilestoneId() {
        return dealMilestoneId;
    }

    public void setDealMilestoneId(
            UUID dealMilestoneId) {
        this.dealMilestoneId =
                dealMilestoneId;
    }

    public DealEntity getDeal() {
        return deal;
    }

    public void setDeal(
            DealEntity deal) {
        this.deal = deal;
    }

    public MilestoneType getMilestoneType() {
        return milestoneType;
    }

    public void setMilestoneType(
            MilestoneType milestoneType) {
        this.milestoneType =
                milestoneType;
    }

    public MilestoneStatus getMilestoneStatus() {
        return milestoneStatus;
    }

    public void setMilestoneStatus(
            MilestoneStatus milestoneStatus) {
        this.milestoneStatus =
                milestoneStatus;
    }

    public OffsetDateTime getPlannedDate() {
        return plannedDate;
    }

    public void setPlannedDate(
            OffsetDateTime plannedDate) {
        this.plannedDate =
                plannedDate;
    }

    public OffsetDateTime getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(
            OffsetDateTime completedDate) {
        this.completedDate =
                completedDate;
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