package com.propertypilot.application.service;

import com.propertypilot.infrastructure.persistence.entity.PricingProfileParameterEntity;

import java.util.UUID;

public interface PricingHistoryService {

    void recordChange(
            PricingProfileParameterEntity parameter,
            String oldValue,
            String newValue,
            UUID changedBy,
            String remarks);
}