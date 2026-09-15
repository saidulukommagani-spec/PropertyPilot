package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.LocationMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LocationMasterRepository
        extends JpaRepository<LocationMasterEntity, UUID> {

    List<LocationMasterEntity> findByCountry(
            String country);

    List<LocationMasterEntity> findByState(
            String state);

    List<LocationMasterEntity> findByPincode(
            String pincode);

    List<LocationMasterEntity> findByLocationType(
            String locationType);
}