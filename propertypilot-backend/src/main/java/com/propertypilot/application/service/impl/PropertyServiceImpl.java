package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.PropertyCreateRequest;
import com.propertypilot.application.dto.PropertyResponse;
import com.propertypilot.application.service.PropertyService;
import com.propertypilot.domain.entity.Property;
import com.propertypilot.infrastructure.persistence.repository.PropertyRepository;
import org.springframework.stereotype.Service;

@Service
public class PropertyServiceImpl
        implements PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyServiceImpl(
            PropertyRepository propertyRepository) {

        this.propertyRepository = propertyRepository;
    }

    @Override
    public PropertyResponse createProperty(
            PropertyCreateRequest request) {

        Property property = new Property();

        property.setPropertyName(
                request.getPropertyName());

        property.setPropertyType(
                request.getPropertyType());

        property.setAddressLine1(
                request.getAddressLine1());

        property.setAddressLine2(
                request.getAddressLine2());

        property.setCity(
                request.getCity());

        property.setState(
                request.getState());

        property.setPostalCode(
                request.getPostalCode());

        property.setCountry(
                request.getCountry());

        Property savedProperty =
                propertyRepository.save(property);

        PropertyResponse response =
                new PropertyResponse();

        response.setPropertyId(
                savedProperty.getPropertyId());

        response.setPropertyName(
                savedProperty.getPropertyName());

        response.setPropertyType(
                savedProperty.getPropertyType());

        response.setAddressLine1(
                savedProperty.getAddressLine1());

        response.setAddressLine2(
                savedProperty.getAddressLine2());

        response.setCity(
                savedProperty.getCity());

        response.setState(
                savedProperty.getState());

        response.setPostalCode(
                savedProperty.getPostalCode());

        response.setCountry(
                savedProperty.getCountry());

        return response;
    }
}