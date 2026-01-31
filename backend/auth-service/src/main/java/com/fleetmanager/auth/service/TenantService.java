package com.fleetmanager.auth.service;

import com.fleetmanager.auth.dto.request.TenantRegistrationDTO;
import com.fleetmanager.auth.dto.response.TenantRegistrationResponse;
import com.fleetmanager.auth.entity.Tenant;
import com.fleetmanager.auth.exception.SubdomainAlreadyExistsException;
import com.fleetmanager.auth.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;
    
    // NOTE: Dependencies for User, PasswordEncoder, and JwtService will be injected in the next task.

    @Transactional
    public TenantRegistrationResponse registerTenant(TenantRegistrationDTO request) {
        // 1. Check subdomain uniqueness
        if (tenantRepository.existsBySubdomain(request.getSubdomain())) {
            throw new SubdomainAlreadyExistsException("Subdomain '" + request.getSubdomain() + "' is already taken.");
        }

        // 2. Create tenant
        Tenant tenant = new Tenant();
        tenant.setName(request.getOrganizationName());
        tenant.setSubdomain(request.getSubdomain());
        tenant.setActive(true);
        
        tenant = tenantRepository.save(tenant);

        // 3. Create admin user (Pending Task)
        // TODO: Implement logic to create the Admin User entity here.
        // This will involve:
        // - Creating a User object
        // - Encoding the password
        // - Setting the Role to ROLE_ADMIN
        // - Associating the User with the saved Tenant

        // 4. Return tenant details
        // Note: adminUserId and token will be null until the User logic is implementedd
        return TenantRegistrationResponse.builder()
                .tenantId(tenant.getId())
                .adminUserId(null) // Placeholder until User is created
                .token(null)       // Placeholder until JWT is generated
                .build();
    }
}