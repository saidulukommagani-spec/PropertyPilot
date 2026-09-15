package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "partner_services",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_partner_services",
                        columnNames = {"partner_id", "service_id"}
                )
        }
)
public class PartnerServiceEntity extends AuditableEntity {

    @Id
    @Column(name = "partner_service_id")
    private UUID partnerServiceId;

    @Column(name = "partner_id", nullable = false)
    private UUID partnerId;

    @Column(name = "service_id", nullable = false)
    private UUID serviceId;

    @Column(name = "status", nullable = false, length = 30)
    private String status;
}