package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "customer_verifications",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_customer_verification",
                        columnNames = {
                                "customer_id",
                                "verification_type"
                        }
                )
        }
)
@Getter
@Setter
public class CustomerVerificationEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "customer_verification_id")
    private UUID customerVerificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private PropertyDocumentEntity document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private UserEntity verifiedBy;

    @Column(name = "verification_type", nullable = false, length = 30)
    private String verificationType;

    @Column(name = "reference_number_hash", length = 255)
    private String referenceNumberHash;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "provider_reference", length = 255)
    private String providerReference;

    @Column(name = "verified_at")
    private OffsetDateTime verifiedAt;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;
}