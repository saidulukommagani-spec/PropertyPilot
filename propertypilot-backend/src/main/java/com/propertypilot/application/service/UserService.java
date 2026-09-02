package com.propertypilot.application.service;

import com.propertypilot.application.dto.UserCreateRequest;
import com.propertypilot.application.dto.UserResponse;


public interface UserService {

    UserResponse createUser(UserCreateRequest request);
}