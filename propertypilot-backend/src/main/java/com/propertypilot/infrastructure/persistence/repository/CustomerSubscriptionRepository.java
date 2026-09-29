package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerSubscriptionRepository
        extends JpaRepository<
                CustomerSubscriptionEntity,
                UUID> {

    List<CustomerSubscriptionEntity>
    findByCustomer_CustomerId(
            UUID customerId);

    List<CustomerSubscriptionEntity>
    findByStatus(
            String status);

    Optional<CustomerSubscriptionEntity>
    findByCustomerSubscriptionId(
            UUID customerSubscriptionId);

    boolean existsByCustomerSubscriptionId(
            UUID customerSubscriptionId);

    /*
     * Active subscriptions for customer.
     */
    List<CustomerSubscriptionEntity>
    findByCustomer_CustomerIdAndStatus(
            UUID customerId,
            String status);

    /*
     * Expired subscriptions.
     */
    List<CustomerSubscriptionEntity>
    findByStatusAndEndDateBefore(
            String status,
            LocalDate date);

    /*
     * Expiring soon.
     */
    List<CustomerSubscriptionEntity>
    findByStatusAndEndDateBetween(
            String status,
            LocalDate startDate,
            LocalDate endDate);

    /*
     * Auto-renew enabled subscriptions.
     */
    List<CustomerSubscriptionEntity>
    findByAutoRenewTrueAndStatus(
            String status);

    /*
     * Active subscriptions ending today.
     */
    List<CustomerSubscriptionEntity>
    findByStatusAndEndDate(
            String status,
            LocalDate endDate);
long countByStatus(
        String status);
        long countByCustomer_CustomerIdAndStatus(
        UUID customerId,
        String status);

}