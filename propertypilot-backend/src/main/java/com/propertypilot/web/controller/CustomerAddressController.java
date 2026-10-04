package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateCustomerAddressRequest;
import com.propertypilot.application.dto.CustomerAddressResponse;
import com.propertypilot.application.dto.UpdateCustomerAddressRequest;
import com.propertypilot.application.service.CustomerAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@Tag(
        name = "Customer Address Management",
        description = "APIs for managing customer addresses"
)
public class CustomerAddressController {

    private final CustomerAddressService service;

    public CustomerAddressController(
            CustomerAddressService service) {

        this.service = service;
    }

    @Operation(
            summary = "Create Customer Address",
            description = "Creates a new address for a customer"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Address created successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation =
                                            CustomerAddressResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request")
    })
    @PostMapping("/{customerId}/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerAddressResponse createAddress(
            @Parameter(description = "Customer ID")
            @PathVariable UUID customerId,

            @Valid
            @RequestBody
            CreateCustomerAddressRequest request) {

        return service.createAddress(
                customerId,
                request);
    }

    @Operation(
            summary = "Get Customer Addresses",
            description = "Returns all addresses belonging to a customer"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Addresses retrieved successfully")
    })
    @GetMapping("/{customerId}/addresses")
    public List<CustomerAddressResponse> getAddresses(
            @Parameter(description = "Customer ID")
            @PathVariable UUID customerId) {

        return service.getCustomerAddresses(
                customerId);
    }

    @Operation(
            summary = "Update Customer Address",
            description = "Updates an existing customer address"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Address updated successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Address not found")
    })
    @PutMapping("/addresses/{addressId}")
    public CustomerAddressResponse updateAddress(
            @Parameter(description = "Address ID")
            @PathVariable UUID addressId,

            @Valid
            @RequestBody
            UpdateCustomerAddressRequest request) {

        return service.updateAddress(
                addressId,
                request);
    }

    @Operation(
            summary = "Delete Customer Address",
            description = "Deletes a customer address"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Address deleted successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Address not found")
    })
    @DeleteMapping("/addresses/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(
            @Parameter(description = "Address ID")
            @PathVariable UUID addressId) {

        service.deleteAddress(
                addressId);
    }
}