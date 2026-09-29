package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PropertyRepository
        extends JpaRepository<Property, UUID> {

    List<Property> findByStatus(String status);

    List<Property> findByStatusNot(String status);

    long count();
    long countByCustomer_CustomerId(
        UUID customerId);
        List<Property> findByCustomer_CustomerId(
        UUID customerId);
        List<Property> findByCustomer_CustomerIdAndStatusNot(
        UUID customerId,
        String status);
}