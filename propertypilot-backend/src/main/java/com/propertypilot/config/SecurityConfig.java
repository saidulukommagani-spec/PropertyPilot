package com.propertypilot.config;

import com.propertypilot.security.CustomUserDetailsService;
import com.propertypilot.security.JwtAuthenticationFilter;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${propertypilot.cors.allowed-origins:http://localhost:5173}")
    private String allowedOrigins;

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
        .cors(cors -> {})
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

                        // The provider webhook authenticates using its signed raw payload.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/payments/settlement"
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
                                HttpMethod.GET,
                                "/api/v1/properties/archived"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/v1/properties/*/restore"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/properties"
                        ).hasRole("CUSTOMER")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/properties",
                                "/api/v1/properties/*"
                        ).hasAnyRole("ADMIN", "CUSTOMER")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/properties/*"
                        ).hasAnyRole("ADMIN", "CUSTOMER")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/v1/properties/*/archive"
                        ).hasAnyRole("ADMIN", "CUSTOMER")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/v1/properties/**"
                        ).hasAnyRole("ADMIN", "CUSTOMER")

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
                                "OPERATIONS",
                                "CUSTOMER",
                                "AGENT"
                        )

                        /*
                         * VISITS
                         */

                        .requestMatchers(
                                "/api/v1/visits/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "OPERATIONS",
                                "AGENT",
                                "CUSTOMER"
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
                        ).hasAnyRole(
                                "ADMIN",
                                "OPERATIONS",
                                "AGENT",
                                "CUSTOMER"
                        )
                        .requestMatchers(
                                "/api/service-requests/**",
                                "/api/evidence/**",
                                "/api/gps-verifications/**",
                                "/api/reports/**",
                                "/api/tasks/**",
                                "/api/v1/property-owners/**",
                                "/api/v1/deals/**",
                                "/api/customers/**",
                                "/api/customer-dashboard/**",
                                "/api/customer-subscriptions/**",
                                "/api/subscription-consumptions/**",
                                "/api/subscription-notifications/**",
                                "/api/subscription-renewals/**",
                                "/api/subscription-plans/**",
                                "/api/subscription-plan-versions/**",
                                "/api/subscription-benefits/**",
                                "/api/subscription-plan-entitlements/**",
                                "/api/pricing-rules/**",
                                "/api/payments/**"
                        ).authenticated()
                        .requestMatchers(
        "/api/v1/pricing/**"
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
                        // Operations dashboards declare both roles at the controller
                        // level; allow Operations through the URL-level guard too.
                        .requestMatchers(
                                "/api/admin/operations/**",
                                "/api/admin/agents/**"
                        ).hasAnyRole("ADMIN", "OPERATIONS")
                        .requestMatchers(
        "/api/admin/**"
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
    @Bean
public CorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration configuration =
            new CorsConfiguration();

    configuration.setAllowedOrigins(List.of(allowedOrigins.split(","))
            .stream()
            .map(String::trim)
            .filter(origin -> !origin.isEmpty())
            .toList());

    configuration.setAllowedMethods(
            List.of(
                    "GET",
                    "POST",
                    "PUT",
                    "PATCH",
                    "DELETE",
                    "OPTIONS"
            )
    );

    configuration.setAllowedHeaders(
            List.of("*")
    );

    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration(
            "/**",
            configuration
    );

    return source;
}
}
