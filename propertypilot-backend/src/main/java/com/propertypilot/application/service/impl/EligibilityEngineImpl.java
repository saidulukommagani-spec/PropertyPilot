package com.propertypilot.application.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.EligibilityRuleDefinition;
import com.propertypilot.application.service.EligibilityEngine;
import com.propertypilot.domain.enums.EligibilityField;
import com.propertypilot.domain.enums.EligibilityOperator;
import com.propertypilot.infrastructure.persistence.entity.Property;
import com.propertypilot.infrastructure.persistence.entity.PropertyLocationEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceEligibilityRuleEntity;
import com.propertypilot.infrastructure.persistence.repository.PropertyLocationRepository;
import com.propertypilot.infrastructure.persistence.repository.PropertyRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceEligibilityRuleRepository;
import com.propertypilot.web.exception.BusinessException;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class EligibilityEngineImpl
        implements EligibilityEngine {

    private static final String ACTIVE = "ACTIVE";

    private final PropertyRepository propertyRepository;
    private final PropertyLocationRepository propertyLocationRepository;
    private final ServiceEligibilityRuleRepository ruleRepository;
    private final ObjectMapper objectMapper;

    public EligibilityEngineImpl(
            PropertyRepository propertyRepository,
            PropertyLocationRepository propertyLocationRepository,
            ServiceEligibilityRuleRepository ruleRepository,
            ObjectMapper objectMapper) {

        this.propertyRepository =
                propertyRepository;

        this.propertyLocationRepository =
                propertyLocationRepository;

        this.ruleRepository =
                ruleRepository;

        this.objectMapper =
                objectMapper;
    }

    @Override
    public void validateEligibility(
            UUID serviceId,
            UUID propertyId) {

        Property property =
                propertyRepository
                        .findById(propertyId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Property not found"));

        PropertyLocationEntity location =
                propertyLocationRepository
                        .findByProperty_PropertyId(propertyId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Property location not found"));

        List<ServiceEligibilityRuleEntity> rules =
                ruleRepository
                        .findByService_ServiceIdAndStatusOrderByPriorityAsc(
                                serviceId,
                                ACTIVE);

        for (ServiceEligibilityRuleEntity rule : rules) {

            EligibilityRuleDefinition definition =
                    parseRule(
                            rule.getRuleDefinition());

            boolean passed =
                    evaluateRule(
                            definition,
                            property,
                            location);

            if (!passed) {

                throw new BusinessException(
                        "Eligibility failed for rule: "
                                + rule.getRuleName());
            }
        }
    }

    private EligibilityRuleDefinition parseRule(
            String json) {

        try {

            return objectMapper.readValue(
                    json,
                    EligibilityRuleDefinition.class);

        } catch (Exception ex) {

            throw new BusinessException(
                    "Invalid eligibility rule configuration");
        }
    }

    private boolean evaluateRule(
            EligibilityRuleDefinition rule,
            Property property,
            PropertyLocationEntity location) {

        EligibilityField field =
                EligibilityField.valueOf(
                        rule.getField());

        EligibilityOperator operator =
                EligibilityOperator.valueOf(
                        rule.getOperator());

        Object actualValue =
                getFieldValue(
                        field,
                        property,
                        location);

        Object expectedValue =
                rule.getValue();

        return switch (operator) {

            case EQUALS ->
                    Objects.toString(actualValue, "")
                            .equalsIgnoreCase(
                                    Objects.toString(
                                            expectedValue,
                                            ""));

            case NOT_EQUALS ->
                    !Objects.toString(actualValue, "")
                            .equalsIgnoreCase(
                                    Objects.toString(
                                            expectedValue,
                                            ""));

            case IN ->
                    ((List<?>) expectedValue)
                            .stream()
                            .map(Object::toString)
                            .anyMatch(value ->
                                    value.equalsIgnoreCase(
                                            Objects.toString(
                                                    actualValue,
                                                    "")));

            case NOT_IN ->
                    ((List<?>) expectedValue)
                            .stream()
                            .map(Object::toString)
                            .noneMatch(value ->
                                    value.equalsIgnoreCase(
                                            Objects.toString(
                                                    actualValue,
                                                    "")));
        };
    }

    private Object getFieldValue(
            EligibilityField field,
            Property property,
            PropertyLocationEntity location) {

        return switch (field) {

            case PROPERTY_TYPE ->
                    property.getPropertyType();

            case LISTING_STATUS ->
                    property.getListingStatus();

            case PROPERTY_STATUS ->
                    property.getStatus();

            case LOCALITY_ID ->
                    location.getLocalityId();

            case COVERAGE_ZONE_ID ->
                    location.getCoverageZoneId();

            case MAP_VERIFICATION_STATUS ->
                    location.getMapVerificationStatus();
        };
    }
}