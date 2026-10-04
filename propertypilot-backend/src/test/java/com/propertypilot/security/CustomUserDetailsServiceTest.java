package com.propertypilot.security;

import com.propertypilot.infrastructure.persistence.entity.*;
import com.propertypilot.infrastructure.persistence.repository.UserRepository;
import com.propertypilot.infrastructure.persistence.repository.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private CustomUserDetailsService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUser_ShouldReturnUserDetails() {

        UserEntity user = new UserEntity();
        user.setUserId(UUID.randomUUID());
        user.setEmail("admin@test.com");
        user.setPasswordHash("password");

        RoleEntity role = new RoleEntity();
        role.setRoleCode("ADMIN");

        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setRole(role);

        when(userRepository.findByEmail("admin@test.com"))
                .thenReturn(Optional.of(user));

        when(userRoleRepository.findByUser_UserId(
                user.getUserId()))
                .thenReturn(List.of(userRole));

        UserDetails result =
                service.loadUserByUsername(
                        "admin@test.com");

        assertEquals(
                "admin@test.com",
                result.getUsername());

        assertEquals(
                1,
                result.getAuthorities().size());
    }

    @Test
    void loadUser_ShouldThrowException() {

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername(
                        "missing@test.com"));
    }
}