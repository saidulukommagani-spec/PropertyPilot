package com.propertypilot.web.controller;

import com.propertypilot.application.dto.AssignAgentRequest;
import com.propertypilot.application.dto.CreateServiceRequestRequest;
import com.propertypilot.application.dto.ServiceRequestResponse;
import com.propertypilot.application.dto.UpdateServiceRequestRequest;
import com.propertypilot.application.dto.UpdateServiceRequestStatusRequest;
import com.propertypilot.application.service.ServiceRequestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Service Request Management",
        description = "Service Request lifecycle APIs"
)
@RestController
@RequestMapping("/api/service-requests")
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;

    public ServiceRequestController(
            ServiceRequestService serviceRequestService) {

        this.serviceRequestService =
                serviceRequestService;
    }

    @Operation(
            summary = "Create Service Request",
            description = "Creates a new service request"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service request created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ServiceRequestResponse createRequest(
            @RequestBody
            CreateServiceRequestRequest request) {

        return serviceRequestService
                .createRequest(request);
    }

    @Operation(
            summary = "Get Service Request",
            description = "Returns service request by ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request found"),
            @ApiResponse(responseCode = "404", description = "Request not found")
    })
    @GetMapping("/{serviceRequestId}")
    public ServiceRequestResponse getRequest(
            @PathVariable UUID serviceRequestId) {

        return serviceRequestService
                .getRequest(serviceRequestId);
    }

    @Operation(
            summary = "Get Customer Requests",
            description = "Returns all requests for a customer"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Requests retrieved")
    })
    @GetMapping("/customer/{customerId}")
    public List<ServiceRequestResponse>
    getCustomerRequests(
            @PathVariable UUID customerId) {

        return serviceRequestService
                .getCustomerRequests(customerId);
    }

    @Operation(
            summary = "Update Service Request",
            description = "Updates service request details"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request updated"),
            @ApiResponse(responseCode = "404", description = "Request not found")
    })
    @PutMapping("/{serviceRequestId}")
    public ServiceRequestResponse updateRequest(
            @PathVariable UUID serviceRequestId,
            @RequestBody
            UpdateServiceRequestRequest request) {

        return serviceRequestService
                .updateRequest(
                        serviceRequestId,
                        request);
    }

    @Operation(
            summary = "Assign Service Request",
            description = "Assign request to an agent"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Assigned successfully")
    })
    @PostMapping("/{id}/assign")
    public ServiceRequestResponse assignRequest(
            @PathVariable UUID id,
            @RequestParam UUID assignedTo,
            @RequestParam UUID assignedBy,
            @RequestParam(required = false)
            String reason) {

        return serviceRequestService.assignRequest(
                id,
                assignedTo,
                assignedBy,
                reason);
    }

    @Operation(
            summary = "Accept Service Request",
            description = "Accept assigned request"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Accepted successfully")
    })
    @PostMapping("/{id}/accept")
    public ServiceRequestResponse acceptRequest(
            @PathVariable UUID id,
            @RequestBody
            UpdateServiceRequestStatusRequest request) {

        return serviceRequestService.acceptRequest(
                id,
                request.getChangeReason());
    }

    @Operation(
            summary = "Start Service Request",
            description = "Start work on request"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Started successfully")
    })
    @PostMapping("/{id}/start")
    public ServiceRequestResponse startRequest(
            @PathVariable UUID id,
            @RequestBody
            UpdateServiceRequestStatusRequest request) {

        return serviceRequestService.startRequest(
                id,
                request.getChangeReason());
    }

    @Operation(
            summary = "Complete Service Request",
            description = "Complete request"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Completed successfully")
    })
    @PostMapping("/{id}/complete")
    public ServiceRequestResponse completeRequest(
            @PathVariable UUID id,
            @RequestBody
            UpdateServiceRequestStatusRequest request) {

        return serviceRequestService.completeRequest(
                id,
                request.getChangeReason());
    }

    @Operation(
            summary = "Cancel Service Request",
            description = "Cancel request"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cancelled successfully")
    })
    @PostMapping("/{id}/cancel")
    public ServiceRequestResponse cancelRequest(
            @PathVariable UUID id,
            @RequestBody
            UpdateServiceRequestStatusRequest request) {

        return serviceRequestService.cancelRequest(
                id,
                request.getChangeReason());
    }

    @Operation(
            summary = "Assign Agent",
            description = "Assign agent to request"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agent assigned")
    })
    @PostMapping("/{serviceRequestId}/assign-agent")
    public ResponseEntity<ServiceRequestResponse>
    assignAgent(
            @PathVariable UUID serviceRequestId,
            @RequestBody AssignAgentRequest request) {

        return ResponseEntity.ok(
                serviceRequestService.assignAgent(
                        serviceRequestId,
                        request.getAgentId()));
    }
}