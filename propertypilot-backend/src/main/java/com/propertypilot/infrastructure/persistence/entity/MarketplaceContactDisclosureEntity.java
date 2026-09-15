package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "marketplace_contact_disclosures",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_inquiry_disclosure",
                        columnNames = {
                                "marketplace_inquiry_id",
                                "disclosure_scope"
                        }
                )
        }
)
@Getter
@Setter
public class MarketplaceContactDisclosureEntity extends AuditableEntity {

    @Id
    @Column(name = "contact_disclosure_id")
    private UUID contactDisclosureId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marketplace_inquiry_id", nullable = false)
    private MarketplaceInquiryEntity marketplaceInquiry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disclosed_by", nullable = false)
    private UserEntity disclosedBy;

    @Column(name = "disclosure_scope", nullable = false, length = 20)
    private String disclosureScope;

    @Column(name = "consent_reference", nullable = false, length = 255)
    private String consentReference;

    @Column(name = "disclosed_at", nullable = false)
    private OffsetDateTime disclosedAt;
}