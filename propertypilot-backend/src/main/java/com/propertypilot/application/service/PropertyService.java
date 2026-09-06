package com.propertypilot.application.service;

import com.propertypilot.application.dto.PropertyCreateRequest;
import com.propertypilot.application.dto.PropertyResponse;
import com.propertypilot.application.dto.PropertyUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface PropertyService {

    PropertyResponse createProperty(
            PropertyCreateRequest request);

    PropertyResponse getPropertyById(
            UUID propertyId);

    /*
     * Active properties for customers.
     * All properties for admins.
     */
    List<PropertyResponse> getAllProperties();

    /*
     * Archived properties.
     * Admin only.
     */
    List<PropertyResponse> getArchivedProperties();

    PropertyResponse updateProperty(
            UUID propertyId,
            PropertyUpdateRequest request);

    /*
     * Legacy delete endpoint.
     * Internally archives the property.
     */
    void deleteProperty(
            UUID propertyId);

    PropertyResponse archiveProperty(
            UUID propertyId);
            PropertyResponse restoreProperty(
        UUID propertyId);
        
            
}