package com.propertypilot.application.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.EligibilityRuleDefinition;
import com.propertypilot.infrastructure.persistence.entity.Property;
import com.propertypilot.infrastructure.persistence.entity.PropertyLocationEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceEligibilityRuleEntity;
import com.propertypilot.infrastructure.persistence.repository.PropertyLocationRepository;
import com.propertypilot.infrastructure.persistence.repository.PropertyRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceEligibilityRuleRepository;
import com.propertypilot.web.exception.BusinessException;
import com.propertypilot.web.exception.ResourceNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EligibilityEngineImplTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private PropertyLocationRepository propertyLocationRepository;

    @Mock
    private ServiceEligibilityRuleRepository ruleRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private EligibilityEngineImpl service;

    @Test
    void validateEligibility_propertyNotFound() {

        UUID propertyId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();

        when(propertyRepository.findById(propertyId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.validateEligibility(
                        serviceId,
                        propertyId))
                .isInstanceOf(
                        ResourceNotFoundException.class)
                .hasMessage(
                        "Property not found");
    }

    @Test
    void validateEligibility_propertyLocationNotFound() {

        UUID propertyId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();

        Property property = new Property();

        when(propertyRepository.findById(propertyId))
                .thenReturn(Optional.of(property));

        when(propertyLocationRepository
                .findByProperty_PropertyId(propertyId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.validateEligibility(
                        serviceId,
                        propertyId))
                .isInstanceOf(
                        ResourceNotFoundException.class)
                .hasMessage(
                        "Property location not found");
    }

    @Test
    void validateEligibility_equalsRulePass() throws Exception {

        UUID propertyId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();

        Property property = new Property();
        property.setPropertyType("HOUSE");

        PropertyLocationEntity location =
                new PropertyLocationEntity();

        ServiceEligibilityRuleEntity rule =
                new ServiceEligibilityRuleEntity();

        rule.setRuleName("Property Type Rule");
        rule.setRuleDefinition("json");

        EligibilityRuleDefinition definition =
                new EligibilityRuleDefinition();

        definition.setField("PROPERTY_TYPE");
        definition.setOperator("EQUALS");
        definition.setValue("HOUSE");

        when(propertyRepository.findById(propertyId))
                .thenReturn(Optional.of(property));

        when(propertyLocationRepository
                .findByProperty_PropertyId(propertyId))
                .thenReturn(Optional.of(location));

        when(ruleRepository
                .findByService_ServiceIdAndStatusOrderByPriorityAsc(
                        serviceId,
                        "ACTIVE"))
                .thenReturn(List.of(rule));

        when(objectMapper.readValue(
                eq("json"),
                eq(EligibilityRuleDefinition.class)))
                .thenReturn(definition);

        service.validateEligibility(
                serviceId,
                propertyId);
    }

    @Test
    void validateEligibility_equalsRuleFail() throws Exception {

        UUID propertyId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();

        Property property = new Property();
        property.setPropertyType("LAND");

        PropertyLocationEntity location =
                new PropertyLocationEntity();

        ServiceEligibilityRuleEntity rule =
                new ServiceEligibilityRuleEntity();

        rule.setRuleName("Property Type Rule");
        rule.setRuleDefinition("json");

        EligibilityRuleDefinition definition =
                new EligibilityRuleDefinition();

        definition.setField("PROPERTY_TYPE");
        definition.setOperator("EQUALS");
        definition.setValue("HOUSE");

        when(propertyRepository.findById(propertyId))
                .thenReturn(Optional.of(property));

        when(propertyLocationRepository
                .findByProperty_PropertyId(propertyId))
                .thenReturn(Optional.of(location));

        when(ruleRepository
                .findByService_ServiceIdAndStatusOrderByPriorityAsc(
                        serviceId,
                        "ACTIVE"))
                .thenReturn(List.of(rule));

        when(objectMapper.readValue(
                eq("json"),
                eq(EligibilityRuleDefinition.class)))
                .thenReturn(definition);

        assertThatThrownBy(() ->
                service.validateEligibility(
                        serviceId,
                        propertyId))
                .isInstanceOf(
                        BusinessException.class)
                .hasMessageContaining(
                        "Eligibility failed");
    }

    @Test
    void validateEligibility_inOperatorPass() throws Exception {

        UUID propertyId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();

        Property property = new Property();
        property.setPropertyType("HOUSE");

        PropertyLocationEntity location =
                new PropertyLocationEntity();

        ServiceEligibilityRuleEntity rule =
                new ServiceEligibilityRuleEntity();

        rule.setRuleName("IN Rule");
        rule.setRuleDefinition("json");

        EligibilityRuleDefinition definition =
                new EligibilityRuleDefinition();

        definition.setField("PROPERTY_TYPE");
        definition.setOperator("IN");
        definition.setValue(
                List.of("HOUSE", "FLAT"));

        when(propertyRepository.findById(propertyId))
                .thenReturn(Optional.of(property));

        when(propertyLocationRepository
                .findByProperty_PropertyId(propertyId))
                .thenReturn(Optional.of(location));

        when(ruleRepository
                .findByService_ServiceIdAndStatusOrderByPriorityAsc(
                        serviceId,
                        "ACTIVE"))
                .thenReturn(List.of(rule));

        when(objectMapper.readValue(
                eq("json"),
                eq(EligibilityRuleDefinition.class)))
                .thenReturn(definition);

        service.validateEligibility(
                serviceId,
                propertyId);
    }

    @Test
    void validateEligibility_notInOperatorPass() throws Exception {

        UUID propertyId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();

        Property property = new Property();
        property.setPropertyType("HOUSE");

        PropertyLocationEntity location =
                new PropertyLocationEntity();

        ServiceEligibilityRuleEntity rule =
                new ServiceEligibilityRuleEntity();

        rule.setRuleName("NOT IN Rule");
        rule.setRuleDefinition("json");

        EligibilityRuleDefinition definition =
                new EligibilityRuleDefinition();

        definition.setField("PROPERTY_TYPE");
        definition.setOperator("NOT_IN");
        definition.setValue(
                List.of("LAND", "PLOT"));

        when(propertyRepository.findById(propertyId))
                .thenReturn(Optional.of(property));

        when(propertyLocationRepository
                .findByProperty_PropertyId(propertyId))
                .thenReturn(Optional.of(location));

        when(ruleRepository
                .findByService_ServiceIdAndStatusOrderByPriorityAsc(
                        serviceId,
                        "ACTIVE"))
                .thenReturn(List.of(rule));

        when(objectMapper.readValue(
                eq("json"),
                eq(EligibilityRuleDefinition.class)))
                .thenReturn(definition);

        service.validateEligibility(
                serviceId,
                propertyId);
    }

    @Test
    void validateEligibility_invalidRuleJson() throws Exception {

        UUID propertyId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();

        Property property = new Property();

        PropertyLocationEntity location =
                new PropertyLocationEntity();

        ServiceEligibilityRuleEntity rule =
                new ServiceEligibilityRuleEntity();

        rule.setRuleDefinition("bad-json");

        when(propertyRepository.findById(propertyId))
                .thenReturn(Optional.of(property));

        when(propertyLocationRepository
                .findByProperty_PropertyId(propertyId))
                .thenReturn(Optional.of(location));

        when(ruleRepository
                .findByService_ServiceIdAndStatusOrderByPriorityAsc(
                        serviceId,
                        "ACTIVE"))
                .thenReturn(List.of(rule));

        when(objectMapper.readValue(
                eq("bad-json"),
                eq(EligibilityRuleDefinition.class)))
                .thenThrow(
                        new RuntimeException());

        assertThatThrownBy(() ->
                service.validateEligibility(
                        serviceId,
                        propertyId))
                .isInstanceOf(
                        BusinessException.class)
                .hasMessage(
                        "Invalid eligibility rule configuration");
    }

    @Test
    void validateEligibility_multipleRulesPass() throws Exception {

        UUID propertyId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();

        Property property = new Property();
        property.setPropertyType("HOUSE");
        property.setStatus("ACTIVE");

        PropertyLocationEntity location =
                new PropertyLocationEntity();

        ServiceEligibilityRuleEntity rule1 =
                new ServiceEligibilityRuleEntity();

        rule1.setRuleDefinition("json1");

        ServiceEligibilityRuleEntity rule2 =
                new ServiceEligibilityRuleEntity();

        rule2.setRuleDefinition("json2");

        EligibilityRuleDefinition def1 =
                new EligibilityRuleDefinition();

        def1.setField("PROPERTY_TYPE");
        def1.setOperator("EQUALS");
        def1.setValue("HOUSE");

        EligibilityRuleDefinition def2 =
                new EligibilityRuleDefinition();

        def2.setField("PROPERTY_STATUS");
        def2.setOperator("EQUALS");
        def2.setValue("ACTIVE");

        when(propertyRepository.findById(propertyId))
                .thenReturn(Optional.of(property));

        when(propertyLocationRepository
                .findByProperty_PropertyId(propertyId))
                .thenReturn(Optional.of(location));

        when(ruleRepository
                .findByService_ServiceIdAndStatusOrderByPriorityAsc(
                        serviceId,
                        "ACTIVE"))
                .thenReturn(List.of(rule1, rule2));

        when(objectMapper.readValue(
                eq("json1"),
                eq(EligibilityRuleDefinition.class)))
                .thenReturn(def1);

        when(objectMapper.readValue(
                eq("json2"),
                eq(EligibilityRuleDefinition.class)))
                .thenReturn(def2);

        service.validateEligibility(
                serviceId,
                propertyId);
    }
}