package com.propertypilot.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.propertypilot.application.dto.CreatePropertyOwnerRequest;
import com.propertypilot.application.dto.PropertyOwnerResponse;
import com.propertypilot.application.dto.UpdatePropertyOwnerRequest;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.Property;
import com.propertypilot.infrastructure.persistence.entity.PropertyOwnerEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerRepository;
import com.propertypilot.infrastructure.persistence.repository.PropertyOwnerRepository;
import com.propertypilot.infrastructure.persistence.repository.PropertyRepository;
import com.propertypilot.web.exception.BusinessException;
import com.propertypilot.web.exception.ResourceNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PropertyOwnerServiceImplTest {

    @Mock
    private PropertyOwnerRepository propertyOwnerRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private PropertyOwnerServiceImpl service;

        private Property property() {

        Property property = new Property();

        property.setPropertyId(
                UUID.randomUUID());

        return property;
    }

    private CustomerEntity customer() {

        CustomerEntity customer =
                new CustomerEntity();

        customer.setCustomerId(
                UUID.randomUUID());

        return customer;
    }

    private PropertyOwnerEntity owner() {

        PropertyOwnerEntity owner =
                new PropertyOwnerEntity();

        owner.setPropertyOwnerId(
                UUID.randomUUID());

        owner.setProperty(
                property());

        owner.setCustomer(
                customer());

        owner.setOwnerName(
                "Sai");

        owner.setOwnershipPercentage(
                new BigDecimal("50"));

        owner.setOwnershipType(
                "INDIVIDUAL");

        owner.setValidFrom(
                LocalDate.now());

        owner.setIsPrimary(true);

        return owner;
    }

    @Test
void createOwner_success() {

    Property property =
            property();

    CustomerEntity customer =
            customer();

    CreatePropertyOwnerRequest request =
            new CreatePropertyOwnerRequest();

    request.setPropertyId(
            property.getPropertyId());

    request.setCustomerId(
            customer.getCustomerId());

    request.setOwnerName(
            "Sai");

    request.setOwnershipPercentage(
            new BigDecimal("50"));

    request.setOwnershipType(
            "INDIVIDUAL");

    request.setValidFrom(
            LocalDate.now());

    request.setIsPrimary(true);

    when(propertyRepository.findById(
            property.getPropertyId()))
            .thenReturn(
                    Optional.of(property));

    when(customerRepository.findById(
            customer.getCustomerId()))
            .thenReturn(
                    Optional.of(customer));

    when(propertyOwnerRepository
            .existsByProperty_PropertyIdAndIsPrimaryTrue(
                    property.getPropertyId()))
            .thenReturn(false);

    when(propertyOwnerRepository
            .findByProperty_PropertyId(
                    property.getPropertyId()))
            .thenReturn(List.of());

    when(propertyOwnerRepository.save(any()))
            .thenAnswer(i -> {

                PropertyOwnerEntity entity =
                        i.getArgument(0);

                entity.setPropertyOwnerId(
                        UUID.randomUUID());

                return entity;
            });

    PropertyOwnerResponse response =
            service.createOwner(request);

    assertThat(response)
            .isNotNull();

    assertThat(response.getOwnerName())
            .isEqualTo("Sai");
}

@Test
void createOwner_propertyNotFound() {

    UUID propertyId =
            UUID.randomUUID();

    CreatePropertyOwnerRequest request =
            new CreatePropertyOwnerRequest();

    request.setPropertyId(
            propertyId);

    when(propertyRepository.findById(
            propertyId))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.createOwner(request))
            .isInstanceOf(
                    ResourceNotFoundException.class)
            .hasMessage(
                    "Property not found");
}

@Test
void createOwner_customerNotFound() {

    Property property =
            property();

    UUID customerId =
            UUID.randomUUID();

    CreatePropertyOwnerRequest request =
            new CreatePropertyOwnerRequest();

    request.setPropertyId(
            property.getPropertyId());

    request.setCustomerId(
            customerId);

    request.setOwnershipPercentage(
            new BigDecimal("20"));

    when(propertyRepository.findById(
            property.getPropertyId()))
            .thenReturn(
                    Optional.of(property));

    when(propertyOwnerRepository
            .findByProperty_PropertyId(
                    property.getPropertyId()))
            .thenReturn(List.of());

    when(customerRepository.findById(
            customerId))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.createOwner(request))
            .isInstanceOf(
                    ResourceNotFoundException.class)
            .hasMessage(
                    "Customer not found");
}
@Test
void createOwner_primaryExists() {

    Property property =
            property();

    CreatePropertyOwnerRequest request =
            new CreatePropertyOwnerRequest();

    request.setPropertyId(
            property.getPropertyId());

    request.setIsPrimary(true);

    when(propertyRepository.findById(
            property.getPropertyId()))
            .thenReturn(
                    Optional.of(property));

    when(propertyOwnerRepository
            .existsByProperty_PropertyIdAndIsPrimaryTrue(
                    property.getPropertyId()))
            .thenReturn(true);

    assertThatThrownBy(() ->
            service.createOwner(request))
            .isInstanceOf(
                    BusinessException.class)
            .hasMessageContaining(
                    "Primary owner already exists");
}

