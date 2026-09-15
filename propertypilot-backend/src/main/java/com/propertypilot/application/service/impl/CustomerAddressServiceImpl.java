package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateCustomerAddressRequest;
import com.propertypilot.application.dto.CustomerAddressResponse;
import com.propertypilot.application.dto.UpdateCustomerAddressRequest;
import com.propertypilot.application.service.CustomerAddressService;
import com.propertypilot.infrastructure.persistence.entity.CustomerAddressEntity;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.LocalityEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerAddressRepository;
import com.propertypilot.infrastructure.persistence.repository.CustomerJpaRepository;
import com.propertypilot.infrastructure.persistence.repository.LocalityRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CustomerAddressServiceImpl
        implements CustomerAddressService {

    private final CustomerJpaRepository customerRepository;
    private final CustomerAddressRepository addressRepository;
    private final LocalityRepository localityRepository;

    public CustomerAddressServiceImpl(
            CustomerJpaRepository customerRepository,
            CustomerAddressRepository addressRepository,
            LocalityRepository localityRepository) {

        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
        this.localityRepository = localityRepository;
    }

    @Override
    public CustomerAddressResponse createAddress(
            UUID customerId,
            CreateCustomerAddressRequest request) {

        CustomerEntity customer =
                customerRepository.findById(customerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found"));

        CustomerAddressEntity entity =
                new CustomerAddressEntity();

        entity.setCustomer(customer);

        if (request.localityId() != null) {

            LocalityEntity locality =
                    localityRepository.findById(
                                    request.localityId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Locality not found"));

            entity.setLocality(locality);
        }

        entity.setAddressType(request.addressType());
        entity.setAddressLine1(request.addressLine1());
        entity.setAddressLine2(request.addressLine2());
        entity.setCity(request.city());
        entity.setPostalCode(request.postalCode());
        entity.setCountryCode(request.countryCode());
        entity.setIsPrimary(request.isPrimary());
        entity.setStatus("ACTIVE");

        return map(
                addressRepository.save(entity));
    }

    @Override
    public CustomerAddressResponse updateAddress(
            UUID addressId,
            UpdateCustomerAddressRequest request) {

        CustomerAddressEntity entity =
                addressRepository.findById(addressId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Address not found"));

        if (request.localityId() != null) {

            LocalityEntity locality =
                    localityRepository.findById(
                                    request.localityId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Locality not found"));

            entity.setLocality(locality);
        }

        entity.setAddressType(request.addressType());
        entity.setAddressLine1(request.addressLine1());
        entity.setAddressLine2(request.addressLine2());
        entity.setCity(request.city());
        entity.setPostalCode(request.postalCode());
        entity.setCountryCode(request.countryCode());
        entity.setIsPrimary(request.isPrimary());

        return map(
                addressRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerAddressResponse getAddress(
            UUID addressId) {

        return map(
                addressRepository.findById(addressId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Address not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerAddressResponse> getCustomerAddresses(
            UUID customerId) {

        return addressRepository
                .findByCustomerCustomerId(customerId)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public void deleteAddress(
            UUID addressId) {

        CustomerAddressEntity entity =
                addressRepository.findById(addressId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Address not found"));

        addressRepository.delete(entity);
    }

    private CustomerAddressResponse map(
            CustomerAddressEntity entity) {

        return new CustomerAddressResponse(
        entity.getCustomerAddressId(),

        entity.getCustomer().getCustomerId(),

        entity.getLocality() != null
                ? entity.getLocality().getLocalityId()
                : null,

        entity.getLocality() != null
                ? entity.getLocality().getLocalityName()
                : null,

        entity.getAddressType(),

        entity.getAddressLine1(),

        entity.getAddressLine2(),

        entity.getCity(),

        entity.getPostalCode(),

        entity.getCountryCode(),

        entity.getIsPrimary(),

        entity.getStatus()
);
    }
}