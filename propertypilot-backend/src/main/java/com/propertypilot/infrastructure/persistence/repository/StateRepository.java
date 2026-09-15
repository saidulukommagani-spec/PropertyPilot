package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.StateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StateRepository
        extends JpaRepository<StateEntity, UUID> {

    List<StateEntity> findByCountryCountryId(
            UUID countryId);

    boolean existsByCountryCountryIdAndStateName(
            UUID countryId,
            String stateName);
}