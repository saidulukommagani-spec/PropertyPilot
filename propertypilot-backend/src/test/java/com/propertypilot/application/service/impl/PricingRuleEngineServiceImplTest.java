package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.CreatePricingRuleRequest;
import com.propertypilot.application.dto.PricingRuleResponse;
import com.propertypilot.application.dto.UpdatePricingRuleRequest;
import com.propertypilot.infrastructure.persistence.entity.PricingRuleEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceEntity;
import com.propertypilot.infrastructure.persistence.repository.PricingRuleRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PricingRuleServiceImplTest {

    @Mock
    private PricingRuleRepository pricingRuleRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private PricingRuleServiceImpl service;

    private UUID serviceId;
    private UUID ruleId;

    private ServiceEntity serviceEntity;
    private PricingRuleEntity ruleEntity;

    @BeforeEach
    void setUp() {

        serviceId = UUID.randomUUID();
        ruleId = UUID.randomUUID();

        serviceEntity = new ServiceEntity();
        serviceEntity.setServiceId(serviceId);
        serviceEntity.setServiceName("Registration");

        ruleEntity = new PricingRuleEntity();
        ruleEntity.setPriceRuleId(ruleId);
        ruleEntity.setService(serviceEntity);
        ruleEntity.setRuleName("RULE1");
        ruleEntity.setBaseAmount(
                BigDecimal.valueOf(1000));
        ruleEntity.setCurrencyCode("INR");
        ruleEntity.setRuleDefinition("{}");
        ruleEntity.setEffectiveFrom(
                OffsetDateTime.now());
        ruleEntity.setStatus("ACTIVE");
        ruleEntity.setVersion(1L);
    }

    @Test
    void createRule_success() {

        CreatePricingRuleRequest request =
                new CreatePricingRuleRequest();

        request.setServiceId(serviceId);
        request.setRuleName("RULE1");
        request.setBaseAmount(
                BigDecimal.valueOf(1000));
        request.setCurrencyCode("INR");
        request.setRuleDefinition("{}");
        request.setEffectiveFrom(
                OffsetDateTime.now());
        request.setStatus("ACTIVE");

        when(serviceRepository.findById(serviceId))
                .thenReturn(Optional.of(serviceEntity));

        when(pricingRuleRepository
                .existsByService_ServiceIdAndRuleNameAndStatus(
                        serviceId,
                        "RULE1",
                        "ACTIVE"))
                .thenReturn(false);

        when(pricingRuleRepository.save(any()))
                .thenReturn(ruleEntity);

        PricingRuleResponse response =
                service.createRule(request);

        assertThat(response).isNotNull();
        assertThat(response.getPriceRuleId())
                .isEqualTo(ruleId);

        verify(pricingRuleRepository)
                .save(any(PricingRuleEntity.class));
    }

    @Test
    void createRule_serviceNotFound() {

        CreatePricingRuleRequest request =
                new CreatePricingRuleRequest();

        request.setServiceId(serviceId);
        request.setStatus("ACTIVE");
        request.setEffectiveFrom(
                OffsetDateTime.now());

        when(serviceRepository.findById(serviceId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.createRule(request))
                .isInstanceOf(
                        ResourceNotFoundException.class)
                .hasMessage(
                        "Service not found");
    }

    @Test
    void createRule_duplicateActiveRule() {

        CreatePricingRuleRequest request =
                new CreatePricingRuleRequest();

        request.setServiceId(serviceId);
        request.setRuleName("RULE1");
        request.setStatus("ACTIVE");
        request.setEffectiveFrom(
                OffsetDateTime.now());

        when(serviceRepository.findById(serviceId))
                .thenReturn(Optional.of(serviceEntity));

        when(pricingRuleRepository
                .existsByService_ServiceIdAndRuleNameAndStatus(
                        serviceId,
                        "RULE1",
                        "ACTIVE"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                service.createRule(request))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessage(
                        "Active pricing rule already exists");
    }

    @Test
    void createRule_invalidStatus() {

        CreatePricingRuleRequest request =
                new CreatePricingRuleRequest();

        request.setStatus("BAD");

        assertThatThrownBy(() ->
                service.createRule(request))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessage("Invalid status");
    }

    @Test
    void createRule_invalidDates() {

        CreatePricingRuleRequest request =
                new CreatePricingRuleRequest();

        request.setStatus("ACTIVE");

        request.setEffectiveFrom(
                OffsetDateTime.now());

        request.setEffectiveTo(
                OffsetDateTime.now().minusDays(1));

        assertThatThrownBy(() ->
                service.createRule(request))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessage(
                        "Effective To must be after Effective From");
    }

    @Test
    void getRule_success() {

        when(pricingRuleRepository.findById(ruleId))
                .thenReturn(Optional.of(ruleEntity));

        PricingRuleResponse response =
                service.getRule(ruleId);

        assertThat(response).isNotNull();
        assertThat(response.getPriceRuleId())
                .isEqualTo(ruleId);
    }

    @Test
    void getRule_notFound() {

        when(pricingRuleRepository.findById(ruleId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.getRule(ruleId))
                .isInstanceOf(
                        ResourceNotFoundException.class)
                .hasMessage(
                        "Pricing rule not found");
    }

    @Test
    void getAllRules_success() {

        when(pricingRuleRepository.findAll())
                .thenReturn(
                        List.of(ruleEntity));

        List<PricingRuleResponse> result =
                service.getAllRules();

        assertThat(result)
                .hasSize(1);
    }

    @Test
    void getAllRules_empty() {

        when(pricingRuleRepository.findAll())
                .thenReturn(
                        Collections.emptyList());

        List<PricingRuleResponse> result =
                service.getAllRules();

        assertThat(result).isEmpty();
    }

    @Test
    void getRulesByService_success() {

        when(pricingRuleRepository
                .findByServiceServiceId(serviceId))
                .thenReturn(
                        List.of(ruleEntity));

        List<PricingRuleResponse> result =
                service.getRulesByService(serviceId);

        assertThat(result)
                .hasSize(1);
    }

    @Test
    void getRulesByService_empty() {

        when(pricingRuleRepository
                .findByServiceServiceId(serviceId))
                .thenReturn(
                        Collections.emptyList());

        List<PricingRuleResponse> result =
                service.getRulesByService(serviceId);

        assertThat(result).isEmpty();
    }

    @Test
    void updateRule_allFields() {

        UpdatePricingRuleRequest request =
                new UpdatePricingRuleRequest();

        request.setRuleName("NEW_RULE");
        request.setBaseAmount(
                BigDecimal.valueOf(2000));
        request.setCurrencyCode("USD");
        request.setRuleDefinition("{\"a\":1}");
        request.setStatus("INACTIVE");

        request.setEffectiveFrom(
                OffsetDateTime.now());

        request.setEffectiveTo(
                OffsetDateTime.now().plusDays(1));

        when(pricingRuleRepository.findById(ruleId))
                .thenReturn(Optional.of(ruleEntity));

        when(pricingRuleRepository.save(any()))
                .thenReturn(ruleEntity);

        PricingRuleResponse response =
                service.updateRule(
                        ruleId,
                        request);

        assertThat(response)
                .isNotNull();

        verify(pricingRuleRepository)
                .save(ruleEntity);
    }

    @Test
    void updateRule_partialUpdate() {

        UpdatePricingRuleRequest request =
                new UpdatePricingRuleRequest();

        request.setRuleName(
                "UPDATED");

        when(pricingRuleRepository.findById(ruleId))
                .thenReturn(Optional.of(ruleEntity));

        when(pricingRuleRepository.save(any()))
                .thenReturn(ruleEntity);

        service.updateRule(
                ruleId,
                request);

        assertThat(ruleEntity.getRuleName())
                .isEqualTo("UPDATED");
    }

    @Test
    void updateRule_notFound() {

        when(pricingRuleRepository.findById(ruleId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.updateRule(
                        ruleId,
                        new UpdatePricingRuleRequest()))
                .isInstanceOf(
                        ResourceNotFoundException.class);
    }

    @Test
    void updateRule_invalidStatus() {

        UpdatePricingRuleRequest request =
                new UpdatePricingRuleRequest();

        request.setStatus("XYZ");

        when(pricingRuleRepository.findById(ruleId))
                .thenReturn(Optional.of(ruleEntity));

        assertThatThrownBy(() ->
                service.updateRule(
                        ruleId,
                        request))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessage("Invalid status");
    }

    @Test
    void updateRule_invalidDates() {

        UpdatePricingRuleRequest request =
                new UpdatePricingRuleRequest();

        request.setEffectiveFrom(
                OffsetDateTime.now());

        request.setEffectiveTo(
                OffsetDateTime.now().minusDays(1));

        when(pricingRuleRepository.findById(ruleId))
                .thenReturn(Optional.of(ruleEntity));

        assertThatThrownBy(() ->
                service.updateRule(
                        ruleId,
                        request))
                .isInstanceOf(
                        IllegalArgumentException.class)
                .hasMessage(
                        "Effective To must be after Effective From");
    }
}