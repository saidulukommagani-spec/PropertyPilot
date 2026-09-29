package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreatePropertyOwnerRequest;
import com.propertypilot.application.dto.PropertyOwnerResponse;
import com.propertypilot.application.dto.UpdatePropertyOwnerRequest;

import java.util.List;
import java.util.UUID;

public interface PropertyOwnerService {

    PropertyOwnerResponse createOwner(
            CreatePropertyOwnerRequest request);

    PropertyOwnerResponse getOwner(
            UUID propertyOwnerId);

    List<PropertyOwnerResponse> getOwnersByProperty(
            UUID propertyId);

    PropertyOwnerResponse updateOwner(
            UUID propertyOwnerId,
            UpdatePropertyOwnerRequest request);

    void deleteOwner(
            UUID propertyOwnerId);
}