package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.ClusterRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface ClusterRuleRepository
        extends JpaRepository<ClusterRuleEntity, UUID> {
    List<ClusterRuleEntity> findByStatusAndServiceTypeIn(String status, List<String> serviceTypes);
}
