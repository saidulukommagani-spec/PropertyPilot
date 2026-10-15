package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.ServiceConfigurationRequest;
import com.propertypilot.application.dto.ServiceConfigurationResponse;
import com.propertypilot.application.dto.EligibleServiceTypeResponse;
import com.propertypilot.application.service.EligibilityEngine;
import com.propertypilot.application.service.ServiceCatalogConfigurationService;
import com.propertypilot.infrastructure.persistence.entity.Property;
import com.propertypilot.infrastructure.persistence.entity.ServiceEntity;
import com.propertypilot.infrastructure.persistence.entity.ServiceEligibilityRuleEntity;
import com.propertypilot.infrastructure.persistence.repository.PropertyRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceRepository;
import com.propertypilot.infrastructure.persistence.repository.ServiceEligibilityRuleRepository;
import com.propertypilot.security.SecurityService;
import com.propertypilot.web.exception.BusinessException;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.Locale;
import java.util.Comparator;
import java.util.Set;
import java.util.ArrayList;
import java.util.Objects;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
@Transactional
public class ServiceCatalogConfigurationServiceImpl implements ServiceCatalogConfigurationService {
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final Set<String> PROPERTY_TYPES = Set.of("HOUSE", "VILLA", "APARTMENT", "TOWNHOUSE",
            "COMMERCIAL_PROPERTY", "INDUSTRIAL_PROPERTY", "PLOT", "AGRICULTURAL_LAND", "OTHER");
    private final ServiceRepository serviceRepository;
    private final ServiceEligibilityRuleRepository eligibilityRuleRepository;
    private final PropertyRepository propertyRepository;
    private final EligibilityEngine eligibilityEngine;
    private final SecurityService securityService;

