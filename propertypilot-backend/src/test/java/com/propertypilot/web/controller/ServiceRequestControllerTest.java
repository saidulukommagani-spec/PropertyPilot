package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.*;
import com.propertypilot.application.service.ServiceRequestService;
import com.propertypilot.security.CustomUserDetailsService;
import com.propertypilot.security.JwtService;
import com.propertypilot.web.exception.GlobalExceptionHandler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ServiceRequestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ServiceRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ServiceRequestService serviceRequestService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Create Service Request")
    void createRequest_success() throws Exception {

        when(serviceRequestService.createRequest(any()))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        post("/api/service-requests")
                                .contentType("application/json")
                                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void getRequest_success() throws Exception {

        UUID id = UUID.randomUUID();

        when(serviceRequestService.getRequest(id))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        get("/api/service-requests/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    void getCustomerRequests_success() throws Exception {

        UUID customerId = UUID.randomUUID();

        when(serviceRequestService.getCustomerRequests(customerId))
                .thenReturn(List.of(buildResponse()));

        mockMvc.perform(
                        get("/api/service-requests/customer/{customerId}",
                                customerId))
                .andExpect(status().isOk());
    }

    @Test
    void updateRequest_success() throws Exception {

        UUID id = UUID.randomUUID();

        when(serviceRequestService.updateRequest(
                eq(id),
                any(UpdateServiceRequestRequest.class)))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        put("/api/service-requests/{id}", id)
                                .contentType("application/json")
                                .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void assignRequest_success() throws Exception {

        UUID id = UUID.randomUUID();

        when(serviceRequestService.assignRequest(
                any(),
                any(),
                any(),
                any()))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        post("/api/service-requests/{id}/assign", id)
                                .param("assignedTo", UUID.randomUUID().toString())
                                .param("assignedBy", UUID.randomUUID().toString())
                                .param("reason", "Assignment"))
                .andExpect(status().isOk());
    }

    @Test
    void acceptRequest_success() throws Exception {

        UUID id = UUID.randomUUID();

        when(serviceRequestService.acceptRequest(any(), any()))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        post("/api/service-requests/{id}/accept", id)
                                .contentType("application/json")
                                .content("""
                                        {
                                          "changeReason":"Accepted"
                                        }
                                        """))
                .andExpect(status().isOk());
    }

    @Test
    void startRequest_success() throws Exception {

        UUID id = UUID.randomUUID();

        when(serviceRequestService.startRequest(any(), any()))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        post("/api/service-requests/{id}/start", id)
                                .contentType("application/json")
                                .content("""
                                        {
                                          "changeReason":"Started"
                                        }
                                        """))
                .andExpect(status().isOk());
    }

    @Test
    void completeRequest_success() throws Exception {

        UUID id = UUID.randomUUID();

        when(serviceRequestService.completeRequest(any(), any()))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        post("/api/service-requests/{id}/complete", id)
                                .contentType("application/json")
                                .content("""
                                        {
                                          "changeReason":"Completed"
                                        }
                                        """))
                .andExpect(status().isOk());
    }

    @Test
    void cancelRequest_success() throws Exception {

        UUID id = UUID.randomUUID();

        when(serviceRequestService.cancelRequest(any(), any()))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        post("/api/service-requests/{id}/cancel", id)
                                .contentType("application/json")
                                .content("""
                                        {
                                          "changeReason":"Cancelled"
                                        }
                                        """))
                .andExpect(status().isOk());
    }

    @Test
    void assignAgent_success() throws Exception {

        UUID serviceRequestId = UUID.randomUUID();

        when(serviceRequestService.assignAgent(any(), any()))
                .thenReturn(buildResponse());

        mockMvc.perform(
                        post("/api/service-requests/{id}/assign-agent",
                                serviceRequestId)
                                .contentType("application/json")
                                .content("""
                                        {
                                          "agentId":"%s"
                                        }
                                        """.formatted(UUID.randomUUID())))
                .andExpect(status().isOk());
    }

    private ServiceRequestResponse buildResponse() {

        ServiceRequestResponse response =
                new ServiceRequestResponse();

        response.setServiceRequestId(
                UUID.randomUUID());

        response.setStatus("OPEN");

        return response;
    }
}