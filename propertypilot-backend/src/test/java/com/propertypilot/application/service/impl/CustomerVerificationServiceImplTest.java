package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreateCustomerVerificationRequest;
import com.propertypilot.application.dto.CustomerVerificationResponse;
import com.propertypilot.application.dto.VerificationDecisionRequest;
import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.CustomerVerificationEntity;
import com.propertypilot.infrastructure.persistence.entity.PropertyDocumentEntity;
import com.propertypilot.infrastructure.persistence.entity.UserEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerJpaRepository;
import com.propertypilot.infrastructure.persistence.repository.CustomerVerificationRepository;
import com.propertypilot.infrastructure.persistence.repository.PropertyDocumentRepository;
import com.propertypilot.infrastructure.persistence.repository.UserRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CustomerVerificationServiceImplTest {

    @Mock
    private CustomerVerificationRepository
            customerVerificationRepository;

    @Mock
    private CustomerJpaRepository
            customerRepository;

    @Mock
    private PropertyDocumentRepository
            propertyDocumentRepository;

    @Mock
    private UserRepository
            userRepository;

    @InjectMocks
    private CustomerVerificationServiceImpl
            service;

    private CustomerVerificationEntity buildEntity() {

        CustomerEntity customer =
                new CustomerEntity();

        customer.setCustomerId(
                UUID.randomUUID());

        PropertyDocumentEntity document =
                new PropertyDocumentEntity();

        document.setDocumentId(
                UUID.randomUUID());

        UserEntity verifier =
                new UserEntity();

        verifier.setUserId(
                UUID.randomUUID());

        CustomerVerificationEntity entity =
                new CustomerVerificationEntity();

        entity.setCustomerVerificationId(
                UUID.randomUUID());

        entity.setCustomer(
                customer);

        entity.setDocument(
                document);

        entity.setVerifiedBy(
                verifier);

        entity.setVerificationType(
                "AADHAR");

        entity.setStatus(
                "VERIFIED");

        entity.setProviderReference(
                "REF123");

        entity.setVerifiedAt(
                OffsetDateTime.now());

        entity.setExpiresAt(
                OffsetDateTime.now()
                        .plusYears(1));

        return entity;
    }

    @Test
    void createVerification_ShouldCreate_WithDocument() {

        UUID customerId =
                UUID.randomUUID();

        UUID documentId =
                UUID.randomUUID();

        CustomerEntity customer =
                new CustomerEntity();

        customer.setCustomerId(
                customerId);

        PropertyDocumentEntity document =
                new PropertyDocumentEntity();

        document.setDocumentId(
                documentId);

        CreateCustomerVerificationRequest request =
                new CreateCustomerVerificationRequest();

        request.setDocumentId(
                documentId);

        request.setVerificationType(
                "AADHAR");

        request.setReferenceNumberHash(
                "HASH");

        request.setProviderReference(
                "REF");

        request.setExpiresAt(
                OffsetDateTime.now());

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        when(propertyDocumentRepository.findById(documentId))
                .thenReturn(Optional.of(document));

        when(customerVerificationRepository.save(any()))
                .thenReturn(buildEntity());

        CustomerVerificationResponse response =
                service.createVerification(
                        customerId,
                        request);

        assertThat(response)
                .isNotNull();

        verify(customerVerificationRepository)
                .save(any());
    }

    @Test
    void createVerification_ShouldCreate_WithoutDocument() {

        UUID customerId =
                UUID.randomUUID();

        CustomerEntity customer =
                new CustomerEntity();

        customer.setCustomerId(
                customerId);

        CreateCustomerVerificationRequest request =
                new CreateCustomerVerificationRequest();

        request.setVerificationType(
                "AADHAR");

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        when(customerVerificationRepository.save(any()))
                .thenReturn(buildEntity());

        CustomerVerificationResponse response =
                service.createVerification(
                        customerId,
                        request);

        assertThat(response)
                .isNotNull();
    }

    @Test
    void createVerification_ShouldThrow_WhenCustomerNotFound() {

        UUID customerId =
                UUID.randomUUID();

        CreateCustomerVerificationRequest request =
                new CreateCustomerVerificationRequest();

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> service.createVerification(
                        customerId,
                        request))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }

    @Test
    void createVerification_ShouldThrow_WhenDocumentNotFound() {

        UUID customerId =
                UUID.randomUUID();

        UUID documentId =
                UUID.randomUUID();

        CustomerEntity customer =
                new CustomerEntity();

        customer.setCustomerId(
                customerId);

        CreateCustomerVerificationRequest request =
                new CreateCustomerVerificationRequest();

        request.setDocumentId(
                documentId);

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        when(propertyDocumentRepository.findById(documentId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> service.createVerification(
                        customerId,
                        request))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }

    @Test
    void getCustomerVerifications_ShouldReturnList() {

        UUID customerId =
                UUID.randomUUID();

        when(customerVerificationRepository
                .findByCustomerCustomerId(customerId))
                .thenReturn(
                        List.of(buildEntity()));

        List<CustomerVerificationResponse> result =
                service.getCustomerVerifications(
                        customerId);

        assertThat(result)
                .hasSize(1);
    }

    @Test
    void getVerification_ShouldReturnVerification() {

        UUID verificationId =
                UUID.randomUUID();

        when(customerVerificationRepository
                .findById(verificationId))
                .thenReturn(
                        Optional.of(buildEntity()));

        CustomerVerificationResponse response =
                service.getVerification(
                        verificationId);

        assertThat(response)
                .isNotNull();
    }

    @Test
    void getVerification_ShouldThrow_WhenNotFound() {

        UUID verificationId =
                UUID.randomUUID();

        when(customerVerificationRepository
                .findById(verificationId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> service.getVerification(
                        verificationId))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }

    @Test
    void approveVerification_ShouldApprove() {

        UUID verificationId =
                UUID.randomUUID();

        UUID verifierId =
                UUID.randomUUID();

        CustomerVerificationEntity entity =
                buildEntity();

        UserEntity verifier =
                new UserEntity();

        verifier.setUserId(
                verifierId);

        VerificationDecisionRequest request =
                new VerificationDecisionRequest();

        request.setVerifiedByUserId(
                verifierId);

        when(customerVerificationRepository
                .findById(verificationId))
                .thenReturn(Optional.of(entity));

        when(userRepository.findById(verifierId))
                .thenReturn(Optional.of(verifier));

        when(customerVerificationRepository.save(any()))
                .thenReturn(entity);

        CustomerVerificationResponse response =
                service.approveVerification(
                        verificationId,
                        request);

        assertThat(response)
                .isNotNull();
    }

    @Test
    void approveVerification_ShouldThrow_WhenVerificationNotFound() {

        UUID verificationId =
                UUID.randomUUID();

        VerificationDecisionRequest request =
                new VerificationDecisionRequest();

        when(customerVerificationRepository
                .findById(verificationId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> service.approveVerification(
                        verificationId,
                        request))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }

    @Test
    void approveVerification_ShouldThrow_WhenVerifierNotFound() {

        UUID verificationId =
                UUID.randomUUID();

        UUID verifierId =
                UUID.randomUUID();

        CustomerVerificationEntity entity =
                buildEntity();

        VerificationDecisionRequest request =
                new VerificationDecisionRequest();

        request.setVerifiedByUserId(
                verifierId);

        when(customerVerificationRepository
                .findById(verificationId))
                .thenReturn(Optional.of(entity));

        when(userRepository.findById(verifierId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> service.approveVerification(
                        verificationId,
                        request))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }

    @Test
    void rejectVerification_ShouldReject() {

        UUID verificationId =
                UUID.randomUUID();

        UUID verifierId =
                UUID.randomUUID();

        CustomerVerificationEntity entity =
                buildEntity();

        UserEntity verifier =
                new UserEntity();

        verifier.setUserId(
                verifierId);

        VerificationDecisionRequest request =
                new VerificationDecisionRequest();

        request.setVerifiedByUserId(
                verifierId);

        request.setRejectionReason(
                "Invalid document");

        when(customerVerificationRepository
                .findById(verificationId))
                .thenReturn(Optional.of(entity));

        when(userRepository.findById(verifierId))
                .thenReturn(Optional.of(verifier));

        when(customerVerificationRepository.save(any()))
                .thenReturn(entity);

        CustomerVerificationResponse response =
                service.rejectVerification(
                        verificationId,
                        request);

        assertThat(response)
                .isNotNull();
    }

    @Test
    void rejectVerification_ShouldThrow_WhenVerificationNotFound() {

        UUID verificationId =
                UUID.randomUUID();

        VerificationDecisionRequest request =
                new VerificationDecisionRequest();

        when(customerVerificationRepository
                .findById(verificationId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> service.rejectVerification(
                        verificationId,
                        request))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }

    @Test
    void rejectVerification_ShouldThrow_WhenVerifierNotFound() {

        UUID verificationId =
                UUID.randomUUID();

        UUID verifierId =
                UUID.randomUUID();

        CustomerVerificationEntity entity =
                buildEntity();

        VerificationDecisionRequest request =
                new VerificationDecisionRequest();

        request.setVerifiedByUserId(
                verifierId);

        when(customerVerificationRepository
                .findById(verificationId))
                .thenReturn(Optional.of(entity));

        when(userRepository.findById(verifierId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                () -> service.rejectVerification(
                        verificationId,
                        request))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }
}