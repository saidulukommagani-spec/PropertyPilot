package com.propertypilot.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        filter =
                new JwtAuthenticationFilter(
                        jwtService,
                        userDetailsService);
    }

    @Test
    void shouldSkipAuthEndpoints() throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setServletPath(
                "/api/v1/auth/login");

        filter.doFilterInternal(
                request,
                new MockHttpServletResponse(),
                filterChain);

        verify(filterChain)
                .doFilter(any(), any());
    }

    @Test
    void shouldSkipWithoutBearerToken() throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setServletPath(
                "/api/test");

        filter.doFilterInternal(
                request,
                new MockHttpServletResponse(),
                filterChain);

        verify(filterChain)
                .doFilter(any(), any());
    }

    @Test
    void shouldAuthenticateSuccessfully() throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setServletPath(
                "/api/test");

        request.addHeader(
                "Authorization",
                "Bearer token");

        when(jwtService.extractUsername("token"))
                .thenReturn("admin@test.com");

        User user =
                new User(
                        "admin@test.com",
                        "pwd",
                        Collections.emptyList());

        when(userDetailsService
                .loadUserByUsername(
                        "admin@test.com"))
                .thenReturn(user);

        when(jwtService.isTokenValid(
                "token",
                "admin@test.com"))
                .thenReturn(true);

        filter.doFilterInternal(
                request,
                new MockHttpServletResponse(),
                filterChain);

        verify(filterChain)
                .doFilter(any(), any());
    }
}