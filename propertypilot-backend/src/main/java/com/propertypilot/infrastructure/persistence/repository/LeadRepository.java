package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.LeadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LeadRepository extends JpaRepository<LeadEntity, UUID> {

}