package com.propertypilot.application.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AdminCustomerResponse(UUID customerId, UUID userId, String fullName, String email, String mobileNumber,
                                    String status, String customerType, String preferredLocation,
                                    OffsetDateTime createdAt) { }
