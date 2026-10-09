package com.propertypilot.application.service;

import com.propertypilot.infrastructure.persistence.entity.ServiceRequestEntity;

public interface CompletionValidationService {

    void validateCompletionRequirements(
            ServiceRequestEntity request);

    boolean isCompletionReady(ServiceRequestEntity request);
}
