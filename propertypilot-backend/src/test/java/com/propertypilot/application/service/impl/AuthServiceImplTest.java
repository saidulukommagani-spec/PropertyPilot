package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.auth.LoginRequest;
import com.propertypilot.application.dto.auth.LoginResponse;
import com.propertypilot.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceImplTest {

    @Test
    void loginReturnsTokenExpirationInSeconds() {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        JwtService jwtService = mock(JwtService.class);
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin@propertypilot.com");
        when(jwtService.generateToken(eq("admin@propertypilot.com"), eq(Map.of())))
                .thenReturn("signed-token");
        when(jwtService.getExpiration()).thenReturn(1_800_000L);

        LoginResponse response = new AuthServiceImpl(authenticationManager, jwtService)
                .login(new LoginRequest("admin@propertypilot.com", "secret"));

        assertThat(response.expiresIn()).isEqualTo(1_800);
        assertThat(response.accessToken()).isEqualTo("signed-token");
        verify(jwtService).getExpiration();
    }
}
