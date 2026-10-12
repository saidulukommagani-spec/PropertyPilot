package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.PropertyCreateRequest;
import com.propertypilot.application.dto.PropertyResponse;
import com.propertypilot.application.dto.PropertyUpdateRequest;
import com.propertypilot.application.service.PropertyService;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.Property;
import com.propertypilot.infrastructure.persistence.repository.CustomerRepository;
import com.propertypilot.infrastructure.persistence.repository.PropertyRepository;
import com.propertypilot.infrastructure.persistence.entity.PropertyLocationEntity;
import com.propertypilot.infrastructure.persistence.repository.PropertyLocationRepository;
import com.propertypilot.infrastructure.persistence.repository.LocalityRepository;
import com.propertypilot.infrastructure.persistence.entity.LocalityEntity;
import com.propertypilot.security.SecurityService;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class PropertyServiceImpl
        implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final CustomerRepository customerRepository;
    private final PropertyLocationRepository propertyLocationRepository;
    private final LocalityRepository localityRepository;
    private final SecurityService securityService;

    public PropertyServiceImpl(
            PropertyRepository propertyRepository,
            CustomerRepository customerRepository,
            PropertyLocationRepository propertyLocationRepository,
            LocalityRepository localityRepository,
            SecurityService securityService) {

        this.propertyRepository = propertyRepository;
        this.customerRepository = customerRepository;
        this.propertyLocationRepository = propertyLocationRepository;
        this.localityRepository = localityRepository;
        this.securityService = securityService;
    }

    @Override
    @Transactional
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
                request.getListingStatus() == null || request.getListingStatus().isBlank()
                        ? "DRAFT" : request.getListingStatus().trim().toUpperCase());

        property.setPrice(
                request.getPrice());

        property.setStatus(
                request.getStatus() == null || request.getStatus().isBlank()
                        ? "AVAILABLE" : request.getStatus().trim().toUpperCase());

        Property savedProperty =
                propertyRepository.save(property);

        PropertyLocationEntity location = new PropertyLocationEntity();
        location.setProperty(savedProperty);
        location.setAddressLine1(request.getAddressLine1().trim());
        location.setAddressLine2(normalizeOptional(request.getAddressLine2()));
        location.setLocalityName(request.getLocalityName().trim());
        location.setCity(request.getCity().trim());
        location.setDistrict(request.getDistrict().trim());
        location.setState(request.getState().trim());
        location.setPostalCode(request.getPostalCode().trim());
        location.setCountryCode(normalizeCountryCode(request.getCountryCode()));
        location.setLocalityId(resolveLocalityId(request.getLocalityName(), request.getPostalCode(), request.getDistrict(), request.getState()));
        location.setMapVerificationStatus("PENDING");
        propertyLocationRepository.save(location);

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
    @Transactional
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

        if (request.getListingStatus() != null) {
            property.setListingStatus(request.getListingStatus().trim().toUpperCase());
        }

        property.setPrice(
                request.getPrice());

        if (request.getStatus() != null) {
            property.setStatus(request.getStatus().trim().toUpperCase());
        }

        if (request.getAddressLine1() != null && !request.getAddressLine1().isBlank()) {
            PropertyLocationEntity location = propertyLocationRepository
                    .findByProperty_PropertyId(propertyId)
                    .orElseGet(PropertyLocationEntity::new);
            location.setProperty(property);
            location.setAddressLine1(request.getAddressLine1().trim());
            location.setAddressLine2(normalizeOptional(request.getAddressLine2()));
            if (request.getLocalityName() != null) location.setLocalityName(request.getLocalityName().trim());
            if (request.getCity() != null) location.setCity(request.getCity().trim());
            if (request.getDistrict() != null) location.setDistrict(request.getDistrict().trim());
            if (request.getState() != null) location.setState(request.getState().trim());
            if (request.getPostalCode() != null) location.setPostalCode(request.getPostalCode().trim());
            if (request.getCountryCode() != null) location.setCountryCode(normalizeCountryCode(request.getCountryCode()));
            if (request.getLocalityName() != null && request.getPostalCode() != null
                    && request.getDistrict() != null && request.getState() != null) {
                location.setLocalityId(resolveLocalityId(request.getLocalityName(), request.getPostalCode(), request.getDistrict(), request.getState()));
            }
            if (location.getMapVerificationStatus() == null) {
                location.setMapVerificationStatus("PENDING");
            }
            propertyLocationRepository.save(location);
        }

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

        propertyLocationRepository.findByProperty_PropertyId(property.getPropertyId())
                .ifPresent(location -> {
                    response.setAddressLine1(location.getAddressLine1());
                    response.setAddressLine2(location.getAddressLine2());
                    response.setLocalityName(location.getLocalityName());
                    response.setCity(location.getCity());
                    response.setDistrict(location.getDistrict());
                    response.setState(location.getState());
                    response.setPostalCode(location.getPostalCode());
                    response.setCountryCode(location.getCountryCode());
                });

        response.setListingStatus(
                property.getListingStatus());

        response.setPrice(
                property.getPrice());

        response.setStatus(
                property.getStatus());

        return response;
    }

    private String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String normalizeCountryCode(String value) {
        return value == null || value.isBlank() ? "IN" : value.trim().toUpperCase();
    }

    private UUID resolveLocalityId(String localityName, String postalCode, String district, String state) {
        return localityRepository
                .findFirstByLocalityNameIgnoreCaseAndPincodeAndDistrict_DistrictNameIgnoreCaseAndDistrict_State_StateNameIgnoreCase(
                        localityName.trim(), postalCode.trim(), district.trim(), state.trim())
                .map(LocalityEntity::getLocalityId)
                .orElse(null);
    }
}
