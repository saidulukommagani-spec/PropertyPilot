package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.domain.enums.BillingEntityType;
import com.propertypilot.domain.enums.InvoiceStatus;
import com.propertypilot.domain.enums.InvoiceType;
import com.propertypilot.infrastructure.persistence.entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import java.time.OffsetDateTime;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface InvoiceRepository
        extends JpaRepository<InvoiceEntity, UUID> {

    @Query(value = "select nextval('invoice_number_seq')", nativeQuery = true)
    Long nextInvoiceNumberSequence();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from InvoiceEntity i where i.invoiceId = :invoiceId")
    Optional<InvoiceEntity> findByIdForUpdate(@Param("invoiceId") UUID invoiceId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from InvoiceEntity i where i.status in :statuses and i.dueAt < :before order by i.dueAt, i.invoiceId")
    List<InvoiceEntity> findByStatusInAndDueAtBeforeForUpdate(
            @Param("statuses") List<InvoiceStatus> statuses,
            @Param("before") OffsetDateTime before);

    @Query("select coalesce(sum(i.invoiceAmount), 0) from InvoiceEntity i")
    BigDecimal sumInvoiceAmounts();

    @Query("select coalesce(sum(i.invoiceAmount), 0) from InvoiceEntity i where i.status = :status")
    BigDecimal sumInvoiceAmountsByStatus(@Param("status") InvoiceStatus status);

    @Query("select coalesce(sum(i.invoiceAmount), 0) from InvoiceEntity i where i.entityType = :entityType")
    BigDecimal sumInvoiceAmountsByEntityType(@Param("entityType") BillingEntityType entityType);

    List<InvoiceEntity> findByEntityTypeAndEntityId(
            String entityType,
            UUID entityId);

    boolean existsByInvoiceNumber(
            String invoiceNumber);

            List<InvoiceEntity>
findByCustomerCustomerId(
        UUID customerId);

List<InvoiceEntity>
findByEntityTypeAndEntityId(
        BillingEntityType entityType,
        UUID entityId);

boolean existsByEntityTypeAndEntityId(BillingEntityType entityType, UUID entityId);

List<InvoiceEntity> findByEntityTypeAndEntityIdAndInvoiceType(
        BillingEntityType entityType, UUID entityId, InvoiceType invoiceType);

    List<InvoiceEntity> findByEntityTypeAndEntityIdIn(BillingEntityType entityType, List<UUID> entityIds);

        long countByStatus(
        InvoiceStatus status);
long countByStatusAndDueAtBefore(InvoiceStatus status, OffsetDateTime before);

List<InvoiceEntity>
findByStatus(
        InvoiceStatus status);

List<InvoiceEntity> findByStatusIn(List<InvoiceStatus> statuses);

        long countByCustomerCustomerIdAndStatus(
        UUID customerId,
        InvoiceStatus status);
}
