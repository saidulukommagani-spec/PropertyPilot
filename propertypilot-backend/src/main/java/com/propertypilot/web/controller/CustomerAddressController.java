package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateCustomerAddressRequest;
import com.propertypilot.application.dto.CustomerAddressResponse;
import com.propertypilot.application.dto.UpdateCustomerAddressRequest;

import com.propertypilot.application.service.CustomerAddressService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerAddressController {

    private final CustomerAddressService service;

    public CustomerAddressController(
            CustomerAddressService service) {

        this.service = service;
    }

    @PostMapping("/{customerId}/addresses")
    public CustomerAddressResponse createAddress(
            @PathVariable UUID customerId,
            @Valid @RequestBody CreateCustomerAddressRequest request) {

        return service.createAddress(
                customerId,
                request);
    }

    @GetMapping("/{customerId}/addresses")
    public List<CustomerAddressResponse> getAddresses(
            @PathVariable UUID customerId) {

        return service.getCustomerAddresses(
                customerId);
    }

    @PutMapping("/addresses/{addressId}")
    public CustomerAddressResponse updateAddress(
            @PathVariable UUID addressId,
            @Valid @RequestBody UpdateCustomerAddressRequest request) {

        return service.updateAddress(
                addressId,
                request);
    }

    @DeleteMapping("/addresses/{addressId}")
    public void deleteAddress(
            @PathVariable UUID addressId) {

        service.deleteAddress(addressId);
    }
}