package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateCustomerFavoriteRequest;
import com.propertypilot.application.dto.CustomerFavoriteResponse;
import com.propertypilot.application.service.CustomerFavoriteService;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.CustomerFavoriteEntity;
import com.propertypilot.infrastructure.persistence.entity.Property;
import com.propertypilot.infrastructure.persistence.repository.CustomerFavoriteRepository;
import com.propertypilot.infrastructure.persistence.repository.CustomerJpaRepository;
import com.propertypilot.infrastructure.persistence.repository.PropertyRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import com.propertypilot.security.SecurityService;

@Service
@Transactional
public class CustomerFavoriteServiceImpl
        implements CustomerFavoriteService {

    private final SecurityService securityService;

    private final CustomerFavoriteRepository
            customerFavoriteRepository;

    private final CustomerJpaRepository
            customerRepository;

    private final PropertyRepository
            propertyRepository;

    public CustomerFavoriteServiceImpl(
            CustomerFavoriteRepository customerFavoriteRepository,
            CustomerJpaRepository customerRepository,
            PropertyRepository propertyRepository,
            SecurityService securityService) {

        this.customerFavoriteRepository =
                customerFavoriteRepository;

        this.customerRepository =
                customerRepository;

        this.propertyRepository =
                propertyRepository;
        this.securityService = securityService;
    }

    @Override
    public CustomerFavoriteResponse createFavorite(
            UUID customerId,
            CreateCustomerFavoriteRequest request) {

        CustomerEntity customer =
                customerRepository.findById(customerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found"));
        securityService.validateCustomerAccess(customer);

        Property property = null;

        if (request.getPropertyId() != null) {

            property =
                    propertyRepository
                            .findById(
                                    request.getPropertyId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Property not found"));
        }
if (customerFavoriteRepository
        .existsByCustomerCustomerIdAndPropertyPropertyId(
                customerId,
                request.getPropertyId())) {

    throw new IllegalArgumentException(
            "Property already added to favorites");
}
        CustomerFavoriteEntity favorite =
                new CustomerFavoriteEntity();

        favorite.setCustomerFavoriteId(
                UUID.randomUUID());

        favorite.setCustomer(customer);

        // favorite.setFavoriteType(
        //         request.getFavoriteType());
favorite.setFavoriteType("PROPERTY");
        favorite.setProperty(property);

        favorite.setMarketplaceListingId(
                request.getMarketplaceListingId());

        CustomerFavoriteEntity saved =
                customerFavoriteRepository
                        .save(favorite);

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerFavoriteResponse>
    getCustomerFavorites(
            UUID customerId) {

        securityService.validateCustomerAccess(customerId);

        return customerFavoriteRepository
                .findByCustomerCustomerId(customerId)
                .stream()
                .filter(f ->
                        f.getCustomer()
                                .getCustomerId()
                                .equals(customerId))
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteFavorite(
            UUID favoriteId) {

        CustomerFavoriteEntity favorite =
                customerFavoriteRepository
                        .findById(favoriteId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Favorite not found"));
        securityService.validateCustomerAccess(favorite.getCustomer());

        customerFavoriteRepository.delete(
                favorite);
    }

    private CustomerFavoriteResponse mapToResponse(
            CustomerFavoriteEntity entity) {

        CustomerFavoriteResponse response =
                new CustomerFavoriteResponse();

        response.setCustomerFavoriteId(
                entity.getCustomerFavoriteId());

        response.setCustomerId(
                entity.getCustomer()
                        .getCustomerId());

        response.setFavoriteType(
                entity.getFavoriteType());

        response.setPropertyId(
                entity.getProperty() != null
                        ? entity.getProperty()
                        .getPropertyId()
                        : null);

        response.setMarketplaceListingId(
                entity.getMarketplaceListingId());

        return response;
    }
}
