package com.propertypilot.web.controller;

import com.propertypilot.application.dto.AssignAgentRequest;
import com.propertypilot.application.dto.CreateServiceRequestRequest;
import com.propertypilot.application.dto.ServiceRequestResponse;
import com.propertypilot.application.dto.UpdateServiceRequestRequest;
import com.propertypilot.application.service.ServiceRequestService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.propertypilot.application.dto.UpdateServiceRequestStatusRequest;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/service-requests")
public class ServiceRequestController {

    private final ServiceRequestService
            serviceRequestService;

    public ServiceRequestController(
            ServiceRequestService serviceRequestService) {

        this.serviceRequestService =
                serviceRequestService;
    }

    @PostMapping
    public ServiceRequestResponse createRequest(
            @RequestBody
            CreateServiceRequestRequest request) {

        return serviceRequestService
                .createRequest(request);
    }

    @GetMapping("/{serviceRequestId}")
    public ServiceRequestResponse getRequest(
            @PathVariable UUID serviceRequestId) {

        return serviceRequestService
                .getRequest(serviceRequestId);
    }

    @GetMapping("/customer/{customerId}")
    public List<ServiceRequestResponse>
    getCustomerRequests(
            @PathVariable UUID customerId) {

        return serviceRequestService
                .getCustomerRequests(customerId);
    }

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

@PostMapping("/{id}/accept")
public ServiceRequestResponse acceptRequest(
        @PathVariable UUID id,
        @RequestBody
        UpdateServiceRequestStatusRequest request) {

    return serviceRequestService.acceptRequest(
            id,
            request.getChangeReason());
}

@PostMapping("/{id}/start")
public ServiceRequestResponse startRequest(
        @PathVariable UUID id,
        @RequestBody
        UpdateServiceRequestStatusRequest request) {

    return serviceRequestService.startRequest(
            id,
            request.getChangeReason());
}
@PostMapping("/{id}/complete")
public ServiceRequestResponse completeRequest(
        @PathVariable UUID id,
        @RequestBody
        UpdateServiceRequestStatusRequest request) {

    return serviceRequestService.completeRequest(
            id,
            request.getChangeReason());
}

@PostMapping("/{id}/cancel")
public ServiceRequestResponse cancelRequest(
        @PathVariable UUID id,
        @RequestBody
        UpdateServiceRequestStatusRequest request) {

    return serviceRequestService.cancelRequest(
            id,
            request.getChangeReason());
}

@PostMapping(
        "/{serviceRequestId}/assign-agent")
public ResponseEntity<
        ServiceRequestResponse>
assignAgent(
        @PathVariable UUID serviceRequestId,
        @RequestBody AssignAgentRequest request) {

    return ResponseEntity.ok(
            serviceRequestService.assignAgent(
                    serviceRequestId,
                    request.getAgentId()));
}

}