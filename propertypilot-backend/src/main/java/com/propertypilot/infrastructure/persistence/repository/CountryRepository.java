package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CountryRepository
        extends JpaRepository<CountryEntity, UUID> {

    Optional<CountryEntity> findByCountryCode(
            String countryCode);

    Optional<CountryEntity> findByCountryName(
            String countryName);

    boolean existsByCountryCode(
            String countryCode);
}