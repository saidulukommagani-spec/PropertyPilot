package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateCustomerPreferenceRequest;
import com.propertypilot.application.dto.CustomerPreferenceResponse;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.CustomerPreferenceEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerJpaRepository;
import com.propertypilot.infrastructure.persistence.repository.CustomerPreferenceRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerPreferenceServiceImplTest {

    @Mock
    private CustomerPreferenceRepository
            customerPreferenceRepository;

    @Mock
    private CustomerJpaRepository
            customerRepository;

    @InjectMocks
    private CustomerPreferenceServiceImpl service;

    private CustomerEntity buildCustomer() {

        CustomerEntity customer =
                new CustomerEntity();

        customer.setCustomerId(
                UUID.randomUUID());

        return customer;
    }

    private CustomerPreferenceEntity buildPreference() {

        CustomerEntity customer =
                buildCustomer();

        CustomerPreferenceEntity entity =
                new CustomerPreferenceEntity();

        entity.setCustomerPreferenceId(
                UUID.randomUUID());

        entity.setCustomer(
                customer);

        entity.setPreferredLanguage(
                "en");

        entity.setPreferredTimezone(
                "Asia/Kolkata");

        entity.setCommunicationChannels(
        "EMAIL,SMS");

entity.setPreferenceData(
        "{\"theme\":\"dark\"}");

        entity.setMarketingConsent(
                true);

        return entity;
    }

    @Test
    void savePreference_ShouldCreatePreference() {

        CustomerEntity customer =
                buildCustomer();

        UUID customerId =
                customer.getCustomerId();

        CreateCustomerPreferenceRequest request =
                new CreateCustomerPreferenceRequest();

        request.setPreferredLanguage(
                "en");

        request.setPreferredTimezone(
                "Asia/Kolkata");

        request.setCommunicationChannels(
        "EMAIL");

request.setPreferenceData(
        "{\"theme\":\"dark\"}");

        request.setMarketingConsent(
                true);

        CustomerPreferenceEntity saved =
                buildPreference();

        when(customerRepository.findById(customerId))
                .thenReturn(
                        Optional.of(customer));

        when(customerPreferenceRepository
                .findByCustomerCustomerId(customerId))
                .thenReturn(
                        Optional.empty());

        when(customerPreferenceRepository.save(any()))
                .thenReturn(
                        saved);

        CustomerPreferenceResponse response =
                service.savePreference(
                        customerId,
                        request);

        assertThat(response)
                .isNotNull();

        verify(customerPreferenceRepository)
                .save(any());
    }

    @Test
    void savePreference_ShouldUpdateExistingPreference() {

        CustomerEntity customer =
                buildCustomer();

        UUID customerId =
                customer.getCustomerId();

        CustomerPreferenceEntity existing =
                buildPreference();

        CreateCustomerPreferenceRequest request =
                new CreateCustomerPreferenceRequest();

        request.setPreferredLanguage(
                "te");

        request.setPreferredTimezone(
                "Asia/Kolkata");

        request.setCommunicationChannels(
        "EMAIL");

        request.setMarketingConsent(
                true);

        when(customerRepository.findById(customerId))
                .thenReturn(
                        Optional.of(customer));

        when(customerPreferenceRepository
                .findByCustomerCustomerId(customerId))
                .thenReturn(
                        Optional.of(existing));

        when(customerPreferenceRepository.save(any()))
                .thenReturn(
                        existing);

        CustomerPreferenceResponse response =
                service.savePreference(
                        customerId,
                        request);

        assertThat(response)
                .isNotNull();

        verify(customerPreferenceRepository)
                .save(any());
    }

    @Test
    void savePreference_ShouldThrow_WhenCustomerNotFound() {

        UUID customerId =
                UUID.randomUUID();

        CreateCustomerPreferenceRequest request =
                new CreateCustomerPreferenceRequest();

        when(customerRepository.findById(customerId))
                .thenReturn(
                        Optional.empty());

        assertThatThrownBy(
                () -> service.savePreference(
                        customerId,
                        request))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }

    @Test
    void getPreference_ShouldReturnPreference() {

        UUID customerId =
                UUID.randomUUID();

        CustomerPreferenceEntity entity =
                buildPreference();

        when(customerPreferenceRepository
                .findByCustomerCustomerId(customerId))
                .thenReturn(
                        Optional.of(entity));

        CustomerPreferenceResponse response =
                service.getPreference(
                        customerId);

        assertThat(response)
                .isNotNull();
    }

    @Test
    void getPreference_ShouldThrow_WhenNotFound() {

        UUID customerId =
                UUID.randomUUID();

        when(customerPreferenceRepository
                .findByCustomerCustomerId(customerId))
                .thenReturn(
                        Optional.empty());

        assertThatThrownBy(
                () -> service.getPreference(
                        customerId))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }
}