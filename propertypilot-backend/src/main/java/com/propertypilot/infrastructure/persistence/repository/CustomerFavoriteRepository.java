package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.CustomerFavoriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerFavoriteRepository
        extends JpaRepository<CustomerFavoriteEntity, UUID> {

    List<CustomerFavoriteEntity> findByCustomerCustomerId(
            UUID customerId);

    Optional<CustomerFavoriteEntity> findByCustomerFavoriteId(
            UUID customerFavoriteId);

    boolean existsByCustomerCustomerIdAndPropertyPropertyId(
            UUID customerId,
            UUID propertyId);
}