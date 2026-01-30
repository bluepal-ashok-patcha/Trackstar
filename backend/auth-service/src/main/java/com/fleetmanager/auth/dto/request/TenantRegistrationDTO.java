package com.fleetmanager.auth.dto.request;

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
public class TenantRegistrationDTO {

	@NotBlank(message = "Organization name is required")
	private String organizationName;

	@NotBlank
	@Size(min = 3, max = 50)
	@Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$", message = "Subdomain must be lowercase, alphanumeric, and may contain hyphens")
	private String subdomain;

	@NotBlank(message = "Admin email is required")
	@Email(message = "Invalid email format")
	private String adminEmail;

	@NotBlank(message = "Password is required")
	@Size(min = 8, message = "Password must be at least 8 characters")
	private String adminPassword;
}
