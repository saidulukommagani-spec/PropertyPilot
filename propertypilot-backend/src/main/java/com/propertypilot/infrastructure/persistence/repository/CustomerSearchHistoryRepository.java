package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.CustomerSearchHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerSearchHistoryRepository
        extends JpaRepository<CustomerSearchHistoryEntity, UUID> {

    List<CustomerSearchHistoryEntity>
    findByCustomerCustomerId(
            UUID customerId);

    Optional<CustomerSearchHistoryEntity>
    findByCustomerSearchHistoryId(
            UUID customerSearchHistoryId);
}