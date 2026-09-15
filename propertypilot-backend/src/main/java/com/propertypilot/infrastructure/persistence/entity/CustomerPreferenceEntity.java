package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "customer_preferences",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "customer_preferences_customer_id_key",
                        columnNames = "customer_id"
                )
        }
)
@Getter
@Setter
public class CustomerPreferenceEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "customer_preference_id")
    private UUID customerPreferenceId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @Column(name = "preferred_language", nullable = false, length = 10)
    private String preferredLanguage;

    @Column(name = "preferred_timezone", nullable = false, length = 64)
    private String preferredTimezone;

    @Column(
            name = "communication_channels",
            nullable = false,
            columnDefinition = "jsonb"
    )
    private String communicationChannels;

    @Column(
            name = "preference_data",
            nullable = false,
            columnDefinition = "jsonb"
    )
    private String preferenceData;

    @Column(name = "marketing_consent", nullable = false)
    private Boolean marketingConsent;
}