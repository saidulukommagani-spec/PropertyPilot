package com.propertypilot.web.controller;

import com.propertypilot.application.dto.PropertyCreateRequest;
import com.propertypilot.application.dto.PropertyResponse;
import com.propertypilot.application.dto.PropertyUpdateRequest;
import com.propertypilot.application.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Property Management",
        description = "Property lifecycle management APIs"
)
@RestController
@RequestMapping("/api/v1/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(
            PropertyService propertyService) {

        this.propertyService = propertyService;
    }

    @Operation(
            summary = "Create Property",
            description = "Creates a new property"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Property created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PropertyResponse createProperty(
            @Valid
            @RequestBody
            PropertyCreateRequest request) {

        return propertyService.createProperty(
                request);
    }

    @Operation(
            summary = "Get Property By Id",
            description = "Returns property details by property identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Property found"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Property not found"
            )
    })
    @GetMapping("/{propertyId}")
    public PropertyResponse getPropertyById(
            @PathVariable
            UUID propertyId) {

        return propertyService.getPropertyById(
                propertyId);
    }

    @Operation(
            summary = "Get All Properties",
            description = "Returns all available properties"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Properties retrieved successfully"
            )
    })
    @GetMapping
    public List<PropertyResponse> getAllProperties() {

        return propertyService.getAllProperties();
    }

    @Operation(
            summary = "Get Archived Properties",
            description = "Returns archived properties"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Archived properties retrieved successfully"
            )
    })
    @GetMapping("/archived")
    public List<PropertyResponse> getArchivedProperties() {

        return propertyService.getArchivedProperties();
    }

    @Operation(
            summary = "Update Property",
            description = "Updates an existing property"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Property updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Property not found"
            )
    })
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

    @Operation(
            summary = "Restore Property",
            description = "Restores an archived property"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Property restored successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Property not found"
            )
    })
    @PatchMapping("/{propertyId}/restore")
    public ResponseEntity<PropertyResponse> restoreProperty(
            @PathVariable UUID propertyId) {

        return ResponseEntity.ok(
                propertyService.restoreProperty(
                        propertyId));
    }

    @Operation(
            summary = "Archive Property",
            description = "Archives an active property"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Property archived successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Property not found"
            )
    })
    @PatchMapping("/{propertyId}/archive")
    public ResponseEntity<PropertyResponse> archiveProperty(
            @PathVariable
            UUID propertyId) {

        return ResponseEntity.ok(
                propertyService.archiveProperty(
                        propertyId));
    }
}