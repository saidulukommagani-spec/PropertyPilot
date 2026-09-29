package com.propertypilot.application.service;

import java.util.UUID;

public interface EligibilityEngine {

    void validateEligibility(
            UUID serviceId,
            UUID propertyId);
}