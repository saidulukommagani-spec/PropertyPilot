package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreateCustomerPreferenceRequest;
import com.propertypilot.application.dto.CustomerPreferenceResponse;

import java.util.UUID;

public interface CustomerPreferenceService {

    CustomerPreferenceResponse savePreference(
            UUID customerId,
            CreateCustomerPreferenceRequest request);

    CustomerPreferenceResponse getPreference(
            UUID customerId);
}