package com.propertypilot.web.controller;

import com.propertypilot.application.dto.ServiceRequestResponse;
import com.propertypilot.application.service.ServiceRequestService;
import com.propertypilot.config.SecurityConfig;
import com.propertypilot.security.CustomUserDetailsService;
import com.propertypilot.security.JwtAuthenticationFilter;
import com.propertypilot.security.JwtService;
import com.propertypilot.web.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ServiceRequestController.class)
@AutoConfigureMockMvc
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
class ServiceRequestCancellationRouteAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServiceRequestService serviceRequestService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser(roles = "AGENT")
    void agentCannotReachCancellationRoute() throws Exception {
        mockMvc.perform(post("/api/service-requests/{id}/cancel", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"changeReason\":\"No longer required\"}"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(serviceRequestService);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerCanReachOwnershipCheckedCancellationService() throws Exception {
        allowCancellationThroughService();

        mockMvc.perform(post("/api/service-requests/{id}/cancel", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"changeReason\":\"No longer required\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void operationsCanReachCancellationService() throws Exception {
        allowCancellationThroughService();

        mockMvc.perform(post("/api/service-requests/{id}/cancel", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"changeReason\":\"No longer required\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanReachCancellationService() throws Exception {
        allowCancellationThroughService();

        mockMvc.perform(post("/api/service-requests/{id}/cancel", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"changeReason\":\"No longer required\"}"))
                .andExpect(status().isOk());
    }

    private void allowCancellationThroughService() {
        when(serviceRequestService.cancelRequest(org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any())).thenReturn(new ServiceRequestResponse());
    }
}
