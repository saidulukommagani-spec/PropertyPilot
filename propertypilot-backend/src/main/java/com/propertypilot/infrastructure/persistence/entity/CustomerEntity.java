package com.propertypilot.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;
import com.propertypilot.domain.enums.CustomerType;
import com.propertypilot.domain.enums.CustomerStatus;

@Entity
@Table(name = "customers")
public class CustomerEntity extends AuditableEntity {

    @Id
    @Column(name = "customer_id")
    private UUID customerId;

    @Enumerated(EnumType.STRING)
@Column(name = "customer_type")
private CustomerType customerType;

    @Column(name = "preferred_location")
    private String preferredLocation;

  @Enumerated(EnumType.STRING)
@Column(name = "status")
private CustomerStatus status;

    @OneToOne
    @JoinColumn(name = "user_id")
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

public void setCustomerType(CustomerType customerType) {
    this.customerType = customerType;
}

    public String getPreferredLocation() {
        return preferredLocation;
    }

    public void setPreferredLocation(String preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

   public CustomerStatus getStatus() {
    return status;
}

public void setStatus(CustomerStatus status) {
    this.status = status;
}

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}