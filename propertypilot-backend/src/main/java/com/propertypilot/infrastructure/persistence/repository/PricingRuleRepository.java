package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.PricingRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
public interface PricingRuleRepository
        extends JpaRepository<PricingRuleEntity, UUID> {

    List<PricingRuleEntity>
    findByStatus(String status);

    List<PricingRuleEntity>
    findByServiceServiceId(UUID serviceId);

    List<PricingRuleEntity>
    findByServiceServiceIdAndStatus(
            UUID serviceId,
            String status);

            Optional<PricingRuleEntity>
findByPriceRuleId(
        UUID priceRuleId);

boolean existsByService_ServiceIdAndRuleNameAndStatus(
        UUID serviceId,
        String ruleName,
        String status);

        Optional<PricingRuleEntity>
findFirstByService_ServiceIdAndStatusAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(
        UUID serviceId,
        String status,
        OffsetDateTime effectiveDate);
}