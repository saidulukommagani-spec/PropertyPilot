package com.propertypilot.application.controller;

import com.propertypilot.application.dto.UserCreateRequest;
import com.propertypilot.application.dto.UserResponse;
import com.propertypilot.application.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
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
}