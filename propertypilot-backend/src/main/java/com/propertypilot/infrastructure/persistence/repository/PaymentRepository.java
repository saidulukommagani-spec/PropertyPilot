package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;
import java.util.Optional;
import com.propertypilot.domain.enums.PaymentStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

public interface PaymentRepository
        extends JpaRepository<PaymentEntity, UUID> {

        @EntityGraph(attributePaths = {"invoice", "customer", "customer.user"})
        @Query("select p from PaymentEntity p")
        List<PaymentEntity> findAllWithDisplayDetails();

        Optional<PaymentEntity> findByProviderAndProviderPaymentId(String provider, String providerPaymentId);

        @Query("select p.invoice.invoiceId from PaymentEntity p where p.provider = :provider and p.providerPaymentId = :providerPaymentId")
        Optional<UUID> findInvoiceIdByProviderAndProviderPaymentId(
                @Param("provider") String provider,
                @Param("providerPaymentId") String providerPaymentId);

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("select p from PaymentEntity p where p.provider = :provider and p.providerPaymentId = :providerPaymentId")
        Optional<PaymentEntity> findByProviderAndProviderPaymentIdForUpdate(
                @Param("provider") String provider,
                @Param("providerPaymentId") String providerPaymentId);
        Optional<PaymentEntity> findByProviderEventId(String providerEventId);
        boolean existsByPaymentIdAndPaymentStatus(UUID paymentId, PaymentStatus paymentStatus);
        boolean existsByInvoice_InvoiceIdAndPaymentStatus(UUID invoiceId, PaymentStatus paymentStatus);

        @Query("select coalesce(sum(p.amount), 0) from PaymentEntity p where p.invoice.invoiceId = :invoiceId and p.paymentStatus = :status")
        BigDecimal sumAmountByInvoiceAndStatus(@Param("invoiceId") UUID invoiceId, @Param("status") PaymentStatus status);

        @Query("select coalesce(sum(p.amount), 0) from PaymentEntity p where p.invoice.invoiceId = :invoiceId and p.paymentStatus = :status and p.settledAt is not null and upper(p.currencyCode) = upper(:currencyCode)")
        BigDecimal sumVerifiedSettledAmountByInvoiceAndCurrency(
                @Param("invoiceId") UUID invoiceId,
                @Param("status") PaymentStatus status,
                @Param("currencyCode") String currencyCode);

        @Query("select p.invoice.invoiceId, coalesce(sum(p.amount), 0) from PaymentEntity p where p.paymentStatus = :status and p.invoice is not null and p.settledAt is not null and upper(p.currencyCode) = upper(p.invoice.currencyCode) group by p.invoice.invoiceId")
        List<Object[]> sumSettledByInvoice(@Param("status") PaymentStatus status);
                List<PaymentEntity>
findByInvoice_InvoiceId(
        UUID invoiceId);

        long count();
        long countByPaymentStatus(PaymentStatus paymentStatus);
        long countByCustomer_CustomerId(
        UUID customerId);
}
