package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByMobileNumber(String mobileNumber);

    boolean existsByEmailAndUserIdNot(String email, UUID userId);

    boolean existsByMobileNumberAndUserIdNot(String mobileNumber, UUID userId);

    Optional<UserEntity> findById(UUID userId);
}
