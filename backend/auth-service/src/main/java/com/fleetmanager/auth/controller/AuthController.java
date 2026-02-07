package com.fleetmanager.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fleetmanager.auth.context.TenantContext;
import com.fleetmanager.auth.dto.request.LoginRequest;
import com.fleetmanager.auth.dto.response.LoginResponse;
import com.fleetmanager.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login and authentication APIs")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/test")
    public String testRoute() {
        return "Auth Service is reachable!";
    }

    @Operation(
        summary = "User Login",
        description = "Login using email, password, and tenant subdomain to receive a JWT token"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class),
                examples = @ExampleObject(value = """
                {
                  "token": "eyJhbGciOiJIUzI1NiIs...",
                  "userId": 1,
                  "email": "admin@company.com",
                  "role": "ADMIN",
                  "tenantId": 101
                }
                """)
            )
        ),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }
    
    @GetMapping("/secured-test")
    public String securedTest() {
        return "TENANT=" + TenantContext.getCurrentTenantIdOrThrow();
    }
}
