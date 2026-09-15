package com.propertypilot.application.dto;

import java.util.UUID;

public record UpdateCustomerAddressRequest(

        UUID localityId,

        String addressType,

        String addressLine1,

        String addressLine2,

        String city,

        String postalCode,

        String countryCode,

        Boolean isPrimary,

        String status

) {
}