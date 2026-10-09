package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.PricingEstimateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.UUID;
import java.time.OffsetDateTime;
import java.util.List;

public interface PricingEstimateRepository
        extends JpaRepository<PricingEstimateEntity, UUID> {

    List<PricingEstimateEntity> findByCustomer_CustomerIdOrderByCreatedAtDesc(UUID customerId);

    List<PricingEstimateEntity> findByStatusAndExpiresAtLessThanEqual(String status, OffsetDateTime now);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PricingEstimateEntity p where p.pricingEstimateId = :quoteId")
    java.util.Optional<PricingEstimateEntity> findByIdForUpdate(@Param("quoteId") UUID quoteId);
}
