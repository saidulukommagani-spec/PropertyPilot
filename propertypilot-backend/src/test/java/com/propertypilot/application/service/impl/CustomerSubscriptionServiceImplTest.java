package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateCustomerSubscriptionRequest;
import com.propertypilot.application.dto.CustomerSubscriptionResponse;
import com.propertypilot.application.dto.UpdateCustomerSubscriptionRequest;
import com.propertypilot.application.service.BillingService;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionPlanVersionEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerJpaRepository;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionPlanVersionRepository;
import com.propertypilot.web.exception.BusinessException;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerSubscriptionServiceImplTest {

    @Mock
    private CustomerSubscriptionRepository
            customerSubscriptionRepository;

    @Mock
    private CustomerJpaRepository
            customerRepository;

    @Mock
    private SubscriptionPlanVersionRepository
            subscriptionPlanVersionRepository;

    @Mock
    private BillingService billingService;

    @InjectMocks
    private CustomerSubscriptionServiceImpl service;

    private CustomerEntity buildCustomer() {

        CustomerEntity customer =
                new CustomerEntity();

        customer.setCustomerId(
                UUID.randomUUID());

        return customer;
    }

    private SubscriptionPlanVersionEntity buildPlanVersion(
            String status,
            BigDecimal price) {

        SubscriptionPlanVersionEntity entity =
                new SubscriptionPlanVersionEntity();

        entity.setPlanVersionId(
                UUID.randomUUID());

        entity.setStatus(
                status);

        entity.setPrice(
                price);

        return entity;
    }

    private CustomerSubscriptionEntity buildSubscription() {

        CustomerSubscriptionEntity entity =
                new CustomerSubscriptionEntity();

        entity.setCustomerSubscriptionId(
                UUID.randomUUID());

        entity.setCustomer(
                buildCustomer());

        entity.setPlanVersion(
                buildPlanVersion(
                        "ACTIVE",
                        BigDecimal.valueOf(999)));

        entity.setStatus(
                "ACTIVE");

        entity.setStartDate(
                LocalDate.now());

        entity.setEndDate(
                LocalDate.now().plusMonths(1));

        entity.setAutoRenew(
                true);

        return entity;
    }

    @Test
    void createSubscription_ShouldCreateSubscription() {

        UUID customerId =
                UUID.randomUUID();

        CustomerEntity customer =
                buildCustomer();

        SubscriptionPlanVersionEntity plan =
                buildPlanVersion(
                        "ACTIVE",
                        BigDecimal.valueOf(999));

        CreateCustomerSubscriptionRequest request =
                new CreateCustomerSubscriptionRequest();

        request.setPlanVersionId(
                plan.getPlanVersionId());

        request.setStartDate(
                LocalDate.now());

        request.setEndDate(
                LocalDate.now().plusMonths(1));

        request.setAutoRenew(
                true);

        CustomerSubscriptionEntity saved =
                buildSubscription();

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        when(subscriptionPlanVersionRepository.findById(
                plan.getPlanVersionId()))
                .thenReturn(Optional.of(plan));

        when(customerSubscriptionRepository.save(any()))
                .thenReturn(saved);

        CustomerSubscriptionResponse response =
                service.createSubscription(
                        customerId,
                        request);

        assertThat(response)
                .isNotNull();

        verify(billingService)
                .createInvoice(
                        any(),
                        any(),
                        any(),
                        any(),
                        any(),
                        anyString());
    }

    @Test
    void createSubscription_ShouldCreateSubscriptionWithoutInvoice() {

        UUID customerId =
                UUID.randomUUID();

        CustomerEntity customer =
                buildCustomer();

        SubscriptionPlanVersionEntity plan =
                buildPlanVersion(
                        "ACTIVE",
                        BigDecimal.ZERO);

        CreateCustomerSubscriptionRequest request =
                new CreateCustomerSubscriptionRequest();

        request.setPlanVersionId(
                plan.getPlanVersionId());

        request.setStartDate(
                LocalDate.now());

        request.setEndDate(
                LocalDate.now().plusMonths(1));

        CustomerSubscriptionEntity saved =
                buildSubscription();

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        when(subscriptionPlanVersionRepository.findById(
                plan.getPlanVersionId()))
                .thenReturn(Optional.of(plan));

        when(customerSubscriptionRepository.save(any()))
                .thenReturn(saved);

        service.createSubscription(
                customerId,
                request);

        verify(billingService,
                never())
                .createInvoice(
                        any(),
                        any(),
                        any(),
                        any(),
                        any(),
                        anyString());
    }

    @Test
    void createSubscription_ShouldThrow_WhenCustomerNotFound() {

        UUID customerId =
                UUID.randomUUID();

        CreateCustomerSubscriptionRequest request =
                new CreateCustomerSubscriptionRequest();

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> service.createSubscription(
                        customerId,
                        request))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }

    @Test
    void createSubscription_ShouldThrow_WhenPlanVersionNotFound() {

        UUID customerId =
                UUID.randomUUID();

        UUID planId =
                UUID.randomUUID();

        CreateCustomerSubscriptionRequest request =
                new CreateCustomerSubscriptionRequest();

        request.setPlanVersionId(
                planId);

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(
                        buildCustomer()));

        when(subscriptionPlanVersionRepository.findById(planId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> service.createSubscription(
                        customerId,
                        request))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }

    @Test
    void createSubscription_ShouldThrow_WhenEndDateBeforeStartDate() {

        UUID customerId =
                UUID.randomUUID();

        SubscriptionPlanVersionEntity plan =
                buildPlanVersion(
                        "ACTIVE",
                        BigDecimal.TEN);

        CreateCustomerSubscriptionRequest request =
                new CreateCustomerSubscriptionRequest();

        request.setPlanVersionId(
                plan.getPlanVersionId());

        request.setStartDate(
                LocalDate.now());

        request.setEndDate(
                LocalDate.now().minusDays(1));

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(
                        buildCustomer()));

        when(subscriptionPlanVersionRepository.findById(
                plan.getPlanVersionId()))
                .thenReturn(Optional.of(plan));

        assertThatThrownBy(
                () -> service.createSubscription(
                        customerId,
                        request))
                .isInstanceOf(
                        BusinessException.class);
    }

    @Test
    void createSubscription_ShouldThrow_WhenPlanNotActive() {

        UUID customerId =
                UUID.randomUUID();

        SubscriptionPlanVersionEntity plan =
                buildPlanVersion(
                        "DRAFT",
                        BigDecimal.TEN);

        CreateCustomerSubscriptionRequest request =
                new CreateCustomerSubscriptionRequest();

        request.setPlanVersionId(
                plan.getPlanVersionId());

        request.setStartDate(
                LocalDate.now());

        request.setEndDate(
                LocalDate.now().plusDays(10));

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(
                        buildCustomer()));

        when(subscriptionPlanVersionRepository.findById(
                plan.getPlanVersionId()))
                .thenReturn(Optional.of(plan));

        assertThatThrownBy(
                () -> service.createSubscription(
                        customerId,
                        request))
                .isInstanceOf(
                        BusinessException.class);
    }

    @Test
    void getSubscription_ShouldReturnSubscription() {

        UUID id =
                UUID.randomUUID();

        CustomerSubscriptionEntity entity =
                buildSubscription();

        when(customerSubscriptionRepository.findById(id))
                .thenReturn(Optional.of(entity));

        CustomerSubscriptionResponse response =
                service.getSubscription(id);

        assertThat(response)
                .isNotNull();
    }

    @Test
    void getSubscription_ShouldThrow_WhenNotFound() {

        UUID id =
                UUID.randomUUID();

        when(customerSubscriptionRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> service.getSubscription(id))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }

    @Test
    void getCustomerSubscriptions_ShouldReturnList() {

        UUID customerId =
                UUID.randomUUID();

        when(customerSubscriptionRepository
                .findByCustomer_CustomerId(customerId))
                .thenReturn(
                        List.of(buildSubscription()));

        List<CustomerSubscriptionResponse> result =
                service.getCustomerSubscriptions(customerId);

        assertThat(result)
                .hasSize(1);
    }

    @Test
    void updateSubscription_ShouldUpdateStatus() {

        UUID id =
                UUID.randomUUID();

        CustomerSubscriptionEntity entity =
                buildSubscription();

        UpdateCustomerSubscriptionRequest request =
                new UpdateCustomerSubscriptionRequest();

        request.setStatus(
                "EXPIRED");

        when(customerSubscriptionRepository.findById(id))
                .thenReturn(Optional.of(entity));

        when(customerSubscriptionRepository.save(any()))
                .thenReturn(entity);

        CustomerSubscriptionResponse response =
                service.updateSubscription(
                        id,
                        request);

        assertThat(response)
                .isNotNull();
    }

    @Test
    void updateSubscription_ShouldThrow_WhenInvalidStatus() {

        UUID id =
                UUID.randomUUID();

        CustomerSubscriptionEntity entity =
                buildSubscription();

        UpdateCustomerSubscriptionRequest request =
                new UpdateCustomerSubscriptionRequest();

        request.setStatus(
                "INVALID");

        when(customerSubscriptionRepository.findById(id))
                .thenReturn(Optional.of(entity));

        assertThatThrownBy(
                () -> service.updateSubscription(
                        id,
                        request))
                .isInstanceOf(
                        IllegalArgumentException.class);
    }

    @Test
    void updateSubscription_ShouldThrow_WhenEndDateBeforeStartDate() {

        UUID id =
                UUID.randomUUID();

        CustomerSubscriptionEntity entity =
                buildSubscription();

        UpdateCustomerSubscriptionRequest request =
                new UpdateCustomerSubscriptionRequest();

        request.setEndDate(
                entity.getStartDate().minusDays(1));

        when(customerSubscriptionRepository.findById(id))
                .thenReturn(Optional.of(entity));

        assertThatThrownBy(
                () -> service.updateSubscription(
                        id,
                        request))
                .isInstanceOf(
                        IllegalArgumentException.class);
    }

    @Test
    void updateSubscription_ShouldUpdateAutoRenew() {

        UUID id =
                UUID.randomUUID();

        CustomerSubscriptionEntity entity =
                buildSubscription();

        UpdateCustomerSubscriptionRequest request =
                new UpdateCustomerSubscriptionRequest();

        request.setAutoRenew(false);

        when(customerSubscriptionRepository.findById(id))
                .thenReturn(Optional.of(entity));

        when(customerSubscriptionRepository.save(any()))
                .thenReturn(entity);

        CustomerSubscriptionResponse response =
                service.updateSubscription(
                        id,
                        request);

        assertThat(response)
                .isNotNull();
    }

    @Test
    void getExpiringSubscriptions_ShouldReturnList() {

        when(customerSubscriptionRepository
                .findByStatusAndEndDateBetween(
                        eq("ACTIVE"),
                        any(LocalDate.class),
                        any(LocalDate.class)))
                .thenReturn(
                        List.of(buildSubscription()));

        List<CustomerSubscriptionResponse> result =
                service.getExpiringSubscriptions(30);

        assertThat(result)
                .hasSize(1);
    }
}