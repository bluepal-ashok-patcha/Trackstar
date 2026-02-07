package com.fleetmanager.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Login request payload")
public class LoginRequest {

    @Schema(example = "admin@company.com", description = "User email")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(example = "Password@123", description = "User password")
    @NotBlank(message = "Password is required")
    private String password;

    @Schema(example = "global-logistics", description = "Tenant subdomain")
    @NotBlank(message = "Subdomain is required")
    private String subdomain;
}
