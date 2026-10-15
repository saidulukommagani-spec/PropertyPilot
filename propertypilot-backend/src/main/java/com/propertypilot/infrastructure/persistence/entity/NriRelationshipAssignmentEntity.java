package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "nri_relationship_assignments")
@Getter
@Setter
@NoArgsConstructor
@Builder
public class NriRelationshipAssignmentEntity extends AuditableEntity {

    @Id
    @Column(name = "nri_relationship_assignment_id")
    private UUID nriRelationshipAssignmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "relationship_manager_user_id",
            nullable = false)
    private UserEntity relationshipManager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private Property property;

    @Column(name = "assigned_at", nullable = false)
    private OffsetDateTime assignedAt;

    @Column(name = "unassigned_at")
    private OffsetDateTime unassignedAt;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "assignment_reason")
    private String assignmentReason;

    public NriRelationshipAssignmentEntity(
            UUID nriRelationshipAssignmentId,
            CustomerEntity customer,
            UserEntity relationshipManager,
            Property property,
            OffsetDateTime assignedAt,
            OffsetDateTime unassignedAt,
            String status,
            String assignmentReason) {
        this.nriRelationshipAssignmentId = nriRelationshipAssignmentId;
        this.customer = customer;
        this.relationshipManager = relationshipManager;
        this.property = property;
        this.assignedAt = assignedAt;
        this.unassignedAt = unassignedAt;
        this.status = status;
        this.assignmentReason = assignmentReason;
    }
}
