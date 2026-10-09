package com.propertypilot.web.controller;

import com.propertypilot.application.service.SubscriptionRenewalService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SubscriptionRenewalController.class)
@AutoConfigureMockMvc
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
class SubscriptionRenewalRouteAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SubscriptionRenewalService renewalService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser(roles = "AGENT")
    void agentCannotReachSubscriptionRenewalRoutes() throws Exception {
        mockMvc.perform(get("/api/subscription-renewals/subscription/{id}", UUID.randomUUID()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(renewalService);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerCanReachOwnedRenewalReadRoute() throws Exception {
        UUID subscriptionId = UUID.randomUUID();
        when(renewalService.getRenewalsBySubscription(subscriptionId)).thenReturn(List.of());

        mockMvc.perform(get("/api/subscription-renewals/subscription/{id}", subscriptionId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void operationsCanReachRenewalReadRoute() throws Exception {
        UUID subscriptionId = UUID.randomUUID();
        when(renewalService.getRenewalsBySubscription(subscriptionId)).thenReturn(List.of());

        mockMvc.perform(get("/api/subscription-renewals/subscription/{id}", subscriptionId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanReachRenewalReadRoute() throws Exception {
        UUID subscriptionId = UUID.randomUUID();
        when(renewalService.getRenewalsBySubscription(subscriptionId)).thenReturn(List.of());

        mockMvc.perform(get("/api/subscription-renewals/subscription/{id}", subscriptionId))
                .andExpect(status().isOk());
    }
}
