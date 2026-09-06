package com.propertypilot.web.controller;

import com.propertypilot.application.dto.PropertyCreateRequest;
import com.propertypilot.application.dto.PropertyResponse;
import com.propertypilot.application.dto.PropertyUpdateRequest;
import com.propertypilot.application.service.PropertyService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(
            PropertyService propertyService) {

        this.propertyService = propertyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PropertyResponse createProperty(
            @Valid
            @RequestBody
            PropertyCreateRequest request) {

        return propertyService.createProperty(
                request);
    }

    @GetMapping("/{propertyId}")
    public PropertyResponse getPropertyById(
            @PathVariable
            UUID propertyId) {

        return propertyService.getPropertyById(
                propertyId);
    }

    @GetMapping
    public List<PropertyResponse> getAllProperties() {

        return propertyService.getAllProperties();
    }

    /*
     * Admin-only endpoint.
     * Returns archived properties.
     */
    @GetMapping("/archived")
    public List<PropertyResponse> getArchivedProperties() {

        return propertyService.getArchivedProperties();
    }
    
    @PutMapping("/{propertyId}")
    public PropertyResponse updateProperty(
            @PathVariable
            UUID propertyId,

            @Valid
            @RequestBody
            PropertyUpdateRequest request) {

        return propertyService.updateProperty(
                propertyId,
                request);
    }

    @PatchMapping("/{propertyId}/restore")
public ResponseEntity<PropertyResponse> restoreProperty(
        @PathVariable UUID propertyId) {

    return ResponseEntity.ok(
            propertyService.restoreProperty(
                    propertyId));
}

    /*
     * Soft delete.
     * Property status becomes ARCHIVED.
     */
    @PatchMapping("/{propertyId}/archive")
    public ResponseEntity<PropertyResponse> archiveProperty(
            @PathVariable
            UUID propertyId) {

        return ResponseEntity.ok(
                propertyService.archiveProperty(
                        propertyId));
    }
}