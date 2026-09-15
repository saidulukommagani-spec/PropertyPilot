package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.LocalityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LocalityRepository
        extends JpaRepository<LocalityEntity, UUID> {

    List<LocalityEntity> findByDistrictDistrictId(
            UUID districtId);

    List<LocalityEntity> findByPincode(
            String pincode);

    boolean existsByDistrictDistrictIdAndLocalityName(
            UUID districtId,
            String localityName);
}