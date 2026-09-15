package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateCustomerPreferenceRequest;
import com.propertypilot.application.dto.CustomerPreferenceResponse;
import com.propertypilot.application.service.CustomerPreferenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerPreferenceController {

    private final CustomerPreferenceService
            customerPreferenceService;

    public CustomerPreferenceController(
            CustomerPreferenceService customerPreferenceService) {

        this.customerPreferenceService =
                customerPreferenceService;
    }

    @PutMapping("/{customerId}/preferences")
    public ResponseEntity<CustomerPreferenceResponse>
    savePreference(
            @PathVariable UUID customerId,
            @RequestBody
            CreateCustomerPreferenceRequest request) {

        return ResponseEntity.ok(
                customerPreferenceService.savePreference(
                        customerId,
                        request));
    }

    @GetMapping("/{customerId}/preferences")
    public ResponseEntity<CustomerPreferenceResponse>
    getPreference(
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                customerPreferenceService.getPreference(
                        customerId));
    }
}