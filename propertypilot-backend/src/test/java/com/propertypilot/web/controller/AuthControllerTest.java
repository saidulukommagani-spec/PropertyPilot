package com.propertypilot.web.controller;


import com.propertypilot.application.dto.auth.LoginRequest;
import com.propertypilot.application.dto.auth.LoginResponse;
import com.propertypilot.application.service.AuthService;
import com.propertypilot.security.CustomUserDetailsService;
import com.propertypilot.security.JwtService;
import com.propertypilot.web.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;  

    @MockitoBean
private AuthService authService;

@MockitoBean
private JwtService jwtService;

@MockitoBean
private CustomUserDetailsService customUserDetailsService;


    @Test
    @DisplayName("Should login successfully")
    void login_success() throws Exception {

        LoginResponse response =
                new LoginResponse(
                        "jwt-token",
                        "Bearer",
                        3600
                );

        when(authService.login(
                ArgumentMatchers.any(LoginRequest.class)))
                .thenReturn(response);

        String request =
                """
                {
                    "email":"admin@propertypilot.com",
                    "password":"Password123"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken")
                        .value("jwt-token"))
                .andExpect(jsonPath("$.tokenType")
                        .value("Bearer"))
                .andExpect(jsonPath("$.expiresIn")
                        .value(3600));
    }

    @Test
    @DisplayName("Should return validation error when email is blank")
    void login_emailRequired() throws Exception {

        String request =
                """
                {
                    "email":"",
                    "password":"Password123"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"))
                .andExpect(jsonPath("$.errors.email")
                        .value("Email is required"));
    }

    @Test
    @DisplayName("Should return validation error when email format is invalid")
    void login_invalidEmail() throws Exception {

        String request =
                """
                {
                    "email":"invalid-email",
                    "password":"Password123"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"))
                .andExpect(jsonPath("$.errors.email")
                        .value("Email must be valid"));
    }

    @Test
    @DisplayName("Should return internal server error")
    void login_internalServerError() throws Exception {

        when(authService.login(
                ArgumentMatchers.any(LoginRequest.class)))
                .thenThrow(
                        new RuntimeException("Unexpected Error")
                );

        String request =
                """
                {
                    "email":"admin@propertypilot.com",
                    "password":"Password123"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message")
                        .value("An unexpected error occurred"));
    }
}
