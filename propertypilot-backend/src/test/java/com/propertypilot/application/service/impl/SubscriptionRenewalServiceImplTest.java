package com.propertypilot.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.propertypilot.application.dto.CreateSubscriptionRenewalRequest;
import com.propertypilot.application.dto.SubscriptionRenewalResponse;
import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanVersionEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionRenewalEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionPlanVersionRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionRenewalRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
@ExtendWith(MockitoExtension.class)
class SubscriptionRenewalServiceImplTest {

    @Mock
    private SubscriptionRenewalRepository renewalRepository;

    @Mock
    private CustomerSubscriptionRepository
            customerSubscriptionRepository;

    @Mock
    private SubscriptionPlanVersionRepository
            planVersionRepository;

    @InjectMocks
    private SubscriptionRenewalServiceImpl service;

private CustomerSubscriptionEntity
buildSubscription(UUID subscriptionId) {

    SubscriptionPlanEntity plan =
            new SubscriptionPlanEntity();

    plan.setSubscriptionPlanId(
            UUID.randomUUID());

    SubscriptionPlanVersionEntity version =
            new SubscriptionPlanVersionEntity();

    version.setSubscriptionPlan(plan);

    CustomerSubscriptionEntity subscription =
            new CustomerSubscriptionEntity();

    subscription.setCustomerSubscriptionId(
            subscriptionId);

    subscription.setPlanVersion(
            version);

    subscription.setAutoRenew(true);

    subscription.setStatus("ACTIVE");

    return subscription;
}

@Test
void renewSubscription_success() {

    UUID subscriptionId =
            UUID.randomUUID();

    UUID paymentId =
            UUID.randomUUID();

    CreateSubscriptionRenewalRequest request =
            new CreateSubscriptionRenewalRequest();

    request.setCustomerSubscriptionId(
            subscriptionId);

    request.setPaymentId(paymentId);

    request.setPeriodStart(
            LocalDate.now());

    request.setPeriodEnd(
            LocalDate.now().plusMonths(1));

    request.setRenewalType(
            "MANUAL");

    request.setRenewalAmount(
            BigDecimal.valueOf(999));

    request.setRemarks(
            "Renewed");

    CustomerSubscriptionEntity existing =
            buildSubscription(
                    subscriptionId);

    SubscriptionPlanVersionEntity latestVersion =
            new SubscriptionPlanVersionEntity();

    SubscriptionPlanEntity plan =
            new SubscriptionPlanEntity();

    plan.setSubscriptionPlanId(
            existing.getPlanVersion()
                    .getSubscriptionPlan()
                    .getSubscriptionPlanId());

    latestVersion.setSubscriptionPlan(
            plan);

    when(customerSubscriptionRepository.findById(
            subscriptionId))
            .thenReturn(Optional.of(existing));

    when(planVersionRepository
            .findTopBySubscriptionPlan_SubscriptionPlanIdOrderByVersionNumberDesc(
                    any()))
            .thenReturn(Optional.of(
                    latestVersion));

    when(customerSubscriptionRepository.save(
            any(CustomerSubscriptionEntity.class)))
            .thenAnswer(inv -> inv.getArgument(0));

    when(renewalRepository.save(
            any(SubscriptionRenewalEntity.class)))
            .thenAnswer(invocation -> {

                SubscriptionRenewalEntity entity =
                        invocation.getArgument(0);

                entity.setSubscriptionRenewalId(
                        UUID.randomUUID());

                return entity;
            });

    SubscriptionRenewalResponse response =
            service.renewSubscription(
                    request);

    assertThat(response)
            .isNotNull();

    assertThat(response.getStatus())
            .isEqualTo("SUCCESS");

    assertThat(existing.getStatus())
            .isEqualTo("EXPIRED");
}

@Test
void renewSubscription_customerSubscriptionNotFound() {

    UUID id =
            UUID.randomUUID();

    CreateSubscriptionRenewalRequest request =
            new CreateSubscriptionRenewalRequest();

    request.setCustomerSubscriptionId(id);

    when(customerSubscriptionRepository.findById(id))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.renewSubscription(request))
            .isInstanceOf(
                    ResourceNotFoundException.class)
            .hasMessage(
                    "Customer Subscription not found");
}

