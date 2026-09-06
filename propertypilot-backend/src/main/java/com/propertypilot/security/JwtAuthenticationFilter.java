package com.propertypilot.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("JWT FILTER HIT");

        String path = request.getServletPath();

        if (path.startsWith("/api/v1/auth")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/actuator")) {

            filterChain.doFilter(request, response);
            return;
        }

        String authHeader =
                request.getHeader("Authorization");

        System.out.println(
                "Authorization Header = "
                        + authHeader);

        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            System.out.println(
                    "No Bearer token found");

            filterChain.doFilter(request, response);
            return;
        }

        String jwt =
                authHeader.substring(7);

        System.out.println(
                "JWT Token Received");

        try {

            String username =
                    jwtService.extractUsername(jwt);

            System.out.println(
                    "Username From Token = "
                            + username);

            if (username != null
                    && SecurityContextHolder
                    .getContext()
                    .getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(
                                        username);

                System.out.println(
                        "User Loaded = "
                                + userDetails.getUsername());

                boolean valid =
                        jwtService.isTokenValid(
                                jwt,
                                userDetails.getUsername());

                System.out.println(
                        "Token Valid = "
                                + valid);

                if (valid) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request));

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authToken);

                    System.out.println(
                            "JWT AUTH SUCCESS");
                }
            }

        } catch (Exception ex) {

            System.out.println(
                    "JWT AUTH ERROR : "
                            + ex.getMessage());

            ex.printStackTrace();
        }

        filterChain.doFilter(
                request,
                response);
    }
}