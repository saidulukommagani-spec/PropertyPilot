package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.ServiceConfigurationRequest;
import com.propertypilot.infrastructure.persistence.entity.ServiceEntity;
import com.propertypilot.infrastructure.persistence.repository.ServiceRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceCatalogConfigurationServiceImplTest {
    @Mock private ServiceRepository serviceRepository;
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
