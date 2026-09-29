package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceRequestRepository
        extends JpaRepository<ServiceRequestEntity, UUID> {

    List<ServiceRequestEntity> findByProperty_PropertyId(
            UUID propertyId);
            @Query("""
SELECT sr
FROM ServiceRequestEntity sr
JOIN FETCH sr.property
WHERE sr.customer.customerId = :customerId
""")
List<ServiceRequestEntity>
findByCustomerWithProperty(
        @Param("customerId")
        UUID customerId);
    @Query("""
           SELECT sr
           FROM ServiceRequestEntity sr
           JOIN FETCH sr.property
           WHERE sr.serviceRequestId = :serviceRequestId
           """)
    Optional<ServiceRequestEntity> findByIdWithProperty(
            @Param("serviceRequestId")
            UUID serviceRequestId);
@Query("""
SELECT sr
FROM ServiceRequestEntity sr
JOIN FETCH sr.property p
JOIN FETCH p.customer c
JOIN FETCH c.user
ORDER BY sr.requestedAt DESC
""")
List<ServiceRequestEntity>
findAllForAdminDashboard();
@Query("""
SELECT sr
FROM ServiceRequestEntity sr
JOIN FETCH sr.property p
JOIN FETCH sr.customer c
JOIN FETCH c.user
WHERE sr.serviceRequestId = :serviceRequestId
""")
Optional<ServiceRequestEntity>
findByIdForAdmin(
        @Param("serviceRequestId")
        UUID serviceRequestId);

        long count();

long countByStatus(
        String status);
        long countByCustomer_CustomerId(
        UUID customerId);

long countByCustomer_CustomerIdAndStatus(
        UUID customerId,
        String status);

    }