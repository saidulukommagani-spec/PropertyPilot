package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.EtaRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EtaRuleRepository
        extends JpaRepository<EtaRuleEntity, UUID> {
}