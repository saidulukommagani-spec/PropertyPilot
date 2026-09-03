package com.propertypilot.application.service.impl;
import com.propertypilot.application.dto.UserUpdateRequest;
import com.propertypilot.application.dto.UserCreateRequest;
import com.propertypilot.application.dto.UserResponse;
import com.propertypilot.application.service.UserService;
import com.propertypilot.domain.enums.UserStatus;
import com.propertypilot.infrastructure.persistence.entity.UserEntity;
import com.propertypilot.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.propertypilot.web.exception.DuplicateResourceException;

import java.util.UUID;
import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserJpaRepository userJpaRepository,
            PasswordEncoder passwordEncoder) {

        this.userJpaRepository = userJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public UserResponse createUser(UserCreateRequest request) {

        userJpaRepository.findByEmail(request.email())
                .ifPresent(user -> {
                  throw new DuplicateResourceException(
        "Email already exists");
                });

        UserEntity userEntity = new UserEntity();

        UUID userId = UUID.randomUUID();

        userEntity.setUserId(userId);
        userEntity.setFullName(request.fullName());
        userEntity.setEmail(request.email());
        userEntity.setMobileNumber(request.mobileNumber());

        userEntity.setPasswordHash(
        passwordEncoder.encode(request.password())
);

        userEntity.setStatus(UserStatus.ACTIVE);

        UserEntity savedUser = userJpaRepository.save(userEntity);

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

    return userJpaRepository.findAll()
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

    UserEntity user = userJpaRepository.findById(userId)
            .orElseThrow(() ->
                    new RuntimeException(
                            "User not found: " + userId));

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

    UserEntity user = userJpaRepository.findById(userId)
            .orElseThrow(() ->
                    new RuntimeException(
                            "User not found: " + userId));

    user.setFullName(request.fullName());
    user.setEmail(request.email());
    user.setMobileNumber(request.mobileNumber());

    UserEntity updatedUser =
            userJpaRepository.save(user);

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

    UserEntity user = userJpaRepository.findById(userId)
            .orElseThrow(() ->
                    new RuntimeException(
                            "User not found: " + userId));

    userJpaRepository.delete(user);
}
}