package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.PropertyCreateRequest;
import com.propertypilot.application.dto.PropertyResponse;
import com.propertypilot.application.dto.PropertyUpdateRequest;
import com.propertypilot.application.service.PropertyService;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.Property;
import com.propertypilot.infrastructure.persistence.repository.CustomerRepository;
import com.propertypilot.infrastructure.persistence.repository.PropertyRepository;
import com.propertypilot.security.SecurityService;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.time.Instant;

@Service
public class PropertyServiceImpl
        implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final CustomerRepository customerRepository;
    private final SecurityService securityService;

    public PropertyServiceImpl(
            PropertyRepository propertyRepository,
            CustomerRepository customerRepository,
            SecurityService securityService) {

        this.propertyRepository = propertyRepository;
        this.customerRepository = customerRepository;
        this.securityService = securityService;
    }

    @Override
    public PropertyResponse createProperty(
            PropertyCreateRequest request) {

        CustomerEntity customer =
                securityService.getCurrentCustomer();

        Property property = new Property();

        property.setCustomer(customer);

        property.setTitle(
                request.getTitle());

        property.setPropertyType(
                request.getPropertyType());

        property.setListingStatus(
                request.getListingStatus());

        property.setPrice(
                request.getPrice());

        property.setStatus(
                request.getStatus());

        Property savedProperty =
                propertyRepository.save(property);

        return buildResponse(savedProperty);
    }

    @Override
    public PropertyResponse getPropertyById(
            UUID propertyId) {

        Property property =
                propertyRepository.findById(propertyId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Property not found"));

        validatePropertyOwnership(property);

        /*
         * Admin can view archived properties.
         * Customers cannot.
         */
        if (!securityService.isAdmin()
                && "ARCHIVED".equalsIgnoreCase(
                        property.getStatus())) {

            throw new ResourceNotFoundException(
                    "Property not found");
        }

        return buildResponse(property);
    }

    @Override
    public List<PropertyResponse> getAllProperties() {

        /*
         * Admin sees everything.
         */
        if (securityService.isAdmin()) {

            return propertyRepository.findAll()
                    .stream()
                    .map(this::buildResponse)
                    .toList();
        }

        UUID currentCustomerId =
                securityService.getCurrentCustomer()
                        .getCustomerId();

        return propertyRepository.findAll()
                .stream()
                 .filter(property ->
                  property.getCustomer()
                        .getCustomerId()
                        .equals(currentCustomerId))
                .map(this::buildResponse)
                 .toList();
    }
@Override
public List<PropertyResponse> getArchivedProperties() {

    if (!securityService.isAdmin()) {

        throw new AccessDeniedException(
                "Only administrators can view archived properties");
    }

    return propertyRepository
            .findByStatus("ARCHIVED")
            .stream()
            .map(this::buildResponse)
            .toList();
}
    @Override
    public PropertyResponse updateProperty(
            UUID propertyId,
            PropertyUpdateRequest request) {

        Property property =
                propertyRepository.findById(propertyId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Property not found"));

        validatePropertyOwnership(property);

        /*
         * Archived properties cannot be modified.
         */
        if ("ARCHIVED".equalsIgnoreCase(
                property.getStatus())) {

            throw new IllegalStateException(
                    "Archived properties cannot be updated");
        }

        property.setTitle(
                request.getTitle());

        property.setPropertyType(
                request.getPropertyType());

        property.setListingStatus(
                request.getListingStatus());

        property.setPrice(
                request.getPrice());

        property.setStatus(
                request.getStatus());

        Property updatedProperty =
                propertyRepository.save(property);

        return buildResponse(updatedProperty);
    }

    /*
     * Legacy DELETE endpoint.
     * Convert delete into archive.
     */
    @Override
    public void deleteProperty(
            UUID propertyId) {

        archiveProperty(propertyId);
    }

@Override
public PropertyResponse archiveProperty(
        UUID propertyId) {

    Property property =
            propertyRepository.findById(propertyId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Property not found"));

    validatePropertyOwnership(property);

    if ("ARCHIVED".equalsIgnoreCase(
            property.getStatus())) {

        throw new IllegalStateException(
                "Property is already archived");
    }

    property.setStatus("ARCHIVED");

    property.setArchivedAt(
            Instant.now());

    if (securityService.isAdmin()) {

        property.setArchivedBy(null);

    } else {

        property.setArchivedBy(
                securityService.getCurrentCustomer()
                        .getCustomerId());
    }

    Property archivedProperty =
            propertyRepository.save(property);

    return buildResponse(
            archivedProperty);
}
    @Override
public PropertyResponse restoreProperty(
        UUID propertyId) {

    if (!securityService.isAdmin()) {

        throw new AccessDeniedException(
                "Only admin can restore properties");
    }

    
    Property property =
            propertyRepository.findById(propertyId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Property not found"));
           if (!"ARCHIVED".equalsIgnoreCase(
            property.getStatus())) {

        throw new IllegalStateException(
                "Property is not archived");
    }
    property.setStatus("AVAILABLE");

    /*
     * Optional:
     * clear archive audit fields
     */
    property.setArchivedAt(null);
    property.setArchivedBy(null);

    Property restoredProperty =
            propertyRepository.save(property);

    return buildResponse(
            restoredProperty);
}

    private void validatePropertyOwnership(
            Property property) {

        if (securityService.isAdmin()) {
            return;
        }

        UUID loggedInCustomerId =
                securityService.getCurrentCustomer()
                        .getCustomerId();

        UUID propertyCustomerId =
                property.getCustomer()
                        .getCustomerId();

        if (!loggedInCustomerId.equals(
                propertyCustomerId)) {

            throw new AccessDeniedException(
                    "You do not have permission to access this property");
        }
    }

    private PropertyResponse buildResponse(
            Property property) {

        PropertyResponse response =
                new PropertyResponse();

        response.setPropertyId(
                property.getPropertyId());

        response.setCustomerId(
                property.getCustomer()
                        .getCustomerId());

        response.setTitle(
                property.getTitle());

        response.setPropertyType(
                property.getPropertyType());

        response.setListingStatus(
                property.getListingStatus());

        response.setPrice(
                property.getPrice());

        response.setStatus(
                property.getStatus());

        return response;
    }
}