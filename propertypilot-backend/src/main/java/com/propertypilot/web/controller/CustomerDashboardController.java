package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CustomerDashboardResponse;
import com.propertypilot.application.service.CustomerDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/customer-dashboard")
@Tag(
        name = "Customer Dashboard",
        description = "Customer dashboard and summary statistics APIs"
)
public class CustomerDashboardController {

    private final CustomerDashboardService
            customerDashboardService;

    public CustomerDashboardController(
            CustomerDashboardService customerDashboardService) {

        this.customerDashboardService =
                customerDashboardService;
    }

    @Operation(
            summary = "Get Customer Dashboard",
            description = """
                    Retrieves dashboard metrics for a customer including:
                    - Properties
                    - Service Requests
                    - Subscriptions
                    - Invoices
                    - Payments
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Dashboard retrieved successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation =
                                            CustomerDashboardResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found"
            )
    })
    @GetMapping("/{customerId}")
    public CustomerDashboardResponse getDashboard(

            @Parameter(
                    description = "Customer ID",
                    required = true
            )
            @PathVariable
            UUID customerId) {

        return customerDashboardService
                .getDashboard(customerId);
    }
}