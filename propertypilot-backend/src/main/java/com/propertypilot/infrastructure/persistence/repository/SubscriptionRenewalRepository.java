package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.SubscriptionRenewalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SubscriptionRenewalRepository
        extends JpaRepository<
                SubscriptionRenewalEntity,
                UUID> {

    List<SubscriptionRenewalEntity>
    findByCustomerSubscription_CustomerSubscriptionId(
            UUID customerSubscriptionId);

    List<SubscriptionRenewalEntity>
    findByNewCustomerSubscription_CustomerSubscriptionId(
            UUID customerSubscriptionId);

    List<SubscriptionRenewalEntity>
    findByStatus(
            String status);

    List<SubscriptionRenewalEntity>
    findByRenewalDateBetween(
            LocalDate fromDate,
            LocalDate toDate);

    List<SubscriptionRenewalEntity>
    findByRenewalType(
            String renewalType);
}