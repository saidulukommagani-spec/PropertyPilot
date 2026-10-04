package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreatePropertyOwnerRequest;
import com.propertypilot.application.dto.PropertyOwnerResponse;
import com.propertypilot.application.dto.UpdatePropertyOwnerRequest;
import com.propertypilot.application.service.PropertyOwnerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Tag(
        name = "Property Owner Management",
        description = "APIs for managing property ownership records"
)
public class PropertyOwnerController {

    private final PropertyOwnerService
            propertyOwnerService;

    public PropertyOwnerController(
            PropertyOwnerService propertyOwnerService) {

        this.propertyOwnerService =
                propertyOwnerService;
    }

    @Operation(
            summary = "Create property owner",
            description = "Creates a new ownership record for a property"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Owner created successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation =
                                            PropertyOwnerResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request"),
            @ApiResponse(responseCode = "404",
                    description = "Property not found"),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error")
    })
    @PostMapping("/property-owners")
    public ResponseEntity<PropertyOwnerResponse>
    createOwner(
            @Valid
            @RequestBody
            CreatePropertyOwnerRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        propertyOwnerService
                                .createOwner(request));
    }

    @Operation(
            summary = "Get property owner",
            description = "Retrieve ownership details by owner ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Owner found"),
            @ApiResponse(responseCode = "404",
                    description = "Owner not found")
    })
    @GetMapping("/property-owners/{propertyOwnerId}")
    public ResponseEntity<PropertyOwnerResponse>
    getOwner(
            @Parameter(
                    description = "Property Owner ID",
                    required = true)
            @PathVariable
            UUID propertyOwnerId) {

        return ResponseEntity.ok(
                propertyOwnerService
                        .getOwner(
                                propertyOwnerId));
    }

    @Operation(
            summary = "Get owners by property",
            description = "Retrieve all owners associated with a property"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Owners retrieved successfully")
    })
    @GetMapping("/properties/{propertyId}/owners")
    public ResponseEntity<
            List<PropertyOwnerResponse>>
    getOwnersByProperty(
            @Parameter(
                    description = "Property ID",
                    required = true)
            @PathVariable
            UUID propertyId) {

        return ResponseEntity.ok(
                propertyOwnerService
                        .getOwnersByProperty(
                                propertyId));
    }

    @Operation(
            summary = "Update property owner",
            description = "Update ownership information"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Owner updated successfully"),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request"),
            @ApiResponse(responseCode = "404",
                    description = "Owner not found")
    })
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

    @Operation(
            summary = "Delete property owner",
            description = "Delete ownership record"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204",
                    description = "Owner deleted successfully"),
            @ApiResponse(responseCode = "404",
                    description = "Owner not found")
    })
    @DeleteMapping("/property-owners/{propertyOwnerId}")
    public ResponseEntity<Void>
    deleteOwner(
            @PathVariable
            UUID propertyOwnerId) {

        propertyOwnerService
                .deleteOwner(
                        propertyOwnerId);

        return ResponseEntity
                .noContent()
                .build();
    }
}