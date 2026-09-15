package com.propertypilot.infrastructure.persistence.repository;

import com.propertypilot.infrastructure.persistence.entity.PricingRuleApprovalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PricingRuleApprovalRepository
        extends JpaRepository<PricingRuleApprovalEntity, UUID> {

    List<PricingRuleApprovalEntity>
    findByStatus(String status);

    List<PricingRuleApprovalEntity>
    findByPricingRulePriceRuleId(
            UUID priceRuleId);

    List<PricingRuleApprovalEntity>
    findByRequestedByUserId(
            UUID requestedBy);

    List<PricingRuleApprovalEntity>
    findByDecidedByUserId(
            UUID decidedBy);
}