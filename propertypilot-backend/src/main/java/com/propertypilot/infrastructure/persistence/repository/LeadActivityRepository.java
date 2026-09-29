package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.LeadActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LeadActivityRepository
        extends JpaRepository<LeadActivityEntity, UUID> {

    List<LeadActivityEntity>
    findByLead_LeadIdOrderByOccurredAtDesc(
            UUID leadId);
}