@Test
void createOwner_percentageExceeds100() {

    Property property =
            property();

    PropertyOwnerEntity existing =
            owner();

    existing.setOwnershipPercentage(
            new BigDecimal("80"));

    CreatePropertyOwnerRequest request =
            new CreatePropertyOwnerRequest();

    request.setPropertyId(
            property.getPropertyId());

    request.setOwnershipPercentage(
            new BigDecimal("30"));

    when(propertyRepository.findById(
            property.getPropertyId()))
            .thenReturn(
                    Optional.of(property));

    when(propertyOwnerRepository
            .findByProperty_PropertyId(
                    property.getPropertyId()))
            .thenReturn(
                    List.of(existing));

    assertThatThrownBy(() ->
            service.createOwner(request))
            .isInstanceOf(
                    BusinessException.class)
            .hasMessageContaining(
                    "cannot exceed 100");
}

@Test
void getOwner_success() {

    PropertyOwnerEntity owner =
            owner();

    when(propertyOwnerRepository.findById(
            owner.getPropertyOwnerId()))
            .thenReturn(
                    Optional.of(owner));

    PropertyOwnerResponse response =
            service.getOwner(
                    owner.getPropertyOwnerId());

    assertThat(response)
            .isNotNull();
}

@Test
void getOwner_notFound() {

    UUID id =
            UUID.randomUUID();

    when(propertyOwnerRepository.findById(id))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.getOwner(id))
            .isInstanceOf(
                    ResourceNotFoundException.class);
}

@Test
void getOwnersByProperty() {

    UUID propertyId =
            UUID.randomUUID();

    when(propertyOwnerRepository
            .findByProperty_PropertyId(
                    propertyId))
            .thenReturn(
                    List.of(owner()));

    assertThat(
            service.getOwnersByProperty(
                    propertyId))
            .hasSize(1);
}

@Test
void updateOwner_success() {

    PropertyOwnerEntity owner =
            owner();

    UpdatePropertyOwnerRequest request =
            new UpdatePropertyOwnerRequest();

    request.setOwnerName(
            "Updated");

    request.setOwnershipPercentage(
            new BigDecimal("40"));

    request.setOwnershipType(
            "JOINT");

    request.setIsPrimary(false);

    when(propertyOwnerRepository.findById(
            owner.getPropertyOwnerId()))
            .thenReturn(
                    Optional.of(owner));

    when(propertyOwnerRepository
            .findByProperty_PropertyId(
                    owner.getProperty()
                            .getPropertyId()))
            .thenReturn(List.of());

    when(propertyOwnerRepository.save(any()))
            .thenAnswer(i -> i.getArgument(0));

    PropertyOwnerResponse response =
            service.updateOwner(
                    owner.getPropertyOwnerId(),
                    request);

    assertThat(response.getOwnerName())
            .isEqualTo("Updated");
}

@Test
void updateOwner_notFound() {

    UUID id =
            UUID.randomUUID();

    when(propertyOwnerRepository.findById(id))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.updateOwner(
                    id,
                    new UpdatePropertyOwnerRequest()))
            .isInstanceOf(
                    ResourceNotFoundException.class);
}

@Test
void updateOwner_primaryExists() {

    PropertyOwnerEntity owner =
            owner();

    PropertyOwnerEntity existing =
            owner();

    existing.setIsPrimary(true);

    UpdatePropertyOwnerRequest request =
            new UpdatePropertyOwnerRequest();

    request.setIsPrimary(true);

    request.setOwnershipPercentage(
            new BigDecimal("40"));

    when(propertyOwnerRepository.findById(
            owner.getPropertyOwnerId()))
            .thenReturn(
                    Optional.of(owner));

    when(propertyOwnerRepository
            .findByProperty_PropertyIdAndPropertyOwnerIdNot(
                    any(),
                    any()))
            .thenReturn(
                    List.of(existing));

    assertThatThrownBy(() ->
            service.updateOwner(
                    owner.getPropertyOwnerId(),
                    request))
            .isInstanceOf(
                    BusinessException.class);
}

@Test
void deleteOwner_success() {

    PropertyOwnerEntity owner =
            owner();

    when(propertyOwnerRepository.findById(
            owner.getPropertyOwnerId()))
            .thenReturn(
                    Optional.of(owner));

    service.deleteOwner(
            owner.getPropertyOwnerId());

    verify(propertyOwnerRepository)
            .delete(owner);
}

