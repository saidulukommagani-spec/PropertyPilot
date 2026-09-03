package com.propertypilot.web.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/v1/test/me")
    public String currentUser(Authentication authentication) {

        return "Logged in as: " +
                authentication.getName();
    }
}