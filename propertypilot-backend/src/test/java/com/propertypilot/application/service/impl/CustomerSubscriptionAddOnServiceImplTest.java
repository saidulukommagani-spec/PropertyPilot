package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateCustomerSubscriptionAddOnRequest;
import com.propertypilot.application.dto.CustomerSubscriptionAddOnResponse;
import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionAddOnEntity;
import com.propertypilot.infrastructure.persistence.entity.CustomerSubscriptionEntity;
import com.propertypilot.infrastructure.persistence.entity.SubscriptionAddOnEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionAddOnRepository;
import com.propertypilot.infrastructure.persistence.repository.CustomerSubscriptionRepository;
import com.propertypilot.infrastructure.persistence.repository.SubscriptionAddOnRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CustomerSubscriptionAddOnServiceImplTest {

    @Mock
    private CustomerSubscriptionRepository
            customerSubscriptionRepository;

    @Mock
    private SubscriptionAddOnRepository
            subscriptionAddOnRepository;

    @Mock
    private CustomerSubscriptionAddOnRepository
            customerSubscriptionAddOnRepository;

    @InjectMocks
    private CustomerSubscriptionAddOnServiceImpl service;

    private CustomerSubscriptionAddOnEntity buildEntity() {

        UUID subscriptionId =
                UUID.randomUUID();

        UUID addOnId =
                UUID.randomUUID();

        CustomerSubscriptionEntity subscription =
                new CustomerSubscriptionEntity();

        subscription.setCustomerSubscriptionId(
                subscriptionId);

        SubscriptionAddOnEntity addOn =
                new SubscriptionAddOnEntity();

        addOn.setSubscriptionAddOnId(
                addOnId);

        addOn.setName(
                "Premium Listing");

        CustomerSubscriptionAddOnEntity entity =
                new CustomerSubscriptionAddOnEntity();

        entity.setCustomerSubscriptionAddOnId(
                UUID.randomUUID());

        entity.setCustomerSubscription(
                subscription);

        entity.setSubscriptionAddOn(
                addOn);

        entity.setQuantity(2);

        entity.setStatus("ACTIVE");

        entity.setStartsAt(
                OffsetDateTime.now());

        entity.setEndsAt(
                OffsetDateTime.now().plusDays(30));

        entity.setPricingEstimateId(
                UUID.randomUUID());

        return entity;
    }

    @Test
    void createAddOn_ShouldCreateAddOn() {

        UUID subscriptionId =
                UUID.randomUUID();

        UUID addOnId =
                UUID.randomUUID();

        CustomerSubscriptionEntity subscription =
                new CustomerSubscriptionEntity();

        subscription.setCustomerSubscriptionId(
                subscriptionId);

        SubscriptionAddOnEntity addOn =
                new SubscriptionAddOnEntity();

        addOn.setSubscriptionAddOnId(
                addOnId);

        addOn.setName(
                "Premium Listing");

        CreateCustomerSubscriptionAddOnRequest request =
                new CreateCustomerSubscriptionAddOnRequest();

        request.setSubscriptionAddOnId(
                addOnId);

        request.setQuantity(2);

        request.setPricingEstimateId(
                UUID.randomUUID());

        request.setStartsAt(
                OffsetDateTime.now());

        request.setEndsAt(
                OffsetDateTime.now().plusDays(30));

        CustomerSubscriptionAddOnEntity saved =
                buildEntity();

        when(customerSubscriptionRepository
                .findById(subscriptionId))
                .thenReturn(Optional.of(subscription));

        when(subscriptionAddOnRepository
                .findById(addOnId))
                .thenReturn(Optional.of(addOn));

        when(customerSubscriptionAddOnRepository
                .save(any()))
                .thenReturn(saved);

        CustomerSubscriptionAddOnResponse response =
                service.createAddOn(
                        subscriptionId,
                        request);

        assertThat(response)
                .isNotNull();

        verify(customerSubscriptionAddOnRepository)
                .save(any());
    }

    @Test
    void createAddOn_ShouldThrow_WhenSubscriptionNotFound() {

        UUID subscriptionId =
                UUID.randomUUID();

        CreateCustomerSubscriptionAddOnRequest request =
                new CreateCustomerSubscriptionAddOnRequest();

        when(customerSubscriptionRepository
                .findById(subscriptionId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.createAddOn(
                        subscriptionId,
                        request))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }

    @Test
    void createAddOn_ShouldThrow_WhenAddOnNotFound() {

        UUID subscriptionId =
                UUID.randomUUID();

        UUID addOnId =
                UUID.randomUUID();

        CustomerSubscriptionEntity subscription =
                new CustomerSubscriptionEntity();

        CreateCustomerSubscriptionAddOnRequest request =
                new CreateCustomerSubscriptionAddOnRequest();

        request.setSubscriptionAddOnId(
                addOnId);

        when(customerSubscriptionRepository
                .findById(subscriptionId))
                .thenReturn(Optional.of(subscription));

        when(subscriptionAddOnRepository
                .findById(addOnId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.createAddOn(
                        subscriptionId,
                        request))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }

    @Test
    void getAddOn_ShouldReturnAddOn() {

        UUID id =
                UUID.randomUUID();

        CustomerSubscriptionAddOnEntity entity =
                buildEntity();

        when(customerSubscriptionAddOnRepository
                .findById(id))
                .thenReturn(Optional.of(entity));

        CustomerSubscriptionAddOnResponse response =
                service.getAddOn(id);

        assertThat(response)
                .isNotNull();
    }

    @Test
    void getAddOn_ShouldThrow_WhenNotFound() {

        UUID id =
                UUID.randomUUID();

        when(customerSubscriptionAddOnRepository
                .findById(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.getAddOn(id))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }

    @Test
    void getSubscriptionAddOns_ShouldReturnList() {

        UUID subscriptionId =
                UUID.randomUUID();

        when(customerSubscriptionAddOnRepository
                .findByCustomerSubscriptionCustomerSubscriptionId(
                        subscriptionId))
                .thenReturn(
                        List.of(buildEntity()));

        List<CustomerSubscriptionAddOnResponse> result =
                service.getSubscriptionAddOns(
                        subscriptionId);

        assertThat(result)
                .hasSize(1);
    }

    @Test
    void deleteAddOn_ShouldDelete() {

        UUID id =
                UUID.randomUUID();

        CustomerSubscriptionAddOnEntity entity =
                buildEntity();

        when(customerSubscriptionAddOnRepository
                .findById(id))
                .thenReturn(Optional.of(entity));

        service.deleteAddOn(id);

        verify(customerSubscriptionAddOnRepository)
                .delete(entity);
    }

    @Test
    void deleteAddOn_ShouldThrow_WhenNotFound() {

        UUID id =
                UUID.randomUUID();

        when(customerSubscriptionAddOnRepository
                .findById(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.deleteAddOn(id))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }
}