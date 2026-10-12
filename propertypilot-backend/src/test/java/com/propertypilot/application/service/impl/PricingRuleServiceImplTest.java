package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreatePricingRuleRequest;
import com.propertypilot.application.dto.PricingRuleResponse;
import com.propertypilot.infrastructure.persistence.entity.PricingRuleEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceEntity;
import com.propertypilot.infrastructure.persistence.repository.PricingRuleRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PricingRuleServiceImplTest {

    @Test
    void allowsInactiveRuleForRetirementWithoutDeletingHistory() {
        PricingRuleRepository ruleRepository = mock(PricingRuleRepository.class);
        ServiceRepository serviceRepository = mock(ServiceRepository.class);
        PricingRuleServiceImpl service = new PricingRuleServiceImpl(ruleRepository, serviceRepository);
        UUID serviceId = UUID.randomUUID();
        ServiceEntity catalogService = new ServiceEntity();
        catalogService.setServiceId(serviceId);
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(catalogService));
        when(ruleRepository.existsByService_ServiceIdAndRuleNameAndStatus(serviceId, "Approved rate", "ACTIVE"))
                .thenReturn(false);
        when(ruleRepository.save(any(PricingRuleEntity.class))).thenAnswer(invocation -> {
            PricingRuleEntity saved = invocation.getArgument(0);
            saved.setPriceRuleId(UUID.randomUUID());
            return saved;
        });

        CreatePricingRuleRequest request = new CreatePricingRuleRequest();
        request.setServiceId(serviceId);
        request.setRuleName("Approved rate");
        request.setBaseAmount(new BigDecimal("250.00"));
        request.setCurrencyCode("INR");
        request.setRuleDefinition("{}");
        request.setEffectiveFrom(OffsetDateTime.now());
        request.setStatus("INACTIVE");

        PricingRuleResponse response = service.createRule(request);

        assertNotNull(response);
        verify(ruleRepository).save(any(PricingRuleEntity.class));
    }
}
