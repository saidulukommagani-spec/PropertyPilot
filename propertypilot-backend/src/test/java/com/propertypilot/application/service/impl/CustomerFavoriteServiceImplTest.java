package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateCustomerFavoriteRequest;
import com.propertypilot.application.dto.CustomerFavoriteResponse;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.CustomerFavoriteEntity;
import com.propertypilot.infrastructure.persistence.entity.Property;
import com.propertypilot.infrastructure.persistence.repository.CustomerFavoriteRepository;
import com.propertypilot.infrastructure.persistence.repository.CustomerJpaRepository;
import com.propertypilot.infrastructure.persistence.repository.PropertyRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerFavoriteServiceImplTest {

    @Mock
    private CustomerFavoriteRepository customerFavoriteRepository;

    @Mock
    private CustomerJpaRepository customerRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @InjectMocks
    private CustomerFavoriteServiceImpl service;

    @Test
    void createFavorite_Success() {

        UUID customerId = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();
        UUID listingId = UUID.randomUUID();

        CustomerEntity customer =
                new CustomerEntity();
        customer.setCustomerId(customerId);

        Property property =
                new Property();
        property.setPropertyId(propertyId);

        CreateCustomerFavoriteRequest request =
                new CreateCustomerFavoriteRequest();

        request.setPropertyId(propertyId);
        request.setMarketplaceListingId(listingId);

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        when(propertyRepository.findById(propertyId))
                .thenReturn(Optional.of(property));

        when(customerFavoriteRepository
                .existsByCustomerCustomerIdAndPropertyPropertyId(
                        customerId,
                        propertyId))
                .thenReturn(false);

        when(customerFavoriteRepository.save(any(
                CustomerFavoriteEntity.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        CustomerFavoriteResponse response =
                service.createFavorite(
                        customerId,
                        request);

        assertNotNull(response);

        assertEquals(
                customerId,
                response.getCustomerId());

        assertEquals(
                propertyId,
                response.getPropertyId());

        assertEquals(
                listingId,
                response.getMarketplaceListingId());

        assertEquals(
                "PROPERTY",
                response.getFavoriteType());

        verify(customerFavoriteRepository)
                .save(any(
                        CustomerFavoriteEntity.class));
    }

    @Test
    void createFavorite_CustomerNotFound() {

        UUID customerId = UUID.randomUUID();

        CreateCustomerFavoriteRequest request =
                new CreateCustomerFavoriteRequest();

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.createFavorite(
                        customerId,
                        request));

        verify(customerFavoriteRepository,
                never()).save(any());
    }

    @Test
    void createFavorite_PropertyNotFound() {

        UUID customerId = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();

        CustomerEntity customer =
                new CustomerEntity();

        customer.setCustomerId(customerId);

        CreateCustomerFavoriteRequest request =
                new CreateCustomerFavoriteRequest();

        request.setPropertyId(propertyId);

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        when(propertyRepository.findById(propertyId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.createFavorite(
                        customerId,
                        request));

        verify(customerFavoriteRepository,
                never()).save(any());
    }

    @Test
    void createFavorite_AlreadyExists() {

        UUID customerId = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();

        CustomerEntity customer =
                new CustomerEntity();

        customer.setCustomerId(customerId);

        Property property =
                new Property();

        property.setPropertyId(propertyId);

        CreateCustomerFavoriteRequest request =
                new CreateCustomerFavoriteRequest();

        request.setPropertyId(propertyId);

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        when(propertyRepository.findById(propertyId))
                .thenReturn(Optional.of(property));

        when(customerFavoriteRepository
                .existsByCustomerCustomerIdAndPropertyPropertyId(
                        customerId,
                        propertyId))
                .thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.createFavorite(
                                customerId,
                                request));

        assertEquals(
                "Property already added to favorites",
                exception.getMessage());

        verify(customerFavoriteRepository,
                never()).save(any());
    }

    @Test
    void getCustomerFavorites_Success() {

        UUID customerId = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();

        CustomerEntity customer =
                new CustomerEntity();

        customer.setCustomerId(customerId);

        Property property =
                new Property();

        property.setPropertyId(propertyId);

        CustomerFavoriteEntity favorite =
                new CustomerFavoriteEntity();

        favorite.setCustomerFavoriteId(
                UUID.randomUUID());

        favorite.setCustomer(customer);

        favorite.setProperty(property);

        favorite.setFavoriteType(
                "PROPERTY");

        when(customerFavoriteRepository
                .findByCustomerCustomerId(
                        customerId))
                .thenReturn(
                        List.of(favorite));

        List<CustomerFavoriteResponse> response =
                service.getCustomerFavorites(
                        customerId);

        assertEquals(
                1,
                response.size());

        assertEquals(
                customerId,
                response.get(0)
                        .getCustomerId());

        assertEquals(
                propertyId,
                response.get(0)
                        .getPropertyId());

        assertEquals(
                "PROPERTY",
                response.get(0)
                        .getFavoriteType());
    }

    @Test
    void deleteFavorite_Success() {

        UUID favoriteId = UUID.randomUUID();

        CustomerFavoriteEntity favorite =
                new CustomerFavoriteEntity();

        favorite.setCustomerFavoriteId(
                favoriteId);

        when(customerFavoriteRepository
                .findById(favoriteId))
                .thenReturn(
                        Optional.of(favorite));

        service.deleteFavorite(
                favoriteId);

        verify(customerFavoriteRepository)
                .delete(favorite);
    }

    @Test
    void deleteFavorite_NotFound() {

        UUID favoriteId = UUID.randomUUID();

        when(customerFavoriteRepository
                .findById(favoriteId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.deleteFavorite(
                        favoriteId));

        verify(customerFavoriteRepository,
                never()).delete(any());
    }
}