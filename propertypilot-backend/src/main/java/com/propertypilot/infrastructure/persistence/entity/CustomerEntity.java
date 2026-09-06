package com.propertypilot.infrastructure.persistence.entity;

import com.propertypilot.domain.enums.CustomerStatus;
import com.propertypilot.domain.enums.CustomerType;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "customers")
public class CustomerEntity extends AuditableEntity {

    @Id
    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", nullable = false)
    private CustomerType customerType;

    @Column(name = "preferred_location")
    private String preferredLocation;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CustomerStatus status;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private UserEntity user;

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(
            CustomerType customerType) {

        this.customerType = customerType;
    }

    public String getPreferredLocation() {
        return preferredLocation;
    }

    public void setPreferredLocation(
            String preferredLocation) {

        this.preferredLocation = preferredLocation;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public void setStatus(
            CustomerStatus status) {

        this.status = status;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(
            UserEntity user) {

        this.user = user;
    }
}