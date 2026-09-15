package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "customer_favorites",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_favorite_property",
                        columnNames = {
                                "customer_id",
                                "property_id"
                        }
                )
        }
)
@Getter
@Setter
public class CustomerFavoriteEntity extends AuditableEntity {

   @Id
@GeneratedValue(strategy = GenerationType.UUID)
@Column(name = "customer_favorite_id")
private UUID customerFavoriteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @Column(name = "favorite_type", nullable = false, length = 20)
    private String favoriteType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private Property property;

    @Column(name = "marketplace_listing_id")
    private UUID marketplaceListingId;
}