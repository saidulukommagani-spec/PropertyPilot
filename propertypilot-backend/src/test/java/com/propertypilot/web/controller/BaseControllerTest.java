package com.propertypilot.web.controller;

import com.propertypilot.security.CustomUserDetailsService;
import com.propertypilot.security.JwtAuthenticationFilter;
import com.propertypilot.security.JwtService;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

public abstract class BaseControllerTest {

    @MockitoBean
    protected JwtService jwtService;

    @MockitoBean
    protected CustomUserDetailsService
            customUserDetailsService;

    @MockitoBean
    protected JwtAuthenticationFilter
            jwtAuthenticationFilter;
}