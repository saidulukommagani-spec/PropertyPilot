package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.domain.enums.LeadStatus;
import com.propertypilot.infrastructure.persistence.entity.LeadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    findByAssignedAgent_AgentIdOrderByCreatedAtDesc(
            UUID agentId);

    List<LeadEntity>
    findByStatus(
            LeadStatus status);
            long count();
    long countByStatus(LeadStatus status);

    @Query(value = """
            select l from LeadEntity l
            left join l.customer c
            left join c.user u
            where (:status is null or l.status = :status)
              and (:search is null or :search = ''
                or lower(coalesce(l.source, '')) like lower(concat('%', :search, '%'))
                or lower(coalesce(l.propertyType, '')) like lower(concat('%', :search, '%'))
                or lower(coalesce(l.location, '')) like lower(concat('%', :search, '%'))
                or lower(coalesce(u.fullName, '')) like lower(concat('%', :search, '%'))
                or lower(coalesce(u.email, '')) like lower(concat('%', :search, '%'))
                or lower(coalesce(u.mobileNumber, '')) like lower(concat('%', :search, '%')))
            """,
            countQuery = """
            select count(l) from LeadEntity l
            left join l.customer c
            left join c.user u
            where (:status is null or l.status = :status)
              and (:search is null or :search = ''
                or lower(coalesce(l.source, '')) like lower(concat('%', :search, '%'))
                or lower(coalesce(l.propertyType, '')) like lower(concat('%', :search, '%'))
                or lower(coalesce(l.location, '')) like lower(concat('%', :search, '%'))
                or lower(coalesce(u.fullName, '')) like lower(concat('%', :search, '%'))
                or lower(coalesce(u.email, '')) like lower(concat('%', :search, '%'))
                or lower(coalesce(u.mobileNumber, '')) like lower(concat('%', :search, '%')))
            """)
    Page<LeadEntity> searchForOperations(@Param("search") String search,
                                         @Param("status") LeadStatus status,
                                         Pageable pageable);
}
