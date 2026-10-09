package com.propertypilot.web.controller;

import com.propertypilot.application.dto.ServiceConfigurationRequest;
import com.propertypilot.application.dto.ServiceConfigurationResponse;
import com.propertypilot.application.service.ServiceCatalogConfigurationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/services")
public class ServiceCatalogController {
    private final ServiceCatalogConfigurationService service;

    public ServiceCatalogController(ServiceCatalogConfigurationService service) {
        this.service = service;
    }

    @GetMapping
    public List<ServiceConfigurationResponse> getActiveServices() {
        return service.getActiveServices();
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ServiceConfigurationResponse> getAllServices() {
        return service.getAllServices();
    }

    @PostMapping("/admin")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ServiceConfigurationResponse create(@Valid @RequestBody ServiceConfigurationRequest request) {
        return service.create(request);
    }

    @PutMapping("/admin/{serviceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ServiceConfigurationResponse update(@PathVariable UUID serviceId,
                                               @Valid @RequestBody ServiceConfigurationRequest request) {
        return service.update(serviceId, request);
    }
}
