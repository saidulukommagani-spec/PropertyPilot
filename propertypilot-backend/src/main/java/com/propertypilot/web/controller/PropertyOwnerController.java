package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreatePropertyOwnerRequest;
import com.propertypilot.application.dto.PropertyOwnerResponse;
import com.propertypilot.application.dto.UpdatePropertyOwnerRequest;
import com.propertypilot.application.service.PropertyOwnerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class PropertyOwnerController {

    private final PropertyOwnerService
            propertyOwnerService;

    public PropertyOwnerController(
            PropertyOwnerService propertyOwnerService) {

        this.propertyOwnerService =
                propertyOwnerService;
    }

    @PostMapping("/property-owners")
    public ResponseEntity<PropertyOwnerResponse>
    createOwner(
            @Valid
            @RequestBody
            CreatePropertyOwnerRequest request) {

        return ResponseEntity.ok(
                propertyOwnerService
                        .createOwner(request));
    }

    @GetMapping("/property-owners/{propertyOwnerId}")
    public ResponseEntity<PropertyOwnerResponse>
    getOwner(
            @PathVariable
            UUID propertyOwnerId) {

        return ResponseEntity.ok(
                propertyOwnerService
                        .getOwner(
                                propertyOwnerId));
    }

    @GetMapping("/properties/{propertyId}/owners")
    public ResponseEntity<
            List<PropertyOwnerResponse>>
    getOwnersByProperty(
            @PathVariable
            UUID propertyId) {

        return ResponseEntity.ok(
                propertyOwnerService
                        .getOwnersByProperty(
                                propertyId));
    }

    @PutMapping("/property-owners/{propertyOwnerId}")
    public ResponseEntity<PropertyOwnerResponse>
    updateOwner(
            @PathVariable
            UUID propertyOwnerId,

            @Valid
            @RequestBody
            UpdatePropertyOwnerRequest request) {

        return ResponseEntity.ok(
                propertyOwnerService
                        .updateOwner(
                                propertyOwnerId,
                                request));
    }

    @DeleteMapping("/property-owners/{propertyOwnerId}")
    public ResponseEntity<Void>
    deleteOwner(
            @PathVariable
            UUID propertyOwnerId) {

        propertyOwnerService
                .deleteOwner(
                        propertyOwnerId);

        return ResponseEntity.noContent()
                .build();
    }
}