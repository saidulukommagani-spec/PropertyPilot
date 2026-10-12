package com.propertypilot.application.service.impl;

import com.propertypilot.application.service.*;
import com.propertypilot.application.dto.CreatePricingEstimateRequest;
import com.propertypilot.application.dto.PricingBreakdownResponse;
import com.propertypilot.application.dto.PricingCalculationResponse;
import com.propertypilot.infrastructure.persistence.entity.PricingEstimateEntity;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.Property;
import com.propertypilot.infrastructure.persistence.entity.ServiceEntity;
import com.propertypilot.infrastructure.persistence.repository.*;
import com.propertypilot.security.SecurityService;
import com.propertypilot.web.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PricingEngineServiceImplTest {
    private final CustomerJpaRepository customers = mock(CustomerJpaRepository.class);
    private final PropertyRepository properties = mock(PropertyRepository.class);
    private final ServiceRepository services = mock(ServiceRepository.class);
    private final PricingRuleRepository rules = mock(PricingRuleRepository.class);
    private final PricingCalculatorService calculator = mock(PricingCalculatorService.class);
    private final PricingProfileLoaderService profiles = mock(PricingProfileLoaderService.class);
    private final PropertyLocationRepository locations = mock(PropertyLocationRepository.class);
    private final CoverageZoneRepository coverage = mock(CoverageZoneRepository.class);
    private final ClusterRuleRepository clusters = mock(ClusterRuleRepository.class);
    private final ServiceRequestRepository requests = mock(ServiceRequestRepository.class);
    private final CouponRepository coupons = mock(CouponRepository.class);
    private final CouponRedemptionRepository redemptions = mock(CouponRedemptionRepository.class);
    private final CustomerSubscriptionRepository subscriptions = mock(CustomerSubscriptionRepository.class);
    private final PricingEstimateRepository estimates = mock(PricingEstimateRepository.class);
    private final PricingQuoteLineItemRepository lines = mock(PricingQuoteLineItemRepository.class);
    private final PricingDiscountApplicationRepository discounts = mock(PricingDiscountApplicationRepository.class);
    private final SecurityService security = mock(SecurityService.class);
    private final SubscriptionPaymentVerificationService paymentVerification = mock(SubscriptionPaymentVerificationService.class);
    private final EligibilityEngine eligibility = mock(EligibilityEngine.class);
    private final PricingEngineServiceImpl engine = new PricingEngineServiceImpl(customers, properties, services,
            rules, calculator, profiles, locations, coverage, clusters, requests, coupons, redemptions,
            subscriptions, estimates, lines, discounts, security, paymentVerification, eligibility);

    private UUID propertyId;
    private UUID serviceId;
    private CustomerEntity customer;
    private Property property;
    private ServiceEntity service;

    @BeforeEach
    void setUp() {
        propertyId = UUID.randomUUID();
        serviceId = UUID.randomUUID();
        customer = new CustomerEntity();
        customer.setCustomerId(UUID.randomUUID());
        property = new Property();
        property.setPropertyId(propertyId);
        property.setCustomer(customer);
        property.setStatus("ACTIVE");
        service = new ServiceEntity();
        service.setServiceId(serviceId);
        service.setActiveFlag(true);
        service.setBillable(true);
        when(security.getCurrentCustomer()).thenReturn(customer);
        when(properties.findById(propertyId)).thenReturn(Optional.of(property));
        when(services.findById(serviceId)).thenReturn(Optional.of(service));
    }

    @Test
    void customerEstimateIgnoresSubmittedCustomerAndDistanceAndRoutesVisitServicesToOperations() {
        service.setRequiresVisit(true);
        var request = new com.propertypilot.application.dto.CreatePricingEstimateRequest();
        request.setCustomerId(UUID.randomUUID());
        request.setPropertyId(propertyId);
        request.setServiceId(serviceId);
        request.setOneWayDistanceKm(999d);

        assertThatThrownBy(() -> engine.calculateCustomerEstimate(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("travel-based estimate is not available");

        verify(security).getCurrentCustomer();
        verify(security).validatePropertyOwnership(property);
        verify(eligibility).validateEligibility(serviceId, propertyId);
        verifyNoInteractions(calculator, estimates);
    }

    @Test
    void customerEstimateRejectsPropertyWithoutOwnershipAuthorization() {
        doThrow(new AccessDeniedException("property access denied"))
                .when(security).validatePropertyOwnership(property);
        var request = new com.propertypilot.application.dto.CreatePricingEstimateRequest();
        request.setPropertyId(propertyId);
        request.setServiceId(serviceId);

        assertThatThrownBy(() -> engine.calculateCustomerEstimate(request))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(eligibility, calculator, estimates);
    }

    @Test
    void customerEstimateDoesNotReadInvalidOptionalEtaParametersWhenNoEtaWasRequested() {
        service.setBasePrice(new BigDecimal("100.00"));
        service.setServiceName("Document Verification");
        when(rules.findByServiceServiceIdAndStatus(serviceId, "ACTIVE")).thenReturn(List.of());
        when(profiles.loadProfile("CUSTOMER")).thenReturn(Map.of(
                "ETA_DISCOUNT_PERCENT", "UNCONFIGURED",
                "ETA_DISCOUNT_MIN_DAYS", "UNCONFIGURED",
                "GST_PERCENT", "0"));
        when(subscriptions.findByCustomer_CustomerIdAndStatus(customer.getCustomerId(), "ACTIVE"))
                .thenReturn(List.of());
        when(locations.findByProperty_PropertyId(propertyId)).thenReturn(Optional.empty());
        PricingBreakdownResponse standard = breakdown(new BigDecimal("100.00"));
        PricingBreakdownResponse customerPrice = breakdown(new BigDecimal("100.00"));
        PricingCalculationResponse calculated = new PricingCalculationResponse();
        calculated.setStandardBreakdown(standard);
        calculated.setCustomerBreakdown(customerPrice);
        when(calculator.calculate(any())).thenReturn(calculated);
        when(estimates.save(any(PricingEstimateEntity.class))).thenAnswer(call -> call.getArgument(0));

        CreatePricingEstimateRequest request = new CreatePricingEstimateRequest();
        request.setPropertyId(propertyId);
        request.setServiceId(serviceId);

        var response = engine.calculateCustomerEstimate(request);

        org.assertj.core.api.Assertions.assertThat(response.getCustomerPrice()).isEqualByComparingTo("100.00");
        verify(estimates).save(any(PricingEstimateEntity.class));
    }

    private PricingBreakdownResponse breakdown(BigDecimal amount) {
        PricingBreakdownResponse value = new PricingBreakdownResponse();
        value.setVendorCost(amount);
        value.setTravelCost(BigDecimal.ZERO);
        value.setAgentDa(BigDecimal.ZERO);
        value.setOverheadAmount(BigDecimal.ZERO);
        value.setMarginAmount(BigDecimal.ZERO);
        value.setSubtotal(amount);
        value.setGstAmount(BigDecimal.ZERO);
        value.setTotalAmount(amount);
        return value;
    }
}
