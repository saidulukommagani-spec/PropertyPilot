package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.DealEntity;
import com.propertypilot.domain.enums.DealStatus;
import com.propertypilot.domain.enums.DealType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface DealRepository
        extends JpaRepository<DealEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from DealEntity d where d.dealId = :dealId")
    Optional<DealEntity> findByIdForUpdate(@Param("dealId") UUID dealId);

                long count();

    Optional<DealEntity> findByPricingEstimate_PricingEstimateId(UUID pricingEstimateId);

    List<DealEntity> findByLead_LeadIdOrderByCreatedAtDesc(UUID leadId);

    boolean existsByLead_LeadIdAndDealTypeAndDealStatusIn(
            UUID leadId, DealType dealType, List<DealStatus> dealStatuses);

    long countByLeadIsNotNull();
}
