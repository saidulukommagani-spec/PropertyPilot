package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.MonitoringScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.UUID;
import java.time.OffsetDateTime;

public interface MonitoringScheduleRepository
        extends JpaRepository<MonitoringScheduleEntity, UUID> {
    long countByStatusIgnoreCase(String status);
    long countByStatusIgnoreCaseAndNextDueAtBefore(String status, OffsetDateTime before);

    java.util.List<MonitoringScheduleEntity> findByStatusIgnoreCaseAndNextDueAtLessThanEqualOrderByNextDueAtAsc(
            String status, OffsetDateTime dueAt);

    java.util.List<MonitoringScheduleEntity> findByProperty_Customer_CustomerIdOrderByNextDueAtAsc(UUID customerId);
    boolean existsBySourceServiceRequest_ServiceRequestId(UUID serviceRequestId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select schedule from MonitoringScheduleEntity schedule where schedule.monitoringScheduleId = :id")
    java.util.Optional<MonitoringScheduleEntity> findByIdForUpdate(@Param("id") UUID id);
}
