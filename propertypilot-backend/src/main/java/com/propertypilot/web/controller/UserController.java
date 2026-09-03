package com.propertypilot.web.controller;

import com.propertypilot.application.dto.UserCreateRequest;
import com.propertypilot.application.dto.UserResponse;
import com.propertypilot.application.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.propertypilot.application.dto.UserUpdateRequest;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(
            @Valid @RequestBody UserCreateRequest request) {

        return userService.createUser(request);
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {

        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserResponse getUserById(
            @PathVariable UUID userId) {

        return userService.getUserById(userId);
    }
    @PutMapping("/{userId}")
public UserResponse updateUser(
        @PathVariable UUID userId,
        @Valid @RequestBody UserUpdateRequest request) {

    return userService.updateUser(
            userId,
            request);
}

@DeleteMapping("/{userId}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void deleteUser(
        @PathVariable UUID userId) {

    userService.deleteUser(userId);
}
}