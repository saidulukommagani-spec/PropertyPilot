package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.auth.LoginRequest;
import com.propertypilot.application.dto.auth.LoginResponse;
import com.propertypilot.application.service.AuthService;
import com.propertypilot.security.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Map;

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
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        request.email().trim().toLowerCase(),
                        request.password()
                )
        );

        String token = jwtService.generateToken(
                authentication.getName(),
                Map.of()
        );

        return new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpiration()
        );
    }
}
