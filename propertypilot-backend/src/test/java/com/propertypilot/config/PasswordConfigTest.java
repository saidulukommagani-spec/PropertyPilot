package com.propertypilot.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class PasswordConfigTest {

    @Test
    void passwordEncoder_ShouldReturnBCrypt() {

        PasswordConfig config =
                new PasswordConfig();

        PasswordEncoder encoder =
                config.passwordEncoder();

        assertNotNull(encoder);

        assertTrue(
                encoder instanceof BCryptPasswordEncoder);
    }
}