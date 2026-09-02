package com.propertypilot.security;

import com.propertypilot.infrastructure.persistence.entity.UserEntity;
import com.propertypilot.infrastructure.persistence.entity.UserRoleEntity;
import com.propertypilot.infrastructure.persistence.repository.UserRepository;
import com.propertypilot.infrastructure.persistence.repository.UserRoleRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public CustomUserDetailsService(
            UserRepository userRepository,
            UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found: " + username));

        List<SimpleGrantedAuthority> authorities =
                userRoleRepository.findByUser_UserId(user.getUserId())
                        .stream()
                        .map(UserRoleEntity::getRole)
                        .map(role ->
                                new SimpleGrantedAuthority(
                                        "ROLE_" + role.getRoleCode()))
                        .toList();

        return User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .build();
    }
}