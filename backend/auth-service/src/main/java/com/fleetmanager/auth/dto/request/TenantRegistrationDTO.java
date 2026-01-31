package com.fleetmanager.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Request body for registering a new organization and admin")
public class TenantRegistrationDTO {

    @NotBlank(message = "Organization name is required")
    @Schema(example = "Global Logistics Inc", description = "The full legal name of the organization")
    private String organizationName;

    @NotBlank
    @Size(min = 3, max = 50)
    @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$", message = "Subdomain must be lowercase, alphanumeric, and may contain hyphens")
    @Schema(example = "global-logistics", description = "The unique URL prefix for the tenant")
    private String subdomain;
    
    @NotBlank(message = "Admin name is required")   // ⭐ ADD THIS
    private String adminName;

    @NotBlank(message = "Admin email is required")
    @Email(message = "Invalid email format")
    @Schema(example = "admin@global.com", description = "Email address for the primary administrator")
    private String adminEmail;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Schema(example = "Str0ngP@ss123", description = "Secure password for the admin account")
    private String adminPassword;
  
}