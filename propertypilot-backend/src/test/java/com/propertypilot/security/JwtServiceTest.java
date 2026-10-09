package com.propertypilot.security;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {

        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "secret",
                "propertypilotpropertypilotpropertypilot123456");

        ReflectionTestUtils.setField(
                jwtService,
                "expiration",
                3600000L);
    }

    @Test
    void generateToken_ShouldGenerateToken() {

        String token =
                jwtService.generateToken(
                        "admin@test.com",
                        new HashMap<>());

        assertNotNull(token);
    }

    @Test
    void extractUsername_ShouldReturnUsername() {

        String token =
                jwtService.generateToken(
                        "admin@test.com",
                        new HashMap<>());

        String username =
                jwtService.extractUsername(token);

        assertEquals(
                "admin@test.com",
                username);
    }

    @Test
    void isTokenValid_ShouldReturnTrue() {

        String token =
                jwtService.generateToken(
                        "admin@test.com",
                        new HashMap<>());

        assertTrue(
                jwtService.isTokenValid(
                        token,
                        "admin@test.com"));
    }

    @Test
    void isTokenValid_ShouldReturnFalse() {

        String token =
                jwtService.generateToken(
                        "admin@test.com",
                        new HashMap<>());

        assertFalse(
                jwtService.isTokenValid(
                        token,
                        "user@test.com"));
    }

    @Test
    void getExpiration_ShouldReturnExpiration() {

        assertEquals(
                3600000L,
                jwtService.getExpiration());
    }

    @Test
    void validateConfiguration_ShouldRejectShortSecret() {

        ReflectionTestUtils.setField(
                jwtService,
                "secret",
                "too-short");

        assertThrows(
                IllegalStateException.class,
                () -> ReflectionTestUtils.invokeMethod(
                        jwtService,
                        "validateConfiguration"));
    }

    @Test
    void validateConfiguration_ShouldRejectMissingSecret() {

        ReflectionTestUtils.setField(
                jwtService,
                "secret",
                " ");

        assertThrows(
                IllegalStateException.class,
                () -> ReflectionTestUtils.invokeMethod(
                        jwtService,
                        "validateConfiguration"));
    }

    @Test
    void validateConfiguration_ShouldRejectNonPositiveExpiration() {

        ReflectionTestUtils.setField(
                jwtService,
                "expiration",
                0L);

        assertThrows(
                IllegalStateException.class,
                () -> ReflectionTestUtils.invokeMethod(
                        jwtService,
                        "validateConfiguration"));
    }
}
