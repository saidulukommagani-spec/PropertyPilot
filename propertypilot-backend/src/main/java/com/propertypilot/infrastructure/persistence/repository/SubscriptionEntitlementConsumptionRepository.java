package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.SubscriptionEntitlementConsumptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionEntitlementConsumptionRepository
        extends JpaRepository<
                SubscriptionEntitlementConsumptionEntity,
                UUID> {

    List<SubscriptionEntitlementConsumptionEntity>
    findByCustomerSubscription_CustomerSubscriptionId(
            UUID customerSubscriptionId);

    List<SubscriptionEntitlementConsumptionEntity>
    findByCustomerSubscription_CustomerSubscriptionIdAndService_ServiceId(
            UUID customerSubscriptionId,
            UUID serviceId);

    Optional<SubscriptionEntitlementConsumptionEntity>
    findByCustomerSubscription_CustomerSubscriptionIdAndService_ServiceIdAndPeriodEndIsNull(
            UUID customerSubscriptionId,
            UUID serviceId);

    List<SubscriptionEntitlementConsumptionEntity>
    findByCustomerSubscription_CustomerSubscriptionIdAndPeriodEndIsNull(
            UUID customerSubscriptionId);
}