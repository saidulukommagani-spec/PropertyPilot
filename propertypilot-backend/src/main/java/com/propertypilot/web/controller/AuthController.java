package com.propertypilot.web.controller;

import com.propertypilot.application.dto.auth.LoginRequest;
import com.propertypilot.application.dto.auth.LoginResponse;
import com.propertypilot.application.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request) {

        return authService.login(request);
    }
}