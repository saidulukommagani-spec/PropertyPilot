package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateCustomerPreferenceRequest;
import com.propertypilot.application.dto.CustomerPreferenceResponse;
import com.propertypilot.application.service.CustomerPreferenceService;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.CustomerPreferenceEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerJpaRepository;
import com.propertypilot.infrastructure.persistence.repository.CustomerPreferenceRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class CustomerPreferenceServiceImpl
        implements CustomerPreferenceService {

    private final CustomerPreferenceRepository customerPreferenceRepository;
    private final CustomerJpaRepository customerRepository;

    public CustomerPreferenceServiceImpl(
            CustomerPreferenceRepository customerPreferenceRepository,
            CustomerJpaRepository customerRepository) {

        this.customerPreferenceRepository =
                customerPreferenceRepository;

        this.customerRepository =
                customerRepository;
    }

    @Override
    public CustomerPreferenceResponse savePreference(
            UUID customerId,
            CreateCustomerPreferenceRequest request) {

        CustomerEntity customer =
                customerRepository.findById(customerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found"));

        CustomerPreferenceEntity preference =
                customerPreferenceRepository
                        .findByCustomerCustomerId(customerId)
                        .orElse(new CustomerPreferenceEntity());

        preference.setCustomer(customer);
        preference.setPreferredLanguage(
                request.getPreferredLanguage());

        preference.setPreferredTimezone(
                request.getPreferredTimezone());

        preference.setCommunicationChannels(
                request.getCommunicationChannels());

        preference.setPreferenceData(
                request.getPreferenceData());

        preference.setMarketingConsent(
                request.getMarketingConsent());

        CustomerPreferenceEntity saved =
                customerPreferenceRepository.save(preference);

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerPreferenceResponse getPreference(
            UUID customerId) {

        CustomerPreferenceEntity preference =
                customerPreferenceRepository
                        .findByCustomerCustomerId(customerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer preference not found"));

        return mapToResponse(preference);
    }

    private CustomerPreferenceResponse mapToResponse(
            CustomerPreferenceEntity entity) {

        CustomerPreferenceResponse response =
                new CustomerPreferenceResponse();

        response.setCustomerPreferenceId(
                entity.getCustomerPreferenceId());

        response.setCustomerId(
                entity.getCustomer().getCustomerId());

        response.setPreferredLanguage(
                entity.getPreferredLanguage());

        response.setPreferredTimezone(
                entity.getPreferredTimezone());

        response.setCommunicationChannels(
                entity.getCommunicationChannels());

        response.setPreferenceData(
                entity.getPreferenceData());

        response.setMarketingConsent(
                entity.getMarketingConsent());

        return response;
    }
}