@Test
void deleteOwner_notFound() {

    UUID id =
            UUID.randomUUID();

    when(propertyOwnerRepository.findById(id))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.deleteOwner(id))
            .isInstanceOf(
                    ResourceNotFoundException.class);
}

@Test
void updateOwner_isPrimaryFalse_shouldSkipValidation() {

    PropertyOwnerEntity owner =
            ownerEntity();

    UpdatePropertyOwnerRequest request =
            new UpdatePropertyOwnerRequest();

    request.setIsPrimary(false);
    request.setOwnerName("Updated Owner");
    request.setOwnershipPercentage(
            new BigDecimal("50"));

    when(propertyOwnerRepository.findById(
            owner.getPropertyOwnerId()))
            .thenReturn(Optional.of(owner));

    when(propertyOwnerRepository.save(any()))
            .thenAnswer(i -> i.getArgument(0));

    PropertyOwnerResponse response =
            service.updateOwner(
                    owner.getPropertyOwnerId(),
                    request);

    assertThat(response).isNotNull();
}

@Test
void updateOwner_shouldExcludeCurrentOwnerPercentage() {

    PropertyOwnerEntity owner =
            ownerEntity();

    owner.setOwnershipPercentage(
            new BigDecimal("70"));

    UpdatePropertyOwnerRequest request =
            new UpdatePropertyOwnerRequest();

    request.setOwnershipPercentage(
            new BigDecimal("70"));

    request.setIsPrimary(false);

    when(propertyOwnerRepository.findById(
            owner.getPropertyOwnerId()))
            .thenReturn(Optional.of(owner));

    when(propertyOwnerRepository
            .findByProperty_PropertyId(
                    owner.getProperty()
                            .getPropertyId()))
            .thenReturn(List.of(owner));

    when(propertyOwnerRepository.save(any()))
            .thenAnswer(i -> i.getArgument(0));

    PropertyOwnerResponse response =
            service.updateOwner(
                    owner.getPropertyOwnerId(),
                    request);

    assertThat(response).isNotNull();
}

@Test
void updateOwner_customerNotFound() {

    PropertyOwnerEntity owner =
            ownerEntity();

    UpdatePropertyOwnerRequest request =
            new UpdatePropertyOwnerRequest();

    request.setCustomerId(
            UUID.randomUUID());

    request.setOwnershipPercentage(
            new BigDecimal("50"));

    request.setIsPrimary(false);

    when(propertyOwnerRepository.findById(
            owner.getPropertyOwnerId()))
            .thenReturn(Optional.of(owner));

    when(customerRepository.findById(
            request.getCustomerId()))
            .thenReturn(Optional.empty());

    assertThatThrownBy(() ->
            service.updateOwner(
                    owner.getPropertyOwnerId(),
                    request))
            .isInstanceOf(
                    ResourceNotFoundException.class)
            .hasMessage(
                    "Customer not found");
}

@Test
void updateOwner_primaryAlreadyExists() {

    PropertyOwnerEntity owner =
            ownerEntity();

    PropertyOwnerEntity anotherOwner =
            ownerEntity();

    anotherOwner.setIsPrimary(true);

    UpdatePropertyOwnerRequest request =
            new UpdatePropertyOwnerRequest();

    request.setIsPrimary(true);

    request.setOwnershipPercentage(
            new BigDecimal("50"));

    when(propertyOwnerRepository.findById(
            owner.getPropertyOwnerId()))
            .thenReturn(Optional.of(owner));

    when(propertyOwnerRepository
            .findByProperty_PropertyIdAndPropertyOwnerIdNot(
                    any(),
                    any()))
            .thenReturn(
                    List.of(anotherOwner));

    assertThatThrownBy(() ->
            service.updateOwner(
                    owner.getPropertyOwnerId(),
                    request))
            .isInstanceOf(
                    BusinessException.class)
            .hasMessage(
                    "Primary owner already exists");
}

private PropertyOwnerEntity ownerEntity() {

    PropertyOwnerEntity owner =
            new PropertyOwnerEntity();

    owner.setPropertyOwnerId(
            UUID.randomUUID());

    Property property =
            new Property();

    property.setPropertyId(
            UUID.randomUUID());

    owner.setProperty(property);

    owner.setOwnerName(
            "John Owner");

    owner.setOwnershipPercentage(
            new BigDecimal("50"));

    owner.setOwnershipType(
            "SELF");

    owner.setIsPrimary(true);

    CustomerEntity customer =
            new CustomerEntity();

    customer.setCustomerId(
            UUID.randomUUID());

    owner.setCustomer(customer);

    return owner;
}
    
}
