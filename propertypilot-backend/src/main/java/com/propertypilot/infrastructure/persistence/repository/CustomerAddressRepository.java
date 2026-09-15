package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.CustomerAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.UUID;

@Repository
public interface CustomerAddressRepository
        extends JpaRepository<CustomerAddressEntity, UUID> {
            List<CustomerAddressEntity> findByCustomerCustomerId(
        UUID customerId);

        boolean existsByCustomerCustomerIdAndAddressType(
        UUID customerId,
        String addressType);


}