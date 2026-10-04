package com.propertypilot.application.scheduler;

import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanVersionEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionRenewalEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionPlanVersionRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionRenewalRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class SubscriptionAutoRenewSchedulerTest {

    @Mock
    private CustomerSubscriptionRepository
            customerSubscriptionRepository;

    @Mock
    private SubscriptionPlanVersionRepository
            planVersionRepository;

    @Mock
    private SubscriptionRenewalRepository
            renewalRepository;

    private SubscriptionAutoRenewScheduler scheduler;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        scheduler =
                new SubscriptionAutoRenewScheduler(
                        customerSubscriptionRepository,
                        planVersionRepository,
                        renewalRepository);
    }

    @Test
    void autoRenewSubscriptions_ShouldReturn_WhenNoSubscriptionsFound() {

        when(customerSubscriptionRepository
                .findByAutoRenewTrueAndStatus(
                        "ACTIVE"))
                .thenReturn(
                        Collections.emptyList());

        scheduler.autoRenewSubscriptions();

        verify(customerSubscriptionRepository)
                .findByAutoRenewTrueAndStatus(
                        "ACTIVE");

        verifyNoInteractions(
                planVersionRepository,
                renewalRepository);
    }

    @Test
    void autoRenewSubscriptions_ShouldSkip_WhenEndDateIsNull() {

        CustomerSubscriptionEntity subscription =
                new CustomerSubscriptionEntity();

        when(customerSubscriptionRepository
                .findByAutoRenewTrueAndStatus(
                        "ACTIVE"))
                .thenReturn(
                        List.of(subscription));

        scheduler.autoRenewSubscriptions();

        verifyNoInteractions(
                planVersionRepository,
                renewalRepository);

        verify(customerSubscriptionRepository,
                never())
                .save(any());
    }

    @Test
    void autoRenewSubscriptions_ShouldSkip_WhenEndDateIsNotToday() {

        CustomerSubscriptionEntity subscription =
                new CustomerSubscriptionEntity();

        subscription.setEndDate(
                LocalDate.now().plusDays(5));

        when(customerSubscriptionRepository
                .findByAutoRenewTrueAndStatus(
                        "ACTIVE"))
                .thenReturn(
                        List.of(subscription));

        scheduler.autoRenewSubscriptions();

        verifyNoInteractions(
                planVersionRepository,
                renewalRepository);

        verify(customerSubscriptionRepository,
                never())
                .save(any());
    }

    @Test
    void autoRenewSubscriptions_ShouldRenewSuccessfully() {

        UUID planId =
                UUID.randomUUID();

        UUID customerId =
                UUID.randomUUID();

        UUID oldSubscriptionId =
                UUID.randomUUID();

        CustomerEntity customer =
                new CustomerEntity();

        customer.setCustomerId(
                customerId);

        SubscriptionPlanEntity plan =
                new SubscriptionPlanEntity();

        plan.setSubscriptionPlanId(
                planId);

        SubscriptionPlanVersionEntity currentVersion =
                new SubscriptionPlanVersionEntity();

        currentVersion.setSubscriptionPlan(
                plan);

        SubscriptionPlanVersionEntity latestVersion =
                new SubscriptionPlanVersionEntity();

        latestVersion.setSubscriptionPlan(
                plan);

        latestVersion.setPrice(
                new BigDecimal("9999.00"));

        CustomerSubscriptionEntity subscription =
                new CustomerSubscriptionEntity();

        subscription.setCustomerSubscriptionId(
                oldSubscriptionId);

        subscription.setCustomer(
                customer);

        subscription.setPlanVersion(
                currentVersion);

        subscription.setStatus(
                "ACTIVE");

        subscription.setAutoRenew(
                true);

        subscription.setEndDate(
                LocalDate.now());

        when(customerSubscriptionRepository
                .findByAutoRenewTrueAndStatus(
                        "ACTIVE"))
                .thenReturn(
                        List.of(subscription));

        when(planVersionRepository
                .findTopBySubscriptionPlan_SubscriptionPlanIdOrderByVersionNumberDesc(
                        planId))
                .thenReturn(
                        Optional.of(latestVersion));

        scheduler.autoRenewSubscriptions();

        verify(planVersionRepository)
                .findTopBySubscriptionPlan_SubscriptionPlanIdOrderByVersionNumberDesc(
                        planId);

        verify(customerSubscriptionRepository,
                atLeast(2))
                .save(any(
                        CustomerSubscriptionEntity.class));

        verify(renewalRepository)
                .save(any(
                        SubscriptionRenewalEntity.class));

        assertEquals(
                "RENEWED",
                subscription.getStatus());
    }

    @Test
    void autoRenewSubscriptions_ShouldCreateCorrectNewSubscription() {

        UUID planId =
                UUID.randomUUID();

        CustomerEntity customer =
                new CustomerEntity();

        customer.setCustomerId(
                UUID.randomUUID());

        SubscriptionPlanEntity plan =
                new SubscriptionPlanEntity();

        plan.setSubscriptionPlanId(
                planId);

        SubscriptionPlanVersionEntity currentVersion =
                new SubscriptionPlanVersionEntity();

        currentVersion.setSubscriptionPlan(
                plan);

        SubscriptionPlanVersionEntity latestVersion =
                new SubscriptionPlanVersionEntity();

        latestVersion.setSubscriptionPlan(
                plan);

        latestVersion.setPrice(
                new BigDecimal("5000.00"));

        CustomerSubscriptionEntity subscription =
                new CustomerSubscriptionEntity();

        subscription.setCustomerSubscriptionId(
                UUID.randomUUID());

        subscription.setCustomer(
                customer);

        subscription.setPlanVersion(
                currentVersion);

        subscription.setEndDate(
                LocalDate.now());

        when(customerSubscriptionRepository
                .findByAutoRenewTrueAndStatus(
                        "ACTIVE"))
                .thenReturn(
                        List.of(subscription));

        when(planVersionRepository
                .findTopBySubscriptionPlan_SubscriptionPlanIdOrderByVersionNumberDesc(
                        planId))
                .thenReturn(
                        Optional.of(latestVersion));

        ArgumentCaptor<CustomerSubscriptionEntity>
                subscriptionCaptor =
                ArgumentCaptor.forClass(
                        CustomerSubscriptionEntity.class);

        scheduler.autoRenewSubscriptions();

        verify(customerSubscriptionRepository,
                atLeast(1))
                .save(
                        subscriptionCaptor.capture());

        List<CustomerSubscriptionEntity>
                capturedSubscriptions =
                subscriptionCaptor.getAllValues();

        CustomerSubscriptionEntity
                newSubscription =
                capturedSubscriptions.stream()
                        .filter(item ->
                                item != subscription)
                        .findFirst()
                        .orElseThrow();

        assertEquals(
                customer,
                newSubscription.getCustomer());

        assertEquals(
                latestVersion,
                newSubscription.getPlanVersion());

        assertEquals(
                "ACTIVE",
                newSubscription.getStatus());

        assertTrue(
                newSubscription.getAutoRenew());

        LocalDate expectedStartDate =
                subscription.getEndDate()
                        .plusDays(1);

        LocalDate expectedEndDate =
                expectedStartDate
                        .plusYears(1)
                        .minusDays(1);

        assertEquals(
                expectedStartDate,
                newSubscription.getStartDate());

        assertEquals(
                expectedEndDate,
                newSubscription.getEndDate());
    }

    @Test
    void autoRenewSubscriptions_ShouldCreateCorrectRenewalRecord() {

        UUID planId =
                UUID.randomUUID();

        CustomerEntity customer =
                new CustomerEntity();

        customer.setCustomerId(
                UUID.randomUUID());

        SubscriptionPlanEntity plan =
                new SubscriptionPlanEntity();

        plan.setSubscriptionPlanId(
                planId);

        SubscriptionPlanVersionEntity currentVersion =
                new SubscriptionPlanVersionEntity();

        currentVersion.setSubscriptionPlan(
                plan);

        SubscriptionPlanVersionEntity latestVersion =
                new SubscriptionPlanVersionEntity();

        latestVersion.setSubscriptionPlan(
                plan);

        latestVersion.setPrice(
                new BigDecimal("12000.00"));

        CustomerSubscriptionEntity subscription =
                new CustomerSubscriptionEntity();

        subscription.setCustomerSubscriptionId(
                UUID.randomUUID());

        subscription.setCustomer(
                customer);

        subscription.setPlanVersion(
                currentVersion);

        subscription.setEndDate(
                LocalDate.now());

        when(customerSubscriptionRepository
                .findByAutoRenewTrueAndStatus(
                        "ACTIVE"))
                .thenReturn(
                        List.of(subscription));

        when(planVersionRepository
                .findTopBySubscriptionPlan_SubscriptionPlanIdOrderByVersionNumberDesc(
                        planId))
                .thenReturn(
                        Optional.of(latestVersion));

        ArgumentCaptor<SubscriptionRenewalEntity>
                renewalCaptor =
                ArgumentCaptor.forClass(
                        SubscriptionRenewalEntity.class);

        scheduler.autoRenewSubscriptions();

        verify(renewalRepository)
                .save(
                        renewalCaptor.capture());

        SubscriptionRenewalEntity renewal =
                renewalCaptor.getValue();

        assertEquals(
                subscription,
                renewal.getCustomerSubscription());

        assertEquals(
                "AUTO",
                renewal.getRenewalType());

        assertEquals(
                new BigDecimal("12000.00"),
                renewal.getRenewalAmount());

        assertEquals(
                "SUCCESS",
                renewal.getStatus());

        assertEquals(
                "Auto renewed by scheduler",
                renewal.getRemarks());

        assertEquals(
                LocalDate.now(),
                renewal.getRenewalDate());

        assertEquals(
                subscription.getEndDate()
                        .plusDays(1),
                renewal.getPeriodStart());
    }

    @Test
    void autoRenewSubscriptions_ShouldHandleMissingLatestPlanVersion() {

        UUID planId =
                UUID.randomUUID();

        SubscriptionPlanEntity plan =
                new SubscriptionPlanEntity();

        plan.setSubscriptionPlanId(
                planId);

        SubscriptionPlanVersionEntity currentVersion =
                new SubscriptionPlanVersionEntity();

        currentVersion.setSubscriptionPlan(
                plan);

        CustomerSubscriptionEntity subscription =
                new CustomerSubscriptionEntity();

        subscription.setCustomerSubscriptionId(
                UUID.randomUUID());

        subscription.setPlanVersion(
                currentVersion);

        subscription.setEndDate(
                LocalDate.now());

        when(customerSubscriptionRepository
                .findByAutoRenewTrueAndStatus(
                        "ACTIVE"))
                .thenReturn(
                        List.of(subscription));

        when(planVersionRepository
                .findTopBySubscriptionPlan_SubscriptionPlanIdOrderByVersionNumberDesc(
                        planId))
                .thenReturn(
                        Optional.empty());

        scheduler.autoRenewSubscriptions();

        verify(planVersionRepository)
                .findTopBySubscriptionPlan_SubscriptionPlanIdOrderByVersionNumberDesc(
                        planId);

        verify(renewalRepository,
                never())
                .save(any());

        verify(customerSubscriptionRepository,
                never())
                .save(any());
    }

    @Test
    void autoRenewSubscriptions_ShouldContinue_WhenOneSubscriptionFails() {

        CustomerSubscriptionEntity failingSubscription =
                new CustomerSubscriptionEntity();

        failingSubscription.setCustomerSubscriptionId(
                UUID.randomUUID());

        failingSubscription.setEndDate(
                LocalDate.now());

        CustomerSubscriptionEntity skippedSubscription =
                new CustomerSubscriptionEntity();

        skippedSubscription.setCustomerSubscriptionId(
                UUID.randomUUID());

        skippedSubscription.setEndDate(
                LocalDate.now().plusDays(2));

        when(customerSubscriptionRepository
                .findByAutoRenewTrueAndStatus(
                        "ACTIVE"))
                .thenReturn(
                        List.of(
                                failingSubscription,
                                skippedSubscription));

        scheduler.autoRenewSubscriptions();

        verify(customerSubscriptionRepository)
                .findByAutoRenewTrueAndStatus(
                        "ACTIVE");

        verifyNoInteractions(
                renewalRepository);
    }
}