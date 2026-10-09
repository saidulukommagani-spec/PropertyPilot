package com.propertypilot.application.dto;

import java.util.UUID;

public record CustomerProfileResponse(UUID customerId, String fullName, String email, String mobileNumber) { }
