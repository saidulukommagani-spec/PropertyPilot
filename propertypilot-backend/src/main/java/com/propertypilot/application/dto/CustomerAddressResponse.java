package com.propertypilot.application.dto;

import com.propertypilot.infrastructure.persistence.entity.CustomerAddressEntity;

import java.util.UUID;

public class CustomerAddressResponse {

    private UUID customerAddressId;
    private UUID customerId;
    private UUID localityId;
    private String localityName;
    private String addressType;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String postalCode;
    private String countryCode;
    private Boolean isPrimary;
    private String status;

    public CustomerAddressResponse(
            UUID customerAddressId,
            UUID customerId,
            UUID localityId,
            String localityName,
            String addressType,
            String addressLine1,
            String addressLine2,
            String city,
            String postalCode,
            String countryCode,
            Boolean isPrimary,
            String status) {

        this.customerAddressId = customerAddressId;
        this.customerId = customerId;
        this.localityId = localityId;
        this.localityName = localityName;
        this.addressType = addressType;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.postalCode = postalCode;
        this.countryCode = countryCode;
        this.isPrimary = isPrimary;
        this.status = status;
    }

    public static CustomerAddressResponse map(
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

    public UUID getCustomerAddressId() {
        return customerAddressId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public UUID getLocalityId() {
        return localityId;
    }

    public String getLocalityName() {
        return localityName;
    }

    public String getAddressType() {
        return addressType;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public String getStatus() {
        return status;
    }
}