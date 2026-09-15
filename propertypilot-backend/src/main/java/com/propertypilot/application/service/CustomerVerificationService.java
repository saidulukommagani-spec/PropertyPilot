package com.propertypilot.application.service;

import com.propertypilot.application.dto.CreateCustomerVerificationRequest;
import com.propertypilot.application.dto.CustomerVerificationResponse;
import com.propertypilot.application.dto.VerificationDecisionRequest;

import java.util.List;
import java.util.UUID;

public interface CustomerVerificationService {

    CustomerVerificationResponse createVerification(
            UUID customerId,
            CreateCustomerVerificationRequest request);

    List<CustomerVerificationResponse> getCustomerVerifications(
            UUID customerId);

    CustomerVerificationResponse getVerification(
            UUID verificationId);

    CustomerVerificationResponse approveVerification(
            UUID verificationId,
            VerificationDecisionRequest request);

    CustomerVerificationResponse rejectVerification(
            UUID verificationId,
            VerificationDecisionRequest request);
}