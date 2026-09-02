package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRoleJpaRepository
        extends JpaRepository<UserRoleEntity, UUID> {
}