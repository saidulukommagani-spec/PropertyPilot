package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository
        extends JpaRepository<PaymentEntity, UUID> {
                List<PaymentEntity>
findByInvoice_InvoiceId(
        UUID invoiceId);

        long count();
        long countByCustomer_CustomerId(
        UUID customerId);
}