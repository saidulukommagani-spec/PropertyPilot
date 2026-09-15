package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "customer_addresses",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_customer_primary_address",
                        columnNames = {
                                "customer_id",
                                "address_type"
                        }
                )
        }
)
@Getter
@Setter
public class CustomerAddressEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "customer_address_id")
    private UUID customerAddressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locality_id")
    private LocalityEntity locality;

    @Column(name = "address_type", nullable = false, length = 20)
    private String addressType;

    @Column(name = "address_line1", nullable = false, length = 255)
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @Column(name = "city", nullable = false, length = 120)
    private String city;

    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;

    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary;

    @Column(name = "status", nullable = false, length = 20)
    private String status;
}