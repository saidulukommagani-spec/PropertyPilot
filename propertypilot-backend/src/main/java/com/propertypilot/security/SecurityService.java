package com.propertypilot.security;

import com.propertypilot.infrastructure.persistence.entity.CustomerEntity;
import com.propertypilot.infrastructure.persistence.entity.UserEntity;
import com.propertypilot.infrastructure.persistence.entity.UserRoleEntity;
import com.propertypilot.infrastructure.persistence.repository.CustomerRepository;
import com.propertypilot.infrastructure.persistence.repository.UserRepository;
import com.propertypilot.infrastructure.persistence.repository.UserRoleRepository;
import com.propertypilot.web.exception.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SecurityService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final UserRoleRepository userRoleRepository;

    public SecurityService(
            UserRepository userRepository,
            CustomerRepository customerRepository,
            UserRoleRepository userRoleRepository) {

        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.userRoleRepository = userRoleRepository;
    }

    public String getCurrentUsername() {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        if (authentication == null ||
                authentication.getName() == null) {

            throw new ResourceNotFoundException(
                    "No authenticated user found");
        }

        return authentication.getName();
    }

    public UserEntity getCurrentUser() {

        return userRepository.findByEmail(
                        getCurrentUsername())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Logged-in user not found"));
    }

    public UUID getCurrentUserId() {

        return getCurrentUser()
                .getUserId();
    }

    public CustomerEntity getCurrentCustomer() {
            String username = getCurrentUsername();

    System.out.println(
            "Looking for customer with email = "
                    + username);

        return customerRepository.findByUser_Email(
                        getCurrentUsername())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found"));
    }

    public boolean isAdmin() {

        return userRoleRepository
                .findByUser_UserId(
                        getCurrentUserId())
                .stream()
                .map(UserRoleEntity::getRole)
                .anyMatch(role ->
                        "ADMIN".equalsIgnoreCase(
                                role.getRoleCode()));
    }
}