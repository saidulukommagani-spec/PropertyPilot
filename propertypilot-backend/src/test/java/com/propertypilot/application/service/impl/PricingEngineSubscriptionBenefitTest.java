package com.propertypilot.application.service.impl;

import com.propertypilot.application.service.PricingCalculatorService;
import com.propertypilot.application.service.PricingProfileLoaderService;
import com.propertypilot.application.service.SubscriptionPaymentVerificationService;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanVersionEntity;
import com.propertypilot.infrastructure.persistence.repository.*;
import com.propertypilot.security.SecurityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PricingEngineSubscriptionBenefitTest {
    @Mock private CustomerJpaRepository customerRepository;
    @Mock private PropertyRepository propertyRepository;
    @Mock private ServiceRepository serviceRepository;
    @Mock private PricingRuleRepository pricingRuleRepository;
    @Mock private PricingCalculatorService calculator;
    @Mock private PricingProfileLoaderService profileLoader;
    @Mock private PropertyLocationRepository propertyLocationRepository;
    @Mock private CoverageZoneRepository coverageZoneRepository;
    @Mock private ClusterRuleRepository clusterRuleRepository;
    @Mock private ServiceRequestRepository serviceRequestRepository;
    @Mock private CouponRepository couponRepository;
    @Mock private CouponRedemptionRepository couponRedemptionRepository;
    @Mock private CustomerSubscriptionRepository subscriptionRepository;
    @Mock private PricingEstimateRepository estimateRepository;
    @Mock private PricingQuoteLineItemRepository lineItemRepository;
    @Mock private PricingDiscountApplicationRepository discountRepository;
    @Mock private SecurityService securityService;
    @Mock private SubscriptionPaymentVerificationService paymentVerificationService;

    @InjectMocks private PricingEngineServiceImpl service;

    @Test
    void activeSubscriptionOnlyGrantsDiscountAfterPaymentVerification() {
        CustomerEntity customer = new CustomerEntity();
        customer.setCustomerId(UUID.randomUUID());
        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setServiceId(UUID.randomUUID());

        SubscriptionPlanVersionEntity plan = new SubscriptionPlanVersionEntity();
        plan.setPrice(new BigDecimal("1499.00"));
        plan.setBenefitsJson(Map.of("serviceDiscountPercent", 10));
        CustomerSubscriptionEntity subscription = new CustomerSubscriptionEntity();
        subscription.setCustomerSubscriptionId(UUID.randomUUID());
        subscription.setCustomer(customer);
        subscription.setPlanVersion(plan);
        subscription.setStartDate(LocalDate.now().minusDays(1));
        subscription.setEndDate(LocalDate.now().plusDays(30));

        when(subscriptionRepository.findByCustomer_CustomerIdAndStatus(customer.getCustomerId(), "ACTIVE"))
                .thenReturn(List.of(subscription));
        when(paymentVerificationService.isPaymentVerified(subscription)).thenReturn(false, true);
        OffsetDateTime now = OffsetDateTime.now();

        assertEquals(BigDecimal.ZERO, service.getActiveSubscriptionDiscount(customer, serviceEntity, now));
        assertEquals(new BigDecimal("10"), service.getActiveSubscriptionDiscount(customer, serviceEntity, now));
    }
}
