package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.domain.enums.BillingEntityType;
import com.propertypilot.domain.enums.InvoiceStatus;
import com.propertypilot.infrastructure.persistence.entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InvoiceRepository
        extends JpaRepository<InvoiceEntity, UUID> {

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

        long countByStatus(
        InvoiceStatus status);

List<InvoiceEntity>
findByStatus(
        InvoiceStatus status);

        long countByCustomerCustomerIdAndStatus(
        UUID customerId,
        InvoiceStatus status);
}