@Test
void renewSubscription_latestPlanVersionNotFound() {

    UUID subscriptionId =
            UUID.randomUUID();

    CreateSubscriptionRenewalRequest request =
            new CreateSubscriptionRenewalRequest();

    request.setCustomerSubscriptionId(
            subscriptionId);

    CustomerSubscriptionEntity existing =
            buildSubscription(
                    subscriptionId);

    when(customerSubscriptionRepository.findById(
            subscriptionId))
            .thenReturn(Optional.of(existing));

    when(planVersionRepository
            .findTopBySubscriptionPlan_SubscriptionPlanIdOrderByVersionNumberDesc(
                    any()))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.renewSubscription(request))
            .isInstanceOf(
                    ResourceNotFoundException.class)
            .hasMessage(
                    "Latest Plan Version not found");
}

@Test
void getRenewal_success() {

    UUID renewalId =
            UUID.randomUUID();

    SubscriptionRenewalEntity entity =
            new SubscriptionRenewalEntity();

    entity.setSubscriptionRenewalId(
            renewalId);

    CustomerSubscriptionEntity subscription =
            new CustomerSubscriptionEntity();

    subscription.setCustomerSubscriptionId(
            UUID.randomUUID());

    entity.setCustomerSubscription(
            subscription);

    entity.setStatus("SUCCESS");

    when(renewalRepository.findById(
            renewalId))
            .thenReturn(Optional.of(entity));

    SubscriptionRenewalResponse response =
            service.getRenewal(
                    renewalId);

    assertThat(response)
            .isNotNull();

    assertThat(response.getStatus())
            .isEqualTo("SUCCESS");
}

@Test
void getRenewal_notFound() {

    UUID renewalId =
            UUID.randomUUID();

    when(renewalRepository.findById(
            renewalId))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.getRenewal(
                    renewalId))
            .isInstanceOf(
                    ResourceNotFoundException.class)
            .hasMessage(
                    "Subscription Renewal not found");
}

@Test
void getRenewalsBySubscription_success() {

    UUID subscriptionId =
            UUID.randomUUID();

    SubscriptionRenewalEntity entity =
            new SubscriptionRenewalEntity();

    CustomerSubscriptionEntity subscription =
            new CustomerSubscriptionEntity();

    subscription.setCustomerSubscriptionId(
            subscriptionId);

    entity.setCustomerSubscription(
            subscription);

    entity.setStatus(
            "SUCCESS");

    when(renewalRepository
            .findByCustomerSubscription_CustomerSubscriptionId(
                    subscriptionId))
            .thenReturn(List.of(entity));

    List<SubscriptionRenewalResponse> result =
            service.getRenewalsBySubscription(
                    subscriptionId);

    assertThat(result)
            .hasSize(1);
}

@Test
void getRenewal_withNewSubscription() {

    UUID renewalId =
            UUID.randomUUID();

    SubscriptionRenewalEntity entity =
            new SubscriptionRenewalEntity();

    entity.setSubscriptionRenewalId(
            renewalId);

    CustomerSubscriptionEntity oldSub =
            new CustomerSubscriptionEntity();

    oldSub.setCustomerSubscriptionId(
            UUID.randomUUID());

    CustomerSubscriptionEntity newSub =
            new CustomerSubscriptionEntity();

    newSub.setCustomerSubscriptionId(
            UUID.randomUUID());

    entity.setCustomerSubscription(
            oldSub);

    entity.setNewCustomerSubscription(
            newSub);

    when(renewalRepository.findById(
            renewalId))
            .thenReturn(Optional.of(entity));

    SubscriptionRenewalResponse response =
            service.getRenewal(
                    renewalId);

    assertThat(
            response.getNewCustomerSubscriptionId())
            .isEqualTo(
                    newSub.getCustomerSubscriptionId());
}

@Test
void getRenewalsBySubscription_emptyList() {

    UUID subscriptionId =
            UUID.randomUUID();

    when(renewalRepository
            .findByCustomerSubscription_CustomerSubscriptionId(
                    subscriptionId))
            .thenReturn(List.of());

    List<SubscriptionRenewalResponse> result =
            service.getRenewalsBySubscription(
                    subscriptionId);

    assertThat(result)
            .isEmpty();
}





}
 