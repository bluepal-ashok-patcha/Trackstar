package com.fleetmanager.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fleetmanager.auth.dto.request.TenantRegistrationDTO;
import com.fleetmanager.auth.dto.response.TenantRegistrationResponse;
import com.fleetmanager.auth.service.TenantService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @PostMapping("/register-tenant")
    public ResponseEntity<TenantRegistrationResponse> register(
            @Valid @RequestBody TenantRegistrationDTO dto) {

        TenantRegistrationResponse response =
                tenantService.registerTenant(dto);

        return ResponseEntity.ok(response);
    }
}