package com.fleetmanager.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Login response containing JWT token and user info")
public class LoginResponse {

    @Schema(example = "eyJhbGciOiJIUzI1NiIs...")
    private String token;

    @Schema(example = "1")
    private Long userId;

    @Schema(example = "admin@company.com")
    private String email;

    @Schema(example = "ADMIN")
    private String role;

    @Schema(example = "101")
    private Long tenantId;
}
