package com.propertypilot.application.service;

import com.propertypilot.application.dto.ServiceConfigurationRequest;
import com.propertypilot.application.dto.ServiceConfigurationResponse;

import java.util.List;
import java.util.UUID;

public interface ServiceCatalogConfigurationService {
    List<ServiceConfigurationResponse> getActiveServices();
    List<ServiceConfigurationResponse> getAllServices();
    ServiceConfigurationResponse create(ServiceConfigurationRequest request);
    ServiceConfigurationResponse update(UUID serviceId, ServiceConfigurationRequest request);
}
