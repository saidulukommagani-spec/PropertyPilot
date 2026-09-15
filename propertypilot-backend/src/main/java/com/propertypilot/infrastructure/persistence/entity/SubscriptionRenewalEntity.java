package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "subscription_renewals")
public class SubscriptionRenewalEntity
        extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "subscription_renewal_id")
    private UUID subscriptionRenewalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "customer_subscription_id",
            nullable = false
    )
    private CustomerSubscriptionEntity customerSubscription;

    @Column(name = "payment_id")
    private UUID paymentId;

    @Column(
            name = "renewal_date",
            nullable = false
    )
    private LocalDate renewalDate;

    @Column(
            name = "period_start",
            nullable = false
    )
    private LocalDate periodStart;

    @Column(
            name = "period_end",
            nullable = false
    )
    private LocalDate periodEnd;

    @Column(
            name = "status",
            nullable = false
    )
    private String status;

    @Version
    @Column(name = "version")
    private Long version;

    // Generate getters/setters
}