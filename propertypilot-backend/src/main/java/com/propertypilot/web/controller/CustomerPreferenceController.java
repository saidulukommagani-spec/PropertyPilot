package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateCustomerPreferenceRequest;
import com.propertypilot.application.dto.CustomerPreferenceResponse;
import com.propertypilot.application.service.CustomerPreferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@Tag(
        name = "Customer Preference Management",
        description = "APIs for managing customer preferences, communication settings, language and timezone preferences"
)
public class CustomerPreferenceController {

    private final CustomerPreferenceService
            customerPreferenceService;

    public CustomerPreferenceController(
            CustomerPreferenceService customerPreferenceService) {

        this.customerPreferenceService =
                customerPreferenceService;
    }

    @PutMapping("/{customerId}/preferences")
    @Operation(
            summary = "Save customer preferences",
            description = "Creates or updates customer preferences including language, timezone, communication channels and marketing consent"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Customer preferences saved successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = CustomerPreferenceResponse.class)))
    })
    public ResponseEntity<CustomerPreferenceResponse>
    savePreference(

            @Parameter(
                    description = "Customer ID",
                    required = true)
            @PathVariable
            UUID customerId,

            @RequestBody
            CreateCustomerPreferenceRequest request) {

        return ResponseEntity.ok(
                customerPreferenceService.savePreference(
                        customerId,
                        request));
    }

    @GetMapping("/{customerId}/preferences")
    @Operation(
            summary = "Get customer preferences",
            description = "Retrieves saved customer preferences"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Customer preferences retrieved successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = CustomerPreferenceResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer preferences not found")
    })
    public ResponseEntity<CustomerPreferenceResponse>
    getPreference(

            @Parameter(
                    description = "Customer ID",
                    required = true)
            @PathVariable
            UUID customerId) {

        return ResponseEntity.ok(
                customerPreferenceService.getPreference(
                        customerId));
    }
}