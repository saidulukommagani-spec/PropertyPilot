package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository
        extends JpaRepository<CustomerEntity, UUID> {

    @EntityGraph(attributePaths = "user")
    Optional<CustomerEntity> findByUser_Email(
            String email);

    @EntityGraph(attributePaths = "user")
             Optional<CustomerEntity> findByUser_UserId(
            UUID userId);

            long count();
}
