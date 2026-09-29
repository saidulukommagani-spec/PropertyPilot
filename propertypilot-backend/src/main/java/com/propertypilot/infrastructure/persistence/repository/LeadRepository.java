package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.domain.enums.LeadStatus;
import com.propertypilot.infrastructure.persistence.entity.LeadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeadRepository
        extends JpaRepository<LeadEntity, UUID> {

    Optional<LeadEntity>
    findByLeadId(
            UUID leadId);

    List<LeadEntity>
    findByCustomer_CustomerId(
            UUID customerId);

    List<LeadEntity>
    findByCustomer_CustomerIdOrderByCreatedAtDesc(
            UUID customerId);

    List<LeadEntity>
    findByStatus(
            LeadStatus status);
            long count();
}