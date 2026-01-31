package com.fleetmanager.auth.service;

import com.fleetmanager.auth.dto.request.TenantRegistrationDTO;
import com.fleetmanager.auth.dto.response.TenantRegistrationResponse;
import com.fleetmanager.auth.entity.Tenant;
import com.fleetmanager.auth.entity.User;
import com.fleetmanager.auth.enums.Role;
import com.fleetmanager.auth.enums.UserStatus;
import com.fleetmanager.auth.exception.SubdomainAlreadyExistsException;
import com.fleetmanager.auth.repository.TenantRepository;
import com.fleetmanager.auth.repository.UserRepository;
import com.fleetmanager.auth.util.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil; // ⭐ ADD THIS

    @Transactional
    public TenantRegistrationResponse registerTenant(TenantRegistrationDTO request) {

        // 1️⃣ Check subdomain uniqueness
        if (tenantRepository.existsBySubdomain(request.getSubdomain())) {
            throw new SubdomainAlreadyExistsException(
                    "Subdomain '" + request.getSubdomain() + "' is already taken.");
        }

        // 2️⃣ Create tenant
        Tenant tenant = new Tenant();
        tenant.setName(request.getOrganizationName());
        tenant.setSubdomain(request.getSubdomain());
        tenant.setActive(true);

        tenant = tenantRepository.save(tenant);

        // 3️⃣ Create Admin User
        User adminUser = new User();
        adminUser.setEmail(request.getAdminEmail());
        adminUser.setName(request.getAdminName());
        adminUser.setTenantId(tenant.getId());
        adminUser.setRole(Role.ADMIN);
        adminUser.setStatus(UserStatus.ACTIVE);
        adminUser.setPasswordHash(passwordEncoder.encode(request.getAdminPassword()));

        adminUser = userRepository.save(adminUser);

        // ⭐ 4️⃣ Generate JWT Token
        String token = jwtUtil.generateToken(adminUser);

        // 5️⃣ Return response
        return TenantRegistrationResponse.builder()
                .tenantId(tenant.getId())
                .adminUserId(adminUser.getId())
                .token(token) // ⭐ NOW TOKEN IS RETURNED
                .build();
    }
}
