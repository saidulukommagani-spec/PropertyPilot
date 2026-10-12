package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceRequestRepository
        extends JpaRepository<ServiceRequestEntity, UUID> {

    List<ServiceRequestEntity> findByProperty_PropertyId(
            UUID propertyId);

    List<ServiceRequestEntity> findByService_ServiceIdAndStatusIn(UUID serviceId, List<String> statuses);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select sr from ServiceRequestEntity sr where sr.serviceRequestId = :serviceRequestId")
    Optional<ServiceRequestEntity> findByIdForUpdate(@Param("serviceRequestId") UUID serviceRequestId);
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

    boolean existsByPricingEstimate_PricingEstimateId(UUID pricingEstimateId);

    java.util.Optional<ServiceRequestEntity> findByPricingEstimate_PricingEstimateId(UUID pricingEstimateId);

    List<ServiceRequestEntity> findByLead_LeadIdOrderByRequestedAtDesc(UUID leadId);

    }
