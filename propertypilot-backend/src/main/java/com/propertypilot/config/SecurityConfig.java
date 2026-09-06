package com.propertypilot.config;

import com.propertypilot.security.CustomUserDetailsService;
import com.propertypilot.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            CustomUserDetailsService customUserDetailsService,
            PasswordEncoder passwordEncoder,
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.customUserDetailsService = customUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(
                customUserDetailsService);

        provider.setPasswordEncoder(
                passwordEncoder);

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        /*
                         * PUBLIC ENDPOINTS
                         */

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/actuator/health",
                                "/actuator/info",
                                "/error"
                        ).permitAll()

                        .requestMatchers(
                                "/api/v1/auth/**"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/users"
                        ).permitAll()

                        /*
                         * USER PROFILE
                         */

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/users/me"
                        ).authenticated()

                        /*
                         * PROPERTY MANAGEMENT
                         */

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/properties"
                        ).authenticated()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/properties"
                                ).authenticated()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/properties/**"
                                ).authenticated()

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/properties/**"
                        ).authenticated()

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/properties/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "PROPERTY_MANAGER"
                        )

                        /*
                         * CUSTOMER MANAGEMENT
                         */

                        .requestMatchers(
                                "/api/v1/customers/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "PROPERTY_MANAGER"
                        )

                        /*
                         * LEADS
                         */

                        .requestMatchers(
                                "/api/v1/leads/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "PROPERTY_MANAGER",
                                "SALES_AGENT"
                        )

                        /*
                         * VISITS
                         */

                        .requestMatchers(
                                "/api/v1/visits/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "PROPERTY_MANAGER",
                                "SALES_AGENT"
                        )

                        /*
                         * BOOKINGS
                         */

                        .requestMatchers(
                                "/api/v1/bookings/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "PROPERTY_MANAGER"
                        )

                        /*
                         * PROPERTY SERVICES
                         */

                        .requestMatchers(
                                "/api/v1/service-requests/**"
                        ).authenticated()

                        .requestMatchers(
                                "/api/v1/property-documents/**"
                        ).authenticated()

                        .requestMatchers(
                                "/api/v1/property-owners/**"
                        ).authenticated()

                        .requestMatchers(
                                "/api/v1/marketplace-listings/**"
                        ).authenticated()

                        /*
                         * ADMIN
                         */

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/users"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/users/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/users/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/users/**"
                        ).hasRole("ADMIN")

                        /*
                         * EVERYTHING ELSE
                         */

                        .anyRequest().authenticated()
                )

                .authenticationProvider(
                        authenticationProvider())

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}