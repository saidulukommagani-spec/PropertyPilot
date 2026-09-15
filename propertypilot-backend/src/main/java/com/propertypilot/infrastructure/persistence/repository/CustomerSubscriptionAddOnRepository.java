package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionAddOnEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CustomerSubscriptionAddOnRepository
        extends JpaRepository<CustomerSubscriptionAddOnEntity, UUID> {

    List<CustomerSubscriptionAddOnEntity>
    findByCustomerSubscriptionCustomerSubscriptionId(
            UUID customerSubscriptionId);
}