package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateCustomerSearchHistoryRequest;
import com.propertypilot.application.dto.CustomerSearchHistoryResponse;
import com.propertypilot.application.service.CustomerSearchHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@Tag(
        name = "Customer Search History",
        description = "APIs for managing customer search history records"
)
public class CustomerSearchHistoryController {

    private final CustomerSearchHistoryService
            customerSearchHistoryService;

    public CustomerSearchHistoryController(
            CustomerSearchHistoryService customerSearchHistoryService) {

        this.customerSearchHistoryService =
                customerSearchHistoryService;
    }

    @Operation(
            summary = "Create customer search history",
            description = "Creates a new search history record for a customer"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Search history created successfully"),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request")
    })
    @PostMapping("/{customerId}/search-history")
    public ResponseEntity<CustomerSearchHistoryResponse>
    createSearchHistory(

            @Parameter(
                    description = "Customer ID",
                    required = true)
            @PathVariable UUID customerId,

            @RequestBody
            CreateCustomerSearchHistoryRequest request) {

        return ResponseEntity.ok(
                customerSearchHistoryService
                        .createSearchHistory(
                                customerId,
                                request));
    }

    @Operation(
            summary = "Get customer search history",
            description = "Returns all search history records for a customer"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Search history retrieved successfully")
    })
    @GetMapping("/{customerId}/search-history")
    public ResponseEntity<
            List<CustomerSearchHistoryResponse>>
    getCustomerSearchHistory(

            @Parameter(
                    description = "Customer ID",
                    required = true)
            @PathVariable UUID customerId) {

        return ResponseEntity.ok(
                customerSearchHistoryService
                        .getCustomerSearchHistory(
                                customerId));
    }

    @Operation(
            summary = "Get search history by ID",
            description = "Returns search history details by search history ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Search history retrieved successfully"),
            @ApiResponse(responseCode = "404",
                    description = "Search history not found")
    })
    @GetMapping("/search-history/{searchHistoryId}")
    public ResponseEntity<
            CustomerSearchHistoryResponse>
    getSearchHistory(

            @Parameter(
                    description = "Search History ID",
                    required = true)
            @PathVariable UUID searchHistoryId) {

        return ResponseEntity.ok(
                customerSearchHistoryService
                        .getSearchHistory(
                                searchHistoryId));
    }
}