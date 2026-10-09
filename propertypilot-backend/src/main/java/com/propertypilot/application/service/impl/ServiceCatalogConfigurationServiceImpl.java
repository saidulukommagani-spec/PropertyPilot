package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.ServiceConfigurationRequest;
import com.propertypilot.application.dto.ServiceConfigurationResponse;
import com.propertypilot.application.service.ServiceCatalogConfigurationService;
import com.propertypilot.infrastructure.persistence.entity.ServiceEntity;
import com.propertypilot.infrastructure.persistence.repository.ServiceRepository;
import com.propertypilot.web.exception.BusinessException;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ServiceCatalogConfigurationServiceImpl implements ServiceCatalogConfigurationService {
    private final ServiceRepository serviceRepository;

    public ServiceCatalogConfigurationServiceImpl(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceConfigurationResponse> getActiveServices() {
        return serviceRepository.findByActiveFlagTrue().stream().map(this::toResponse).toList();
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
        return toResponse(serviceRepository.save(entity));
    }

    @Override
    public ServiceConfigurationResponse update(UUID serviceId, ServiceConfigurationRequest request) {
        validatePolicy(request);
        ServiceEntity entity = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
        apply(entity, request);
        return toResponse(serviceRepository.save(entity));
    }

    private void validatePolicy(ServiceConfigurationRequest request) {
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
                entity.getServiceName(), entity.getCategory(), entity.getBasePrice(), entity.getActiveFlag(),
                entity.getRequiresVisit(), entity.getVisitMandatory(), entity.getMaxVisitsAllowed(),
                entity.getRequiresGps(), entity.getGpsRadiusKm(), entity.getRequiresBeforePhoto(), entity.getRequiresAfterPhoto(),
                entity.getRequiresSignature(), entity.getRequiresDocumentUpload(), entity.getRemote(),
                entity.getBillable());
    }
}
