package com.propertypilot.application.service;

import com.propertypilot.application.dto.PropertyCreateRequest;
import com.propertypilot.application.dto.PropertyResponse;

public interface PropertyService {

    PropertyResponse createProperty(
            PropertyCreateRequest request);
}