package com.propertypilot.web.controller;

import com.propertypilot.application.dto.CreateCustomerFavoriteRequest;
import com.propertypilot.application.dto.CustomerFavoriteResponse;
import com.propertypilot.application.service.CustomerFavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
@RequestMapping("/api/customers")
@Tag(
        name = "Customer Favorites",
        description = "Customer favorite properties and listings management APIs"
)
public class CustomerFavoriteController {

    private final CustomerFavoriteService
            customerFavoriteService;

    public CustomerFavoriteController(
            CustomerFavoriteService customerFavoriteService) {

        this.customerFavoriteService =
                customerFavoriteService;
    }

    @Operation(
            summary = "Create Customer Favorite",
            description = """
                    Adds a property or marketplace listing
                    to customer favorites.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Favorite created successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation =
                                            CustomerFavoriteResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request"
            )
    })
    @PostMapping("/{customerId}/favorites")
    public ResponseEntity<CustomerFavoriteResponse>
    createFavorite(

            @Parameter(
                    description = "Customer ID",
                    required = true
            )
            @PathVariable
            UUID customerId,

            @Valid
            @RequestBody
            CreateCustomerFavoriteRequest request) {

        return ResponseEntity.ok(
                customerFavoriteService
                        .createFavorite(
                                customerId,
                                request));
    }

    @Operation(
            summary = "Get Customer Favorites",
            description = """
                    Retrieves all favorites
                    for a customer.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Favorites retrieved successfully",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation =
                                                    CustomerFavoriteResponse.class
                                    )
                            )
                    )
            )
    })
    @GetMapping("/{customerId}/favorites")
    public ResponseEntity<
            List<CustomerFavoriteResponse>>
    getCustomerFavorites(

            @Parameter(
                    description = "Customer ID",
                    required = true
            )
            @PathVariable
            UUID customerId) {

        return ResponseEntity.ok(
                customerFavoriteService
                        .getCustomerFavorites(
                                customerId));
    }

    @Operation(
            summary = "Delete Customer Favorite",
            description = """
                    Removes a favorite item.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Favorite deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Favorite not found"
            )
    })
    @DeleteMapping("/favorites/{favoriteId}")
    public ResponseEntity<Void>
    deleteFavorite(

            @Parameter(
                    description = "Favorite ID",
                    required = true
            )
            @PathVariable
            UUID favoriteId) {

        customerFavoriteService
                .deleteFavorite(
                        favoriteId);

        return ResponseEntity.noContent()
                .build();
    }
}