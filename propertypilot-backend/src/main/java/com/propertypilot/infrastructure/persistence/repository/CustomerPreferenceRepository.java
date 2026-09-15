package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.CustomerPreferenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerPreferenceRepository
        extends JpaRepository<CustomerPreferenceEntity, UUID> {

    Optional<CustomerPreferenceEntity> findByCustomerCustomerId(
            UUID customerId);
}