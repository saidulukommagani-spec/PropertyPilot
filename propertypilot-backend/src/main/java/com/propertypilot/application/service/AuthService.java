package com.propertypilot.application.service;

import com.propertypilot.application.dto.auth.LoginRequest;
import com.propertypilot.application.dto.auth.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}