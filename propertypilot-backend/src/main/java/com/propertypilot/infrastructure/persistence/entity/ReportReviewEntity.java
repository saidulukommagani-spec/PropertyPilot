package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "report_reviews",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_report_reviewer",
                        columnNames = {
                                "report_id",
                                "reviewer_user_id"
                        }
                )
        }
)
public class ReportReviewEntity extends AuditableEntity {

    @Id
    @Column(name = "report_review_id")
    private UUID reportReviewId;

    @Column(name = "report_id", nullable = false)
    private UUID reportId;

    @Column(name = "reviewer_user_id", nullable = false)
    private UUID reviewerUserId;

    @Column(name = "review_status", nullable = false, length = 20)
    private String reviewStatus;

    @Column(name = "comments")
    private String comments;

    @Column(name = "reviewed_at")
    private OffsetDateTime reviewedAt;
}