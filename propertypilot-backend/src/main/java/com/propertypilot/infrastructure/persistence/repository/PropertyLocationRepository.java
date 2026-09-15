package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.PropertyLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PropertyLocationRepository
        extends JpaRepository<PropertyLocationEntity, UUID> {

    Optional<PropertyLocationEntity>
    findByProperty_PropertyId(UUID propertyId);
    
}