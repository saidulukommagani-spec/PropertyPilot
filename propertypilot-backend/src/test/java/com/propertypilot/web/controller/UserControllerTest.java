package com.propertypilot.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.propertypilot.application.dto.UserCreateRequest;
import com.propertypilot.application.dto.UserResponse;
import com.propertypilot.application.dto.UserUpdateRequest;
import com.propertypilot.application.service.UserService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Create User - Success")
    void createUser_success() throws Exception {

        UserResponse response = buildUserResponse();

        when(userService.createUser(any(UserCreateRequest.class)))
                .thenReturn(response);

        UserCreateRequest request =
                new UserCreateRequest(
                        "John Doe",
                        "john@example.com",
                        "9876543210",
                        "Password123"
                );

        mockMvc.perform(
                        post("/api/v1/users")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName")
                        .value("John Doe"))
                .andExpect(jsonPath("$.email")
                        .value("john@example.com"))
                .andExpect(jsonPath("$.roles[0]")
                        .value("CUSTOMER"));
    }

    @Test
    @DisplayName("Create User - Validation Failure")
    void createUser_validationFailure() throws Exception {

        String request = """
                {
                  "fullName":"",
                  "email":"invalid-email",
                  "mobileNumber":"",
                  "password":"123"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/users")
                                .contentType("application/json")
                                .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"));
    }

    @Test
    @DisplayName("Get All Users")
    void getAllUsers_success() throws Exception {

        when(userService.getAllUsers())
                .thenReturn(List.of(buildUserResponse()));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName")
                        .value("John Doe"))
                .andExpect(jsonPath("$[0].roles[0]")
                        .value("CUSTOMER"));
    }

    @Test
    @DisplayName("Get User By Id")
    void getUserById_success() throws Exception {

        UUID userId = UUID.randomUUID();

        when(userService.getUserById(userId))
                .thenReturn(buildUserResponse());

        mockMvc.perform(
                        get("/api/v1/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName")
                        .value("John Doe"))
                .andExpect(jsonPath("$.roles[0]")
                        .value("CUSTOMER"));
    }

    @Test
    @DisplayName("Update User - Success")
    void updateUser_success() throws Exception {

        UUID userId = UUID.randomUUID();

        UserUpdateRequest request =
                new UserUpdateRequest(
                        "John Updated",
                        "updated@example.com",
                        "9999999999"
                );

        UserResponse response =
                new UserResponse(
                        userId,
                        "John Updated",
                        "updated@example.com",
                        "9999999999",
                        "ACTIVE",
                        List.of("CUSTOMER")
                );

        when(userService.updateUser(
                eq(userId),
                any(UserUpdateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        put("/api/v1/users/{userId}", userId)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName")
                        .value("John Updated"))
                .andExpect(jsonPath("$.roles[0]")
                        .value("CUSTOMER"));
    }

    @Test
    @DisplayName("Update User - Validation Failure")
    void updateUser_validationFailure() throws Exception {

        UUID userId = UUID.randomUUID();

        String request = """
                {
                  "fullName":"",
                  "email":"invalid",
                  "mobileNumber":""
                }
                """;

        mockMvc.perform(
                        put("/api/v1/users/{userId}", userId)
                                .contentType("application/json")
                                .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"));
    }

    @Test
    @DisplayName("Delete User")
    void deleteUser_success() throws Exception {

        UUID userId = UUID.randomUUID();

        doNothing()
                .when(userService)
                .deleteUser(userId);

        mockMvc.perform(
                        delete("/api/v1/users/{userId}", userId))
                .andExpect(status().isNoContent());
    }

    private UserResponse buildUserResponse() {

        return new UserResponse(
                UUID.randomUUID(),
                "John Doe",
                "john@example.com",
                "9876543210",
                "ACTIVE",
                List.of("CUSTOMER")
        );
    }
}