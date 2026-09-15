package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.CouponRedemptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CouponRedemptionRepository
        extends JpaRepository<CouponRedemptionEntity, UUID> {

    List<CouponRedemptionEntity> findByCustomerCustomerId(UUID customerId);

    List<CouponRedemptionEntity> findByCouponCouponId(UUID couponId);

    List<CouponRedemptionEntity> findByStatus(String status);
}