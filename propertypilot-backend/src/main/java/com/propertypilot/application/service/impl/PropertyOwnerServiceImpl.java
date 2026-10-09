package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreatePropertyOwnerRequest;
import com.propertypilot.application.dto.PropertyOwnerResponse;
import com.propertypilot.application.dto.UpdatePropertyOwnerRequest;
import com.propertypilot.application.service.PropertyOwnerService;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.Property;
import com.propertypilot.infrastructure.persistence.entity.PropertyOwnerEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerRepository;
import com.propertypilot.infrastructure.persistence.repository.PropertyOwnerRepository;
import com.propertypilot.infrastructure.persistence.repository.PropertyRepository;
import com.propertypilot.web.exception.BusinessException;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import com.propertypilot.security.SecurityService;

@Service
@Transactional
public class PropertyOwnerServiceImpl
        implements PropertyOwnerService {

    private final SecurityService securityService;

    private final PropertyOwnerRepository
            propertyOwnerRepository;

    private final PropertyRepository
            propertyRepository;

    private final CustomerRepository
            customerRepository;

    public PropertyOwnerServiceImpl(
            PropertyOwnerRepository propertyOwnerRepository,
            PropertyRepository propertyRepository,
            CustomerRepository customerRepository,
            SecurityService securityService) {

        this.propertyOwnerRepository =
                propertyOwnerRepository;

        this.propertyRepository =
                propertyRepository;

        this.customerRepository =
                customerRepository;
        this.securityService = securityService;
    }

    @Override
    public PropertyOwnerResponse createOwner(
            CreatePropertyOwnerRequest request) {

        Property property =
                propertyRepository
                        .findById(
                                request.getPropertyId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Property not found"));
        securityService.validateOperationsAccess();

        validatePrimaryOwner(
                property.getPropertyId(),
                request.getIsPrimary());

        validateOwnershipPercentage(
                property.getPropertyId(),
                request.getOwnershipPercentage(),
                null);

        CustomerEntity customer = null;

        if (request.getCustomerId() != null) {

            customer =
                    customerRepository
                            .findById(
                                    request.getCustomerId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Customer not found"));
        }

        PropertyOwnerEntity owner =
                new PropertyOwnerEntity();

        owner.setProperty(property);
        owner.setCustomer(customer);
        owner.setOwnerName(
                request.getOwnerName());
        owner.setOwnershipPercentage(
                request.getOwnershipPercentage());
        owner.setOwnershipType(
                request.getOwnershipType());
        owner.setValidFrom(
                request.getValidFrom());
        owner.setValidTo(
                request.getValidTo());
        owner.setIsPrimary(
                request.getIsPrimary());

        return mapToResponse(
                propertyOwnerRepository
                        .save(owner));
    }

    @Override
    @Transactional(readOnly = true)
    public PropertyOwnerResponse getOwner(
            UUID propertyOwnerId) {

        PropertyOwnerEntity owner = propertyOwnerRepository
                        .findById(
                                propertyOwnerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Property owner not found"));
        securityService.validatePropertyOwnership(owner.getProperty());
        return mapToResponse(owner);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyOwnerResponse>
    getOwnersByProperty(
            UUID propertyId) {

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        securityService.validatePropertyOwnership(property);

        return propertyOwnerRepository
                .findByProperty_PropertyId(
                        propertyId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public PropertyOwnerResponse updateOwner(
            UUID propertyOwnerId,
            UpdatePropertyOwnerRequest request) {

        PropertyOwnerEntity owner =
                propertyOwnerRepository
                        .findById(
                                propertyOwnerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Property owner not found"));
        securityService.validateOperationsAccess();

        validatePrimaryOwnerForUpdate(
                owner.getProperty()
                        .getPropertyId(),
                propertyOwnerId,
                request.getIsPrimary());

        validateOwnershipPercentage(
                owner.getProperty()
                        .getPropertyId(),
                request.getOwnershipPercentage(),
                propertyOwnerId);

        CustomerEntity customer = null;

        if (request.getCustomerId() != null) {

            customer =
                    customerRepository
                            .findById(
                                    request.getCustomerId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Customer not found"));
        }

        owner.setCustomer(customer);
        owner.setOwnerName(
                request.getOwnerName());
        owner.setOwnershipPercentage(
                request.getOwnershipPercentage());
        owner.setOwnershipType(
                request.getOwnershipType());
        owner.setValidFrom(
                request.getValidFrom());
        owner.setValidTo(
                request.getValidTo());
        owner.setIsPrimary(
                request.getIsPrimary());

        return mapToResponse(
                propertyOwnerRepository
                        .save(owner));
    }

    @Override
    public void deleteOwner(
            UUID propertyOwnerId) {

        PropertyOwnerEntity owner =
                propertyOwnerRepository
                        .findById(
                                propertyOwnerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Property owner not found"));
        securityService.validateOperationsAccess();

        propertyOwnerRepository
                .delete(owner);
    }

    private void validatePrimaryOwner(
            UUID propertyId,
            Boolean isPrimary) {

        if (Boolean.TRUE.equals(isPrimary)
                && propertyOwnerRepository
                .existsByProperty_PropertyIdAndIsPrimaryTrue(
                        propertyId)) {

            throw new BusinessException(
                    "Primary owner already exists");
        }
    }

    private void validatePrimaryOwnerForUpdate(
            UUID propertyId,
            UUID propertyOwnerId,
            Boolean isPrimary) {

        if (!Boolean.TRUE.equals(isPrimary)) {
            return;
        }

        List<PropertyOwnerEntity> owners =
                propertyOwnerRepository
                        .findByProperty_PropertyIdAndPropertyOwnerIdNot(
                                propertyId,
                                propertyOwnerId);

        boolean primaryExists =
                owners.stream()
                        .anyMatch(
                                PropertyOwnerEntity::getIsPrimary);

        if (primaryExists) {

            throw new BusinessException(
                    "Primary owner already exists");
        }
    }

    private void validateOwnershipPercentage(
            UUID propertyId,
            BigDecimal percentage,
            UUID excludeOwnerId) {

        BigDecimal total =
                propertyOwnerRepository
                        .findByProperty_PropertyId(
                                propertyId)
                        .stream()
                        .filter(o ->
                                excludeOwnerId == null ||
                                !o.getPropertyOwnerId()
                                        .equals(
                                                excludeOwnerId))
                        .map(PropertyOwnerEntity
                                ::getOwnershipPercentage)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add);

        total = total.add(
                percentage);

        if (total.compareTo(
                new BigDecimal("100")) > 0) {

            throw new BusinessException(
                    "Total ownership percentage cannot exceed 100");
        }
    }

    private PropertyOwnerResponse mapToResponse(
            PropertyOwnerEntity entity) {

        PropertyOwnerResponse response =
                new PropertyOwnerResponse();

        response.setPropertyOwnerId(
                entity.getPropertyOwnerId());

        response.setPropertyId(
                entity.getProperty()
                        .getPropertyId());

        response.setCustomerId(
                entity.getCustomer() != null
                        ? entity.getCustomer()
                        .getCustomerId()
                        : null);

        response.setOwnerName(
                entity.getOwnerName());

        response.setOwnershipPercentage(
                entity.getOwnershipPercentage());

        response.setOwnershipType(
                entity.getOwnershipType());

        response.setValidFrom(
                entity.getValidFrom());

        response.setValidTo(
                entity.getValidTo());

        response.setIsPrimary(
                entity.getIsPrimary());

        return response;
    }
}
