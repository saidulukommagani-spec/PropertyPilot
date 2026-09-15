package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateCustomerVerificationRequest;
import com.propertypilot.application.dto.CustomerVerificationResponse;
import com.propertypilot.application.dto.VerificationDecisionRequest;
import com.propertypilot.application.service.CustomerVerificationService;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.CustomerVerificationEntity;
import com.propertypilot.infrastructure.persistence.entity.PropertyDocumentEntity;
import com.propertypilot.infrastructure.persistence.entity.UserEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerJpaRepository;
import com.propertypilot.infrastructure.persistence.repository.CustomerVerificationRepository;
import com.propertypilot.infrastructure.persistence.repository.PropertyDocumentRepository;
import com.propertypilot.infrastructure.persistence.repository.UserRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CustomerVerificationServiceImpl
        implements CustomerVerificationService {

    private final CustomerVerificationRepository
            customerVerificationRepository;

    private final CustomerJpaRepository
            customerRepository;

    private final PropertyDocumentRepository
            propertyDocumentRepository;

    private final UserRepository
            userRepository;

    public CustomerVerificationServiceImpl(
            CustomerVerificationRepository customerVerificationRepository,
            CustomerJpaRepository customerRepository,
            PropertyDocumentRepository propertyDocumentRepository,
            UserRepository userRepository) {

        this.customerVerificationRepository =
                customerVerificationRepository;

        this.customerRepository =
                customerRepository;

        this.propertyDocumentRepository =
                propertyDocumentRepository;

        this.userRepository =
                userRepository;
    }

    @Override
    public CustomerVerificationResponse createVerification(
            UUID customerId,
            CreateCustomerVerificationRequest request) {

        CustomerEntity customer =
                customerRepository.findById(customerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found"));

        PropertyDocumentEntity document = null;

        if (request.getDocumentId() != null) {

            document =
                    propertyDocumentRepository
                            .findById(request.getDocumentId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Document not found"));
        }

        CustomerVerificationEntity verification =
                new CustomerVerificationEntity();

        verification.setCustomer(customer);

        verification.setDocument(document);

        verification.setVerificationType(
                request.getVerificationType());

        verification.setReferenceNumberHash(
                request.getReferenceNumberHash());

        verification.setProviderReference(
                request.getProviderReference());

        verification.setExpiresAt(
                request.getExpiresAt());

        verification.setStatus("PENDING");

        CustomerVerificationEntity saved =
                customerVerificationRepository
                        .save(verification);

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerVerificationResponse>
    getCustomerVerifications(
            UUID customerId) {

        return customerVerificationRepository
                .findByCustomerCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerVerificationResponse getVerification(
            UUID verificationId) {

        CustomerVerificationEntity verification =
                customerVerificationRepository
                        .findById(verificationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Verification not found"));

        return mapToResponse(verification);
    }

    @Override
    public CustomerVerificationResponse approveVerification(
            UUID verificationId,
            VerificationDecisionRequest request) {

        CustomerVerificationEntity verification =
                customerVerificationRepository
                        .findById(verificationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Verification not found"));

        UserEntity verifier =
                userRepository.findById(
                                request.getVerifiedByUserId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Verifier not found"));

        verification.setVerifiedBy(verifier);

        verification.setStatus("VERIFIED");

        verification.setVerifiedAt(
                OffsetDateTime.now());

        verification.setRejectionReason(null);

        CustomerVerificationEntity saved =
                customerVerificationRepository
                        .save(verification);

        return mapToResponse(saved);
    }

    @Override
    public CustomerVerificationResponse rejectVerification(
            UUID verificationId,
            VerificationDecisionRequest request) {

        CustomerVerificationEntity verification =
                customerVerificationRepository
                        .findById(verificationId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Verification not found"));

        UserEntity verifier =
                userRepository.findById(
                                request.getVerifiedByUserId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Verifier not found"));

        verification.setVerifiedBy(verifier);

        verification.setStatus("REJECTED");

        verification.setVerifiedAt(
                OffsetDateTime.now());

        verification.setRejectionReason(
                request.getRejectionReason());

        CustomerVerificationEntity saved =
                customerVerificationRepository
                        .save(verification);

        return mapToResponse(saved);
    }

    private CustomerVerificationResponse mapToResponse(
            CustomerVerificationEntity entity) {

        CustomerVerificationResponse response =
                new CustomerVerificationResponse();

        response.setCustomerVerificationId(
                entity.getCustomerVerificationId());

        response.setCustomerId(
                entity.getCustomer().getCustomerId());

        response.setDocumentId(
                entity.getDocument() != null
                        ? entity.getDocument().getDocumentId()
                        : null);

        response.setVerifiedByUserId(
                entity.getVerifiedBy() != null
                        ? entity.getVerifiedBy().getUserId()
                        : null);

        response.setVerificationType(
                entity.getVerificationType());

        response.setStatus(
                entity.getStatus());

        response.setRejectionReason(
                entity.getRejectionReason());

        response.setProviderReference(
                entity.getProviderReference());

        response.setVerifiedAt(
                entity.getVerifiedAt());

        response.setExpiresAt(
                entity.getExpiresAt());

        return response;
    }
}