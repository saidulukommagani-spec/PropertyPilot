package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.PropertyOwnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PropertyOwnerRepository
        extends JpaRepository<PropertyOwnerEntity, UUID> {

    List<PropertyOwnerEntity>
    findByProperty_PropertyId(
            UUID propertyId);

    boolean existsByProperty_PropertyIdAndIsPrimaryTrue(
            UUID propertyId);

    List<PropertyOwnerEntity>
    findByProperty_PropertyIdAndPropertyOwnerIdNot(
            UUID propertyId,
            UUID propertyOwnerId);
}