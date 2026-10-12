package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.ServiceConfigurationRequest;
import com.propertypilot.infrastructure.persistence.entity.ServiceEntity;
import com.propertypilot.infrastructure.persistence.repository.ServiceRepository;
import com.propertypilot.infrastructure.persistence.repository.PropertyRepository;
import com.propertypilot.application.service.EligibilityEngine;
import com.propertypilot.security.SecurityService;
import com.propertypilot.infrastructure.persistence.entity.Property;
import com.propertypilot.infrastructure.persistence.entity.ServiceEntity;
import com.propertypilot.web.exception.BusinessException;
import org.springframework.security.access.AccessDeniedException;
import com.propertypilot.web.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceCatalogConfigurationServiceImplTest {
    @Mock private ServiceRepository serviceRepository;
    @Mock private PropertyRepository propertyRepository;
    @Mock private EligibilityEngine eligibilityEngine;
    @Mock private SecurityService securityService;
    @InjectMocks private ServiceCatalogConfigurationServiceImpl service;

    @Test
    void createPersistsLaunchPolicyFlags() {
        ServiceConfigurationRequest request = request();
        request.setRequiresVisit(true);
        request.setVisitMandatory(true);
        request.setRequiresGps(true);
        request.setRequiresBeforePhoto(true);
        request.setRequiresSignature(true);
        when(serviceRepository.save(any(ServiceEntity.class))).thenAnswer(invocation -> {
            ServiceEntity entity = invocation.getArgument(0);
            entity.setServiceId(UUID.randomUUID());
            return entity;
        });

        var response = service.create(request);

        assertThat(response.requiresVisit()).isTrue();
        assertThat(response.visitMandatory()).isTrue();
        assertThat(response.requiresGps()).isTrue();
        assertThat(response.gpsRadiusKm()).isEqualByComparingTo("0.200");
        assertThat(response.requiresBeforePhoto()).isTrue();
        assertThat(response.requiresSignature()).isTrue();
        assertThat(response.billable()).isTrue();
        verify(serviceRepository).save(any(ServiceEntity.class));
    }

    @Test
    void remoteServiceCannotRequireVisitOrGps() {
        ServiceConfigurationRequest request = request();
        request.setRemote(true);
        request.setRequiresVisit(true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Remote services");
    }

    @Test
    void eligibleServiceTypesAndServicesUseStoredPropertyAndActiveEligibleCatalog() {
        UUID propertyId = UUID.randomUUID();
        Property property = new Property();
        property.setPropertyId(propertyId);
        ServiceEntity inspection = catalogService("INSPECTION", true);
        ServiceEntity documents = catalogService("DOCUMENTS", true);
        ServiceEntity inactive = catalogService("INSPECTION", false);
        ServiceEntity ineligible = catalogService("REPAIR", true);
        when(propertyRepository.findById(propertyId)).thenReturn(java.util.Optional.of(property));
        when(serviceRepository.findByActiveFlagTrue()).thenReturn(java.util.List.of(inspection, documents, inactive, ineligible));
        org.mockito.Mockito.lenient().doThrow(new BusinessException("not eligible"))
                .when(eligibilityEngine).validateEligibility(ineligible.getServiceId(), propertyId);

        var types = service.getEligibleServiceTypes(propertyId);
        var services = service.getEligibleServices(propertyId, "inspection");

        assertThat(types).extracting("code").containsExactly("DOCUMENTS", "INSPECTION");
        assertThat(types).extracting("label").containsExactly("Documents", "Inspection");
        assertThat(services).extracting("serviceId").containsExactly(inspection.getServiceId());
        verify(securityService, org.mockito.Mockito.times(2)).validatePropertyOwnership(property);
        verify(eligibilityEngine, org.mockito.Mockito.times(2)).validateEligibility(inspection.getServiceId(), propertyId);
        verify(eligibilityEngine, org.mockito.Mockito.times(2)).validateEligibility(documents.getServiceId(), propertyId);
    }

    @Test
    void eligibleCatalogRejectsUnauthorizedPropertyAndUnknownTypeAndDiscoversNewTypesFromCatalog() {
        UUID propertyId = UUID.randomUUID();
        Property property = new Property();
        when(propertyRepository.findById(propertyId)).thenReturn(java.util.Optional.of(property));
        org.mockito.Mockito.doThrow(new AccessDeniedException("forbidden"))
                .when(securityService).validatePropertyOwnership(property);
        assertThatThrownBy(() -> service.getEligibleServiceTypes(propertyId))
                .isInstanceOf(AccessDeniedException.class);

        org.mockito.Mockito.doNothing().when(securityService).validatePropertyOwnership(property);
        assertThatThrownBy(() -> service.getEligibleServices(propertyId, "UNKNOWN"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("unavailable");
        verify(serviceRepository).findByActiveFlagTrue();
    }

    private ServiceEntity catalogService(String category, boolean active) {
        ServiceEntity entity = new ServiceEntity();
        entity.setServiceId(UUID.randomUUID());
        entity.setCategory(category);
        entity.setServiceName(category + " service");
        entity.setActiveFlag(active);
        return entity;
    }

    private ServiceConfigurationRequest request() {
        ServiceConfigurationRequest request = new ServiceConfigurationRequest();
        request.setServiceCode("PROPERTY_AUDIT");
        request.setServiceName("Property Audit");
        request.setCategory("PROPERTY_HEALTH");
        request.setBasePrice(BigDecimal.ZERO);
        request.setActiveFlag(true);
        request.setRequiresVisit(false);
        request.setVisitMandatory(false);
        request.setMaxVisitsAllowed(0);
        request.setRequiresGps(false);
        request.setGpsRadiusKm(new BigDecimal("0.200"));
        request.setRequiresBeforePhoto(false);
        request.setRequiresAfterPhoto(false);
        request.setRequiresSignature(false);
        request.setRequiresDocumentUpload(false);
        request.setRemote(false);
        request.setBillable(true);
        return request;
    }
}
