package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.CouponRedemptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import java.util.Collection;

public interface CouponRedemptionRepository
        extends JpaRepository<CouponRedemptionEntity, UUID> {

    List<CouponRedemptionEntity> findByCustomerCustomerId(UUID customerId);

    List<CouponRedemptionEntity> findByCouponCouponId(UUID couponId);

    List<CouponRedemptionEntity> findByStatus(String status);

    long countByCoupon_CouponIdAndStatusIn(UUID couponId, Collection<String> statuses);
    long countByCoupon_CouponIdAndCustomer_CustomerIdAndStatusIn(
            UUID couponId, UUID customerId, Collection<String> statuses);

    boolean existsByCoupon_CouponIdAndPricingEstimate_PricingEstimateIdAndStatusIn(
            UUID couponId, UUID pricingEstimateId, Collection<String> statuses);
}
