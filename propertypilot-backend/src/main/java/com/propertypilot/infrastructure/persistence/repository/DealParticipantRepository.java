package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.DealParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DealParticipantRepository
        extends JpaRepository<
        DealParticipantEntity,
        UUID> {

    List<DealParticipantEntity>
    findByDeal_DealId(
            UUID dealId);

}