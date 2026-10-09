package com.propertypilot.web.controller;

import com.propertypilot.application.dto.AdminCustomerResponse;
import com.propertypilot.application.service.AdminCustomerService;
import com.propertypilot.security.SecurityService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/customers")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCustomerController {
    private final AdminCustomerService service;
    private final SecurityService securityService;

    public AdminCustomerController(AdminCustomerService service, SecurityService securityService) {
        this.service = service;
        this.securityService = securityService;
    }

    @GetMapping
    public List<AdminCustomerResponse> listCustomers() {
        securityService.validateAdminAccess();
        return service.listCustomers();
    }

    @GetMapping("/{customerId}")
    public AdminCustomerResponse getCustomer(@PathVariable UUID customerId) {
        return service.getCustomer(customerId);
    }

    @PatchMapping("/{customerId}/status")
    public AdminCustomerResponse updateStatus(@PathVariable UUID customerId, @RequestBody Map<String, String> request) {
        return service.updateStatus(customerId, request.getOrDefault("status", ""));
    }
}
