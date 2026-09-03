package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.domain.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PropertyRepository
        extends JpaRepository<Property, UUID> {
}