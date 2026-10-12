package com.propertypilot.application.service;

import com.propertypilot.application.dto.ServiceConfigurationRequest;
import com.propertypilot.application.dto.ServiceConfigurationResponse;

import java.util.List;
import java.util.UUID;
import com.propertypilot.application.dto.EligibleServiceTypeResponse;

public interface ServiceCatalogConfigurationService {
    List<ServiceConfigurationResponse> getActiveServices();
    List<EligibleServiceTypeResponse> getEligibleServiceTypes(UUID propertyId);
    List<ServiceConfigurationResponse> getEligibleServices(UUID propertyId, String serviceType);
    List<ServiceConfigurationResponse> getAllServices();
    ServiceConfigurationResponse create(ServiceConfigurationRequest request);
    ServiceConfigurationResponse update(UUID serviceId, ServiceConfigurationRequest request);
}
