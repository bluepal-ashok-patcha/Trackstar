package com.fleetmanager.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Task 3: Test Endpoint
    @GetMapping("/test")
    public String testRoute() {
        return "Auth Service is reachable!";
    }

    // ... existing code ...
}
