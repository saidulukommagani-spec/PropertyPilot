package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.PricingParameterResponse;
import com.propertypilot.application.dto.UpdatePricingParameterRequest;
import com.propertypilot.application.service.PricingConfigurationService;
import com.propertypilot.infrastructure.persistence.entity.PricingProfileParameterEntity;
import com.propertypilot.infrastructure.persistence.repository.PricingProfileParameterRepository;
import com.propertypilot.infrastructure.persistence.repository.PricingProfileRepository;
import com.propertypilot.infrastructure.persistence.entity.PricingProfileEntity;
import com.propertypilot.domain.constants.LaunchPricingParameterCodes;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.propertypilot.application.service.PricingHistoryService;
import com.propertypilot.application.dto.PricingCouponRequest;
import com.propertypilot.application.dto.ClusterDiscountRuleRequest;
import com.propertypilot.infrastructure.persistence.entity.CouponEntity;
import com.propertypilot.infrastructure.persistence.entity.ClusterRuleEntity;
import com.propertypilot.infrastructure.persistence.repository.CouponRepository;
import com.propertypilot.infrastructure.persistence.repository.ClusterRuleRepository;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;


@Service
@Transactional
public class PricingConfigurationServiceImpl
        implements PricingConfigurationService {

   private final PricingProfileParameterRepository
        parameterRepository;

private final PricingProfileRepository profileRepository;
private final CouponRepository couponRepository;
private final ClusterRuleRepository clusterRuleRepository;

private final PricingHistoryService
        pricingHistoryService;

public PricingConfigurationServiceImpl(
        PricingProfileParameterRepository parameterRepository,
        PricingProfileRepository profileRepository,
        CouponRepository couponRepository,
        ClusterRuleRepository clusterRuleRepository,
        PricingHistoryService pricingHistoryService) {

    this.parameterRepository =
            parameterRepository;

    this.profileRepository = profileRepository;
    this.couponRepository = couponRepository;
    this.clusterRuleRepository = clusterRuleRepository;

    this.pricingHistoryService =
            pricingHistoryService;
}

    @Override
    public void setProfileActive(String profileCode, boolean active) {
        PricingProfileEntity profile = profileRepository.findByProfileCode(profileCode)
                .orElseThrow(() -> new ResourceNotFoundException("Pricing profile not found"));
        if (active) {
            List<PricingProfileParameterEntity> parameters = parameterRepository
                    .findByPricingProfileProfileCodeAndActiveFlagTrue(profileCode);
            for (String requiredCode : LaunchPricingParameterCodes.REQUIRED) {
                PricingProfileParameterEntity parameter = parameters.stream()
                        .filter(item -> requiredCode.equals(item.getParameterCode()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Required pricing parameter is missing: " + requiredCode));
                String value = parameter.getParameterValue();
                if (LaunchPricingParameterCodes.TRAVEL_DISTANCE_MODE.equals(requiredCode)) {
                    if (!"ROUND_TRIP".equalsIgnoreCase(value) && !"ONE_WAY".equalsIgnoreCase(value)) {
                        throw new IllegalArgumentException("TRAVEL_DISTANCE_MODE must be ONE_WAY or ROUND_TRIP");
                    }
                    continue;
                }
                BigDecimal numeric;
                try {
                    numeric = new BigDecimal(value);
                } catch (RuntimeException ex) {
                    throw new IllegalArgumentException("Pricing parameter is not configured: " + requiredCode);
                }
                if (numeric.signum() < 0 || (requiredCode.endsWith("PERCENT")
                        && numeric.compareTo(BigDecimal.valueOf(100)) > 0)) {
                    throw new IllegalArgumentException("Invalid pricing parameter: " + requiredCode);
                }
            }
        }
        profile.setActiveFlag(active);
        profileRepository.save(profile);
    }

    @Override
    public CouponEntity createCoupon(PricingCouponRequest request) {
        validateCoupon(request);
        String code = request.getCouponCode().trim().toUpperCase(java.util.Locale.ROOT);
        if (couponRepository.existsByCouponCode(code)) {
            throw new IllegalArgumentException("Coupon code already exists");
        }
        CouponEntity coupon = new CouponEntity();
        applyCoupon(coupon, request, code);
        return couponRepository.save(coupon);
    }

    @Override
    public CouponEntity updateCoupon(UUID couponId, PricingCouponRequest request) {
        validateCoupon(request);
        CouponEntity coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));
        String code = request.getCouponCode().trim().toUpperCase(java.util.Locale.ROOT);
        if (!code.equalsIgnoreCase(coupon.getCouponCode()) && couponRepository.existsByCouponCode(code)) {
            throw new IllegalArgumentException("Coupon code already exists");
        }
        applyCoupon(coupon, request, code);
        return couponRepository.save(coupon);
    }

    private void validateCoupon(PricingCouponRequest request) {
        if (request.getValidTo().isBefore(request.getValidFrom())) {
            throw new IllegalArgumentException("Coupon end must be after its start");
        }
        if ("PERCENTAGE".equals(request.getDiscountType())
                && request.getDiscountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Percentage coupon cannot exceed 100 percent");
        }
        if (request.getRedemptionLimit() != null && request.getRedemptionLimit() < 1
                || request.getPerCustomerLimit() != null && request.getPerCustomerLimit() < 1) {
            throw new IllegalArgumentException("Coupon redemption limits must be positive");
        }
    }

    private void applyCoupon(CouponEntity coupon, PricingCouponRequest request, String code) {
        coupon.setCouponCode(code);
        coupon.setDescription(request.getDescription());
        coupon.setDiscountType(request.getDiscountType());
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setMaximumDiscount(request.getMaximumDiscount());
        coupon.setMinimumOrderAmount(request.getMinimumOrderAmount());
        coupon.setValidFrom(request.getValidFrom());
        coupon.setValidTo(request.getValidTo());
        coupon.setRedemptionLimit(request.getRedemptionLimit());
        coupon.setPerCustomerLimit(request.getPerCustomerLimit());
        coupon.setStatus(request.getStatus());
    }

    @Override
    public ClusterRuleEntity createClusterRule(ClusterDiscountRuleRequest request) {
        validateClusterRule(request);
        ClusterRuleEntity rule = new ClusterRuleEntity();
        rule.setClusterRuleId(UUID.randomUUID());
        applyClusterRule(rule, request);
        return clusterRuleRepository.save(rule);
    }

    @Override
    public ClusterRuleEntity updateClusterRule(UUID ruleId, ClusterDiscountRuleRequest request) {
        validateClusterRule(request);
        ClusterRuleEntity rule = clusterRuleRepository.findById(ruleId)
                .orElseThrow(() -> new ResourceNotFoundException("Cluster pricing rule not found"));
        applyClusterRule(rule, request);
        return clusterRuleRepository.save(rule);
    }

    private void validateClusterRule(ClusterDiscountRuleRequest request) {
        if (request.getDiscountPercentage().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Cluster discount cannot exceed 100 percent");
        }
    }

    private void applyClusterRule(ClusterRuleEntity rule, ClusterDiscountRuleRequest request) {
        rule.setServiceType(request.getServiceType().trim().toUpperCase(java.util.Locale.ROOT));
        rule.setRadiusKm(request.getRadiusKm());
        rule.setMinimumRequests(request.getMinimumRequests());
        rule.setDiscountPercentage(request.getDiscountPercentage());
        rule.setStatus(request.getStatus());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PricingParameterResponse>
    getProfileParameters(
            String profileCode) {

        return parameterRepository
                .findByPricingProfileProfileCode(profileCode)
                .stream()
                .filter(parameter ->
                        parameter.getPricingProfile()
                                .getProfileCode()
                                .equalsIgnoreCase(profileCode))
                .map(this::buildResponse)
                .toList();
    }

    @Override
public PricingParameterResponse
updateParameter(
        UUID parameterId,
        UpdatePricingParameterRequest request) {

    PricingProfileParameterEntity entity =
            parameterRepository
                    .findById(parameterId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Pricing parameter not found"));

    String oldValue =
            entity.getParameterValue();

    String newValue =
            request.getParameterValue();

    pricingHistoryService.recordChange(
            entity,
            oldValue,
            newValue,
            null, // logged-in admin user id later
            request.getRemarks());

    entity.setParameterValue(
            newValue);

    PricingProfileEntity profile = entity.getPricingProfile();
    if (profile != null && Boolean.TRUE.equals(profile.getActiveFlag())) {
        profile.setActiveFlag(false);
        profileRepository.save(profile);
    }

    return buildResponse(
            parameterRepository.save(entity));
}

@Override
@Transactional(readOnly = true)
public String getParameterValue(
        String profileCode,
        String parameterCode) {

    PricingProfileParameterEntity entity =
            parameterRepository
                    .findByPricingProfileProfileCodeAndParameterCode(
                            profileCode,
                            parameterCode)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Pricing parameter not found"));

    return entity.getParameterValue();
}

    private PricingParameterResponse
    buildResponse(
            PricingProfileParameterEntity entity) {

        PricingParameterResponse response =
                new PricingParameterResponse();

        response.setPricingParameterId(
                entity.getPricingParameterId());

        response.setProfileCode(
                entity.getPricingProfile()
                        .getProfileCode());

        response.setParameterCode(
                entity.getParameterCode());

        response.setParameterName(
                entity.getParameterName());

        response.setParameterValue(
                entity.getParameterValue());

        response.setParameterType(
                entity.getParameterType());

        response.setActiveFlag(
                entity.getActiveFlag());

        response.setVersion(
                entity.getVersion());

        return response;
    }
   
@Override
@Transactional(readOnly = true)
public BigDecimal getDecimalParameter(
        String profileCode,
        String parameterCode) {

    String value =
            getParameterValue(
                    profileCode,
                    parameterCode);

    return new BigDecimal(value);
}
}
