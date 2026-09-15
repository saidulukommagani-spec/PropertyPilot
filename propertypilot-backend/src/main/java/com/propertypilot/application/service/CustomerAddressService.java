package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreateCustomerAddressRequest;
import com.propertypilot.application.dto.CustomerAddressResponse;
import com.propertypilot.application.dto.UpdateCustomerAddressRequest;

import java.util.List;
import java.util.UUID;

public interface CustomerAddressService {

    CustomerAddressResponse createAddress(
            UUID customerId,
            CreateCustomerAddressRequest request);

    List<CustomerAddressResponse> getCustomerAddresses(
            UUID customerId);

    CustomerAddressResponse getAddress(
            UUID customerAddressId);

    CustomerAddressResponse updateAddress(
            UUID customerAddressId,
            UpdateCustomerAddressRequest request);

    void deleteAddress(
            UUID customerAddressId);
}