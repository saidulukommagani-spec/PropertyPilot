package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CustomerSubscriptionRepository
        extends JpaRepository<
                CustomerSubscriptionEntity,
                UUID> {

    List<CustomerSubscriptionEntity>
    findByCustomer_CustomerId(
            UUID customerId);

    List<CustomerSubscriptionEntity>
    findByStatus(
            String status);

    boolean existsByCustomerSubscriptionId(
            UUID customerSubscriptionId);
}