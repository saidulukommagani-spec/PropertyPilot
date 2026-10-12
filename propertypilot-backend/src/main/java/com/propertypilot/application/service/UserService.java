package com.propertypilot.application.service;

import com.propertypilot.application.dto.UserCreateRequest;
import com.propertypilot.application.dto.UserResponse;
import com.propertypilot.application.dto.UserUpdateRequest;
import com.propertypilot.application.dto.UserAccessProvisionRequest;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse createUser(UserCreateRequest request);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(UUID userId);

    UserResponse updateUser(
            UUID userId,
            UserUpdateRequest request);

    void deleteUser(UUID userId);
     UserResponse getCurrentUser(String email);

    UserResponse provisionAccess(UUID userId, UserAccessProvisionRequest request);
}
