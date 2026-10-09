package com.propertypilot.application.service;

import com.propertypilot.application.dto.PricingParameterResponse;
import com.propertypilot.application.dto.UpdatePricingParameterRequest;
import com.propertypilot.application.dto.PricingCouponRequest;
import com.propertypilot.application.dto.ClusterDiscountRuleRequest;
import com.propertypilot.infrastructure.persistence.entity.CouponEntity;
import com.propertypilot.infrastructure.persistence.entity.ClusterRuleEntity;

import java.util.List;
import java.util.UUID;

public interface PricingConfigurationService {

    List<PricingParameterResponse>
    getProfileParameters(
            String profileCode);

    PricingParameterResponse
    updateParameter(
            UUID parameterId,
            UpdatePricingParameterRequest request);

           String getParameterValue(
        String profileCode,
        String parameterCode);

java.math.BigDecimal getDecimalParameter(
        String profileCode,
        String parameterCode); 

void setProfileActive(String profileCode, boolean active);

CouponEntity createCoupon(PricingCouponRequest request);
CouponEntity updateCoupon(UUID couponId, PricingCouponRequest request);
ClusterRuleEntity createClusterRule(ClusterDiscountRuleRequest request);
ClusterRuleEntity updateClusterRule(UUID ruleId, ClusterDiscountRuleRequest request);

        
}
