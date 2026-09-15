package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.CustomerVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerVerificationRepository
        extends JpaRepository<CustomerVerificationEntity, UUID> {

    List<CustomerVerificationEntity> findByCustomerCustomerId(
            UUID customerId);

    Optional<CustomerVerificationEntity> findByCustomerVerificationId(
            UUID customerVerificationId);

    boolean existsByCustomerCustomerIdAndVerificationType(
            UUID customerId,
            String verificationType);
}