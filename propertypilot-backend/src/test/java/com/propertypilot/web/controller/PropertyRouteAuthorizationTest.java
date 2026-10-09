package com.propertypilot.web.controller;

import com.propertypilot.application.service.PropertyService;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PropertyController.class)
@AutoConfigureMockMvc
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
class PropertyRouteAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PropertyService propertyService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser(roles = "AGENT")
    void agentCannotUseCustomerPropertyDirectory() throws Exception {
        mockMvc.perform(get("/api/v1/properties"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(propertyService);
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void operationsCannotUseCustomerPropertyDirectory() throws Exception {
        mockMvc.perform(get("/api/v1/properties"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(propertyService);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerCanUseCustomerPropertyDirectory() throws Exception {
        mockMvc.perform(get("/api/v1/properties"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void operationsCannotListArchivedProperties() throws Exception {
        mockMvc.perform(get("/api/v1/properties/archived"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(propertyService);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerCannotListArchivedProperties() throws Exception {
        mockMvc.perform(get("/api/v1/properties/archived"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(propertyService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void administratorCanListArchivedProperties() throws Exception {
        mockMvc.perform(get("/api/v1/properties/archived"))
                .andExpect(status().isOk());
    }
}
