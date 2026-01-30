package com.fleetmanager.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fleetmanager.auth.dto.request.TenantRegistrationDTO;
import com.fleetmanager.auth.dto.response.TenantRegistrationResponse;
import com.fleetmanager.auth.service.TenantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Operations related to tenant registration and login")
public class TenantController {

    private final TenantService tenantService;

    @Operation(
        summary = "Register a new tenant",
        description = "Handles the creation of a new organization (tenant) and its initial administrator account."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully registered",
            content = @Content(schema = @Schema(implementation = TenantRegistrationResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Validation error or subdomain already exists")
    })
    @PostMapping("/register-tenant")
    public ResponseEntity<TenantRegistrationResponse> register(
            @Valid @RequestBody TenantRegistrationDTO dto) {

        TenantRegistrationResponse response = tenantService.registerTenant(dto);
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }
}