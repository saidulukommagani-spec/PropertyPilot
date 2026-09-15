package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "complaints",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "complaints_complaint_number_key",
                        columnNames = "complaint_number"
                )
        }
)
@Getter
@Setter
public class ComplaintEntity extends AuditableEntity {

    @Id
    @Column(name = "complaint_id", nullable = false)
    private UUID complaintId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_request_id")
    private ServiceRequestEntity serviceRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private UserEntity assignedTo;

    @Column(name = "complaint_number", nullable = false, length = 80)
    private String complaintNumber;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "subject", nullable = false, length = 200)
    private String subject;

    @Column(name = "description", nullable = false, columnDefinition = "text")
    private String description;

    @Column(name = "priority", nullable = false, length = 20)
    private String priority;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "resolution", columnDefinition = "text")
    private String resolution;

    @Column(name = "resolved_at")
    private OffsetDateTime resolvedAt;
}