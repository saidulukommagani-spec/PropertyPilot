package com.propertypilot.config;

import com.propertypilot.PropertyPilotApplication;
import com.propertypilot.security.CustomUserDetailsService;
import com.propertypilot.security.JwtAuthenticationFilter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        securityConfig =
                new SecurityConfig(
                        customUserDetailsService,
                        passwordEncoder,
                        jwtAuthenticationFilter);
    }

    @Test
    void authenticationProvider_ShouldCreateProvider() {

        DaoAuthenticationProvider provider =
                securityConfig.authenticationProvider();

        assertNotNull(provider);
        assertInstanceOf(
                DaoAuthenticationProvider.class,
                provider);
    }

    @Test
    void authenticationProvider_ShouldCreateNewInstance() {

        DaoAuthenticationProvider provider1 =
                securityConfig.authenticationProvider();

        DaoAuthenticationProvider provider2 =
                securityConfig.authenticationProvider();

        assertNotNull(provider1);
        assertNotNull(provider2);
        assertNotSame(provider1, provider2);
    }

    @Test
    void authenticationManager_ShouldReturnManager()
            throws Exception {

        AuthenticationManager manager =
                mock(AuthenticationManager.class);

        when(authenticationConfiguration
                .getAuthenticationManager())
                .thenReturn(manager);

        AuthenticationManager result =
                securityConfig.authenticationManager(
                        authenticationConfiguration);

        assertNotNull(result);
        assertSame(manager, result);

        verify(authenticationConfiguration, times(1))
                .getAuthenticationManager();
    }

    @Test
    void authenticationManager_ShouldThrowException()
            throws Exception {

        RuntimeException exception =
                new RuntimeException("Authentication Error");

        when(authenticationConfiguration
                .getAuthenticationManager())
                .thenThrow(exception);

        RuntimeException thrown =
                assertThrows(
                        RuntimeException.class,
                        () -> securityConfig.authenticationManager(
                                authenticationConfiguration));

        assertEquals(
                "Authentication Error",
                thrown.getMessage());
    }

    @Test
    void securityConfig_ShouldBeCreated() {

        assertNotNull(securityConfig);
    }

    @Test
    void application_ShouldNotEnableSpringGeneratedInMemoryUsers() {
        SpringBootApplication application = PropertyPilotApplication.class
                .getAnnotation(SpringBootApplication.class);

        assertNotNull(application);
        assertTrue(java.util.Arrays.asList(application.exclude())
                .contains(UserDetailsServiceAutoConfiguration.class));
    }
}
