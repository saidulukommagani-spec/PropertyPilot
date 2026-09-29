package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.PricingProfileParameterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PricingProfileParameterRepository
        extends JpaRepository<PricingProfileParameterEntity, UUID> {

    List<PricingProfileParameterEntity>
    findByPricingProfilePricingProfileId(
            UUID pricingProfileId);

    Optional<PricingProfileParameterEntity>
    findByPricingProfileProfileCodeAndParameterCode(
            String profileCode,
            String parameterCode);

            List<PricingProfileParameterEntity>
findByPricingProfileProfileCode(
        String profileCode);

        
}