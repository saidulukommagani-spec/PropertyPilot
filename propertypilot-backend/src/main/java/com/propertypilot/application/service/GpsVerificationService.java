package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreateGpsVerificationRequest;
import com.propertypilot.application.dto.GpsVerificationResponse;

import java.util.UUID;

public interface GpsVerificationService {

    GpsVerificationResponse createVerification(
            CreateGpsVerificationRequest request);

    GpsVerificationResponse getVerification(
            UUID gpsVerificationId);
}