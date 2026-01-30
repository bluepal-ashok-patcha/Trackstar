package com.fleetmanager.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter 
@Builder
@Schema(description = "Response containing registration details and access token")
public class TenantRegistrationResponse {

    @Schema(example = "1", description = "Unique identifier of the created tenant")
    private Long tenantId;

    @Schema(example = "101", description = "Unique identifier of the created admin user")
    private Long adminUserId;

    @Schema(example = "eyJhbGciOiJIUzI1Ni...", description = "JWT Access Token for immediate authentication")
    private String token;
}