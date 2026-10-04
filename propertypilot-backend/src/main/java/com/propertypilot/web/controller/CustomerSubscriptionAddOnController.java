package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateCustomerSubscriptionAddOnRequest;
import com.propertypilot.application.dto.CustomerSubscriptionAddOnResponse;
import com.propertypilot.application.service.CustomerSubscriptionAddOnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customer-subscriptions")
@Tag(
        name = "Customer Subscription Add-On Management",
        description = "APIs for managing customer subscription add-ons"
)
public class CustomerSubscriptionAddOnController {

    private final CustomerSubscriptionAddOnService
            customerSubscriptionAddOnService;

    public CustomerSubscriptionAddOnController(
            CustomerSubscriptionAddOnService
                    customerSubscriptionAddOnService) {

        this.customerSubscriptionAddOnService =
                customerSubscriptionAddOnService;
    }

    @Operation(
            summary = "Create subscription add-on",
            description = "Creates a new add-on for a customer subscription"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Add-on created successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation =
                                            CustomerSubscriptionAddOnResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request")
    })
    @PostMapping("/{customerSubscriptionId}/add-ons")
    public ResponseEntity<CustomerSubscriptionAddOnResponse>
    createAddOn(

            @Parameter(
                    description = "Customer Subscription ID",
                    required = true)
            @PathVariable
            UUID customerSubscriptionId,

            @Valid
            @RequestBody
            CreateCustomerSubscriptionAddOnRequest request) {

        return ResponseEntity.ok(
                customerSubscriptionAddOnService
                        .createAddOn(
                                customerSubscriptionId,
                                request));
    }

    @Operation(
            summary = "Get subscription add-ons",
            description = "Returns all add-ons associated with a customer subscription"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Add-ons retrieved successfully")
    })
    @GetMapping("/{customerSubscriptionId}/add-ons")
    public ResponseEntity<List<CustomerSubscriptionAddOnResponse>>
    getAddOns(

            @Parameter(
                    description = "Customer Subscription ID",
                    required = true)
            @PathVariable
            UUID customerSubscriptionId) {

        return ResponseEntity.ok(
                customerSubscriptionAddOnService
                        .getSubscriptionAddOns(
                                customerSubscriptionId));
    }

    @Operation(
            summary = "Get add-on by ID",
            description = "Returns details of a specific subscription add-on"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Add-on retrieved successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Add-on not found")
    })
    @GetMapping("/add-ons/{addOnId}")
    public ResponseEntity<CustomerSubscriptionAddOnResponse>
    getAddOn(

            @Parameter(
                    description = "Add-On ID",
                    required = true)
            @PathVariable
            UUID addOnId) {

        return ResponseEntity.ok(
                customerSubscriptionAddOnService
                        .getAddOn(addOnId));
    }

    @Operation(
            summary = "Delete add-on",
            description = "Deletes a subscription add-on"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Add-on deleted successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Add-on not found")
    })
    @DeleteMapping("/add-ons/{addOnId}")
    public ResponseEntity<Void>
    deleteAddOn(

            @Parameter(
                    description = "Add-On ID",
                    required = true)
            @PathVariable
            UUID addOnId) {

        customerSubscriptionAddOnService
                .deleteAddOn(addOnId);

        return ResponseEntity.noContent()
                .build();
    }
}