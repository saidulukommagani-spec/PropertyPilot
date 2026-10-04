package com.propertypilot.web.controller;

import com.propertypilot.application.dto.RevenueDashboardResponse;
import com.propertypilot.application.service.RevenueDashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/revenue")
@RequiredArgsConstructor
@Tag(
        name = "Revenue Dashboard",
        description = "Revenue analytics and billing dashboard APIs"
)
public class RevenueDashboardController {

    private final RevenueDashboardService
            revenueDashboardService;

    @Operation(
            summary = "Get Revenue Dashboard",
            description =
                    "Returns overall revenue, invoice and collection analytics"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Revenue dashboard fetched successfully"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            )
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RevenueDashboardResponse>
    getRevenueDashboard() {

        return ResponseEntity.ok(
                revenueDashboardService
                        .getRevenueDashboard()
        );
    }
}