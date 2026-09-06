package com.propertypilot.application.service.impl;

import com.propertypilot.application.dto.UserCreateRequest;
import com.propertypilot.application.dto.UserResponse;
import com.propertypilot.application.dto.UserUpdateRequest;
import com.propertypilot.application.service.UserService;
import com.propertypilot.domain.enums.UserStatus;
import com.propertypilot.infrastructure.persistence.entity.RoleEntity;
import com.propertypilot.infrastructure.persistence.entity.UserEntity;
import com.propertypilot.infrastructure.persistence.entity.UserRoleEntity;
import com.propertypilot.infrastructure.persistence.repository.RoleRepository;
import com.propertypilot.infrastructure.persistence.repository.UserRepository;
import com.propertypilot.infrastructure.persistence.repository.UserRoleRepository;
import com.propertypilot.web.exception.DuplicateResourceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {

        System.out.println("CREATE USER START");
        System.out.println("EMAIL = " + request.email());

        String normalizedEmail =
                request.email().trim().toLowerCase();

        userRepository.findByEmail(normalizedEmail)
                .ifPresent(user -> {
                    throw new DuplicateResourceException(
                            "Email already exists");
                });

        userRepository.findByMobileNumber(
                        request.mobileNumber())
                .ifPresent(user -> {
                    throw new DuplicateResourceException(
                            "Mobile number already exists");
                });

        UserEntity userEntity = new UserEntity();

        userEntity.setFullName(request.fullName());
        userEntity.setEmail(normalizedEmail);
        userEntity.setMobileNumber(
                request.mobileNumber());

        userEntity.setPasswordHash(
                passwordEncoder.encode(
                        request.password()));

        userEntity.setStatus(UserStatus.ACTIVE);

        UserEntity savedUser =
                userRepository.save(userEntity);

        RoleEntity customerRole =
                roleRepository.findByRoleCode("CUSTOMER")
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "CUSTOMER role not found"));

        UserRoleEntity userRole =
                new UserRoleEntity();

        userRole.setUser(savedUser);
        userRole.setRole(customerRole);

        // Required by DB constraint
        userRole.setEffectiveFrom(
                OffsetDateTime.now());

        userRoleRepository.save(userRole);

        System.out.println(
                "USER SAVED = "
                        + savedUser.getUserId());

        return new UserResponse(
                savedUser.getUserId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getMobileNumber(),
                savedUser.getStatus().name()
        );
    }

    @Override
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getUserId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getMobileNumber(),
                        user.getStatus().name()
                ))
                .toList();
    }

    @Override
    public UserResponse getUserById(UUID userId) {

        UserEntity user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found: "
                                                + userId));

        return new UserResponse(
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getMobileNumber(),
                user.getStatus().name()
        );
    }

    @Override
    public UserResponse updateUser(
            UUID userId,
            UserUpdateRequest request) {

        UserEntity user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found: "
                                                + userId));

        user.setFullName(request.fullName());

        String normalizedEmail =
                request.email().trim().toLowerCase();

        user.setEmail(normalizedEmail);
        user.setMobileNumber(
                request.mobileNumber());

        UserEntity updatedUser =
                userRepository.save(user);

        return new UserResponse(
                updatedUser.getUserId(),
                updatedUser.getFullName(),
                updatedUser.getEmail(),
                updatedUser.getMobileNumber(),
                updatedUser.getStatus().name()
        );
    }

    @Override
    public void deleteUser(UUID userId) {

        UserEntity user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found: "
                                                + userId));

        userRepository.delete(user);
    }

    @Override
    public UserResponse getCurrentUser(
            String email) {

        String normalizedEmail =
                email.trim().toLowerCase();

        UserEntity user =
                userRepository.findByEmail(
                                normalizedEmail)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found: "
                                                + email));

        return new UserResponse(
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getMobileNumber(),
                user.getStatus().name()
        );
    }
}