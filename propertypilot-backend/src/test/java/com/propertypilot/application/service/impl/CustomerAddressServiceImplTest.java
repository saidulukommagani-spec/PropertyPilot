package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateCustomerAddressRequest;
import com.propertypilot.application.dto.CustomerAddressResponse;
import com.propertypilot.application.dto.UpdateCustomerAddressRequest;
import com.propertypilot.infrastructure.persistence.entity.CustomerAddressEntity;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.LocalityEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerAddressRepository;
import com.propertypilot.infrastructure.persistence.repository.CustomerJpaRepository;
import com.propertypilot.infrastructure.persistence.repository.LocalityRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CustomerAddressServiceImplTest {

    @Mock
    private CustomerJpaRepository customerRepository;

    @Mock
    private CustomerAddressRepository addressRepository;

    @Mock
    private LocalityRepository localityRepository;

    @InjectMocks
    private CustomerAddressServiceImpl service;

    private CustomerAddressEntity buildAddress() {

        UUID customerId =
                UUID.randomUUID();

        UUID localityId =
                UUID.randomUUID();

        CustomerEntity customer =
                new CustomerEntity();

        customer.setCustomerId(
                customerId);

        LocalityEntity locality =
                new LocalityEntity();

        locality.setLocalityId(
                localityId);

        locality.setLocalityName(
                "Vanasthalipuram");

        CustomerAddressEntity entity =
                new CustomerAddressEntity();

        entity.setCustomerAddressId(
                UUID.randomUUID());

        entity.setCustomer(
                customer);

        entity.setLocality(
                locality);

        entity.setAddressType(
                "HOME");

        entity.setAddressLine1(
                "House No 1");

        entity.setAddressLine2(
                "Street 2");

        entity.setCity(
                "Hyderabad");

        entity.setPostalCode(
                "500070");

        entity.setCountryCode(
                "IN");

        entity.setIsPrimary(
                true);

        entity.setStatus(
                "ACTIVE");

        return entity;
    }

    @Test
    void createAddress_ShouldCreate() {

        UUID customerId =
                UUID.randomUUID();

        UUID localityId =
                UUID.randomUUID();

        CustomerEntity customer =
                new CustomerEntity();

        customer.setCustomerId(
                customerId);

        LocalityEntity locality =
                new LocalityEntity();

        locality.setLocalityId(
                localityId);

        CreateCustomerAddressRequest request =
                new CreateCustomerAddressRequest(
                        localityId,
                        "HOME",
                        "Line1",
                        "Line2",
                        "Hyderabad",
                        "500070",
                        "IN",
                        true
                );

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        when(localityRepository.findById(localityId))
                .thenReturn(Optional.of(locality));

        when(addressRepository.save(any()))
                .thenReturn(buildAddress());

        CustomerAddressResponse response =
                service.createAddress(
                        customerId,
                        request);

        assertThat(response)
                .isNotNull();

        verify(addressRepository)
                .save(any());
    }

    @Test
    void createAddress_ShouldThrow_WhenCustomerNotFound() {

        UUID customerId =
                UUID.randomUUID();

        CreateCustomerAddressRequest request =
                new CreateCustomerAddressRequest(
                        null,
                        "HOME",
                        "Line1",
                        null,
                        "Hyderabad",
                        "500070",
                        "IN",
                        true
                );

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> service.createAddress(
                        customerId,
                        request))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }

    @Test
    void createAddress_ShouldThrow_WhenLocalityNotFound() {

        UUID customerId =
                UUID.randomUUID();

        UUID localityId =
                UUID.randomUUID();

        CustomerEntity customer =
                new CustomerEntity();

        customer.setCustomerId(
                customerId);

        CreateCustomerAddressRequest request =
                new CreateCustomerAddressRequest(
                        localityId,
                        "HOME",
                        "Line1",
                        null,
                        "Hyderabad",
                        "500070",
                        "IN",
                        true
                );

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        when(localityRepository.findById(localityId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> service.createAddress(
                        customerId,
                        request))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }

    @Test
    void updateAddress_ShouldUpdate() {

        UUID addressId =
                UUID.randomUUID();

        UUID localityId =
                UUID.randomUUID();

        CustomerAddressEntity entity =
                buildAddress();

        LocalityEntity locality =
                new LocalityEntity();

        locality.setLocalityId(
                localityId);

        UpdateCustomerAddressRequest request =
                new UpdateCustomerAddressRequest(
                        localityId,
                        "OFFICE",
                        "New Line1",
                        "New Line2",
                        "Hyderabad",
                        "500071",
                        "IN",
                        false,
                        "ACTIVE"
                );

        when(addressRepository.findById(addressId))
                .thenReturn(Optional.of(entity));

        when(localityRepository.findById(localityId))
                .thenReturn(Optional.of(locality));

        when(addressRepository.save(any()))
                .thenReturn(entity);

        CustomerAddressResponse response =
                service.updateAddress(
                        addressId,
                        request);

        assertThat(response)
                .isNotNull();
    }

    @Test
    void updateAddress_ShouldThrow_WhenAddressNotFound() {

        UUID addressId =
                UUID.randomUUID();

        UpdateCustomerAddressRequest request =
                new UpdateCustomerAddressRequest(
                        null,
                        "HOME",
                        "Line1",
                        null,
                        "Hyderabad",
                        "500070",
                        "IN",
                        true,
                        "ACTIVE"
                );

        when(addressRepository.findById(addressId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> service.updateAddress(
                        addressId,
                        request))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }

    @Test
    void getAddress_ShouldReturnAddress() {

        UUID addressId =
                UUID.randomUUID();

        when(addressRepository.findById(addressId))
                .thenReturn(Optional.of(buildAddress()));

        CustomerAddressResponse response =
                service.getAddress(addressId);

        assertThat(response)
                .isNotNull();
    }

    @Test
    void getAddress_ShouldThrow_WhenNotFound() {

        UUID addressId =
                UUID.randomUUID();

        when(addressRepository.findById(addressId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> service.getAddress(addressId))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }

    @Test
    void getCustomerAddresses_ShouldReturnList() {

        UUID customerId =
                UUID.randomUUID();

        when(addressRepository
                .findByCustomerCustomerId(customerId))
                .thenReturn(
                        List.of(buildAddress()));

        List<CustomerAddressResponse> result =
                service.getCustomerAddresses(customerId);

        assertThat(result)
                .hasSize(1);
    }

    @Test
    void deleteAddress_ShouldDelete() {

        UUID addressId =
                UUID.randomUUID();

        CustomerAddressEntity entity =
                buildAddress();

        when(addressRepository.findById(addressId))
                .thenReturn(Optional.of(entity));

        service.deleteAddress(addressId);

        verify(addressRepository)
                .delete(entity);
    }

    @Test
    void deleteAddress_ShouldThrow_WhenNotFound() {

        UUID addressId =
                UUID.randomUUID();

        when(addressRepository.findById(addressId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> service.deleteAddress(addressId))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }
}