    public ServiceCatalogConfigurationServiceImpl(ServiceRepository serviceRepository,
            ServiceEligibilityRuleRepository eligibilityRuleRepository,
            PropertyRepository propertyRepository, EligibilityEngine eligibilityEngine,
            SecurityService securityService) {
        this.serviceRepository = serviceRepository;
        this.eligibilityRuleRepository = eligibilityRuleRepository;
        this.propertyRepository = propertyRepository;
        this.eligibilityEngine = eligibilityEngine;
        this.securityService = securityService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceConfigurationResponse> getActiveServices() {
        return serviceRepository.findByActiveFlagTrue().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EligibleServiceTypeResponse> getEligibleServiceTypes(UUID propertyId) {
        Property property = customerProperty(propertyId);
        return eligibleServices(property).stream()
                .map(ServiceEntity::getCategory)
                .filter(category -> category != null && !category.isBlank())
                .map(ServiceCatalogConfigurationServiceImpl::normalizeCode)
                .distinct()
                .sorted()
                .map(code -> new EligibleServiceTypeResponse(code, label(code)))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceConfigurationResponse> getEligibleServices(UUID propertyId, String serviceType) {
        String code = normalizeCode(serviceType);
        if (code.isBlank()) {
            throw new BusinessException("Choose a supported service type");
        }
        Property property = customerProperty(propertyId);
        List<ServiceEntity> services = eligibleServices(property).stream()
                .filter(item -> code.equals(normalizeCode(item.getCategory())))
                .sorted(Comparator.comparing(ServiceEntity::getServiceName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
        if (services.isEmpty()) {
            throw new BusinessException("This service type is unavailable for the selected property");
        }
        return services.stream().map(this::toResponse).toList();
    }

    private Property customerProperty(UUID propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        securityService.validatePropertyOwnership(property);
        if (property.getArchivedAt() != null || "ARCHIVED".equalsIgnoreCase(property.getStatus())) {
            throw new BusinessException("Archived properties cannot be used for a service request");
        }
        return property;
    }

    private List<ServiceEntity> eligibleServices(Property property) {
        return serviceRepository.findByActiveFlagTrue().stream()
                .filter(item -> Boolean.TRUE.equals(item.getActiveFlag()))
                .filter(item -> item.getCategory() != null && !item.getCategory().isBlank())
                .filter(item -> {
                    try {
                        eligibilityEngine.validateEligibility(item.getServiceId(), property.getPropertyId());
                        return true;
                    } catch (BusinessException | ResourceNotFoundException unavailable) {
                        return false;
                    }
                })
                .toList();
    }

    private static String normalizeCode(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT).replaceAll("[\\s-]+", "_");
    }

    private static String label(String code) {
        return java.util.Arrays.stream(code.toLowerCase(Locale.ROOT).split("_"))
                .filter(part -> !part.isBlank())
                .map(part -> Character.toUpperCase(part.charAt(0)) + part.substring(1))
                .collect(java.util.stream.Collectors.joining(" "));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceConfigurationResponse> getAllServices() {
        return serviceRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public ServiceConfigurationResponse create(ServiceConfigurationRequest request) {
        validatePolicy(request);
        ServiceEntity entity = new ServiceEntity();
        apply(entity, request);
        entity = serviceRepository.save(entity);
        saveEligibility(entity, request);
        return toResponse(entity);
    }

    @Override
    public ServiceConfigurationResponse update(UUID serviceId, ServiceConfigurationRequest request) {
        validatePolicy(request);
        ServiceEntity entity = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
        apply(entity, request);
        entity = serviceRepository.save(entity);
        saveEligibility(entity, request);
        return toResponse(entity);
    }

    private void validatePolicy(ServiceConfigurationRequest request) {
        if (request.getEligiblePropertyTypes() == null || request.getConditionalPropertyTypes() == null) {
            throw new BusinessException("Set eligible and conditional property types before saving the service");
        }
        Set<String> eligible = normalizePropertyTypes(request.getEligiblePropertyTypes());
        Set<String> conditional = normalizePropertyTypes(request.getConditionalPropertyTypes());
        if (!eligible.stream().allMatch(PROPERTY_TYPES::contains)
                || !conditional.stream().allMatch(PROPERTY_TYPES::contains)) {
            throw new BusinessException("Choose supported property types");
        }
        if (eligible.stream().anyMatch(conditional::contains)) {
            throw new BusinessException("A property type cannot be both eligible and conditional");
        }
        if (!Set.of("CORE", "OPTIONAL").contains(request.getCatalogPriority())) {
            throw new BusinessException("Priority must be Core or Optional");
        }
        if (Boolean.TRUE.equals(request.getRemote())
                && (Boolean.TRUE.equals(request.getRequiresVisit())
                || Boolean.TRUE.equals(request.getVisitMandatory())
                || Boolean.TRUE.equals(request.getRequiresGps()))) {
            throw new BusinessException("Remote services cannot require visits or GPS verification");
        }
        if (Boolean.TRUE.equals(request.getVisitMandatory())
                && !Boolean.TRUE.equals(request.getRequiresVisit())) {
            throw new BusinessException("A mandatory visit must also be enabled for the service");
        }
        if (Boolean.TRUE.equals(request.getRequiresGps())
                && !Boolean.TRUE.equals(request.getRequiresVisit())) {
            throw new BusinessException("GPS verification requires a visit-enabled service");
        }
    }

    private void apply(ServiceEntity entity, ServiceConfigurationRequest request) {
        entity.setServiceCode(request.getServiceCode().trim());
        entity.setServiceName(request.getServiceName().trim());
        entity.setCategory(request.getCategory().trim());
        entity.setCatalogGroup(request.getCatalogGroup().trim());
        entity.setCatalogPriority(request.getCatalogPriority().trim().toUpperCase(Locale.ROOT));
        entity.setDeliveryPolicy(blankToNull(request.getDeliveryPolicy()));
        entity.setFrequency(blankToNull(request.getFrequency()));
        entity.setCatalogNotes(blankToNull(request.getCatalogNotes()));
        entity.setConditionalPropertyTypes(normalizePropertyTypes(request.getConditionalPropertyTypes()).toArray(String[]::new));
        entity.setBasePrice(request.getBasePrice());
        entity.setActiveFlag(request.getActiveFlag());
        entity.setRequiresVisit(request.getRequiresVisit());
        entity.setVisitMandatory(request.getVisitMandatory());
        entity.setMaxVisitsAllowed(request.getMaxVisitsAllowed());
        entity.setRequiresGps(request.getRequiresGps());
        entity.setGpsRadiusKm(request.getGpsRadiusKm());
        entity.setRequiresBeforePhoto(request.getRequiresBeforePhoto());
        entity.setRequiresAfterPhoto(request.getRequiresAfterPhoto());
        entity.setRequiresSignature(request.getRequiresSignature());
        entity.setRequiresDocumentUpload(request.getRequiresDocumentUpload());
        entity.setRemote(request.getRemote());
        entity.setBillable(request.getBillable());
    }

    private ServiceConfigurationResponse toResponse(ServiceEntity entity) {
        return new ServiceConfigurationResponse(entity.getServiceId(), entity.getServiceCode(),
                entity.getServiceName(), entity.getCategory(), entity.getCatalogGroup(), entity.getCatalogPriority(),
                entity.getDeliveryPolicy(), entity.getFrequency(), entity.getCatalogNotes(),
                eligiblePropertyTypes(entity), java.util.Arrays.stream(
                        entity.getConditionalPropertyTypes() == null ? new String[0] : entity.getConditionalPropertyTypes()).toList(),
                entity.getBasePrice(), entity.getActiveFlag(),
                entity.getRequiresVisit(), entity.getVisitMandatory(), entity.getMaxVisitsAllowed(),
                entity.getRequiresGps(), entity.getGpsRadiusKm(), entity.getRequiresBeforePhoto(), entity.getRequiresAfterPhoto(),
                entity.getRequiresSignature(), entity.getRequiresDocumentUpload(), entity.getRemote(),
                entity.getBillable());
    }

    private void saveEligibility(ServiceEntity service, ServiceConfigurationRequest request) {
        eligibilityRuleRepository.findByService_ServiceIdAndStatusOrderByPriorityAsc(service.getServiceId(), "ACTIVE")
                .stream()
                .filter(existing -> !"PROPERTY_TYPE_COVERAGE".equals(existing.getRuleName()))
                .filter(this::isPropertyTypeRule)
                .forEach(existing -> {
                    existing.setStatus("INACTIVE");
                    eligibilityRuleRepository.save(existing);
                });
        ServiceEligibilityRuleEntity rule = eligibilityRuleRepository
                .findByService_ServiceIdAndRuleName(service.getServiceId(), "PROPERTY_TYPE_COVERAGE")
                .orElseGet(() -> {
                    ServiceEligibilityRuleEntity created = new ServiceEligibilityRuleEntity();
                    created.setEligibilityRuleId(UUID.randomUUID());
                    created.setService(service);
                    created.setRuleName("PROPERTY_TYPE_COVERAGE");
                    return created;
                });
        try {
            rule.setRuleDefinition(JSON.writeValueAsString(java.util.Map.of(
                    "field", "PROPERTY_TYPE", "operator", "IN",
                    "value", normalizePropertyTypes(request.getEligiblePropertyTypes()))));
        } catch (com.fasterxml.jackson.core.JsonProcessingException exception) {
            throw new BusinessException("Property eligibility could not be saved");
        }
        rule.setPriority(100);
        rule.setStatus("ACTIVE");
        eligibilityRuleRepository.save(rule);
    }

    private List<String> eligiblePropertyTypes(ServiceEntity service) {
        return eligibilityRuleRepository.findByService_ServiceIdAndStatusOrderByPriorityAsc(service.getServiceId(), "ACTIVE")
                .stream()
                .filter(this::isPropertyTypeRule)
                .map(rule -> propertyTypeValues(rule.getRuleDefinition()))
                .flatMap(List::stream)
                .distinct()
                .toList();
    }

    private boolean isPropertyTypeRule(ServiceEligibilityRuleEntity rule) {
        try {
            return "PROPERTY_TYPE".equals(JSON.readTree(rule.getRuleDefinition()).path("field").asText());
        } catch (Exception ignored) {
            return false;
        }
    }

    private List<String> propertyTypeValues(String definition) {
        try {
            JsonNode values = JSON.readTree(definition).path("value");
            if (values.isArray()) {
                List<String> types = new ArrayList<>();
                values.forEach(value -> types.add(value.asText()));
                return List.copyOf(types);
            }
            return values.isTextual() ? List.of(values.asText()) : List.of();
        } catch (Exception ignored) {
            return List.of();
        }
    }

    private static Set<String> normalizePropertyTypes(List<String> values) {
        return values.stream().filter(Objects::nonNull)
                .map(value -> value.trim().toUpperCase(Locale.ROOT))
                .filter(value -> !value.isBlank()).collect(java.util.stream.Collectors.toUnmodifiableSet());
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
