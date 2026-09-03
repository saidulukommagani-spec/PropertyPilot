package com.propertypilot.application.service;

import com.propertypilot.application.dto.PropertyCreateRequest;
import com.propertypilot.application.dto.PropertyResponse;
import java.util.UUID;

public interface PropertyService {

    PropertyResponse createProperty(
            PropertyCreateRequest request);

    PropertyResponse getPropertyById(
            UUID propertyId);
}