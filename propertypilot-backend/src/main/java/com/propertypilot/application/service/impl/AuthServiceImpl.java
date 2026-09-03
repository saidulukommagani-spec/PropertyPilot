package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.auth.LoginRequest;
import com.propertypilot.application.dto.auth.LoginResponse;
import com.propertypilot.application.service.AuthService;
import com.propertypilot.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
        )
);

String token = jwtService.generateToken(
        request.email(),
        new HashMap<>()
);

return new LoginResponse(
        token,
        "Bearer",
        900000L
);
    }
}