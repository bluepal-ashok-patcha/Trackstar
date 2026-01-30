package com.fleetmanager.auth.dto.response;

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
public class TenantRegistrationResponse {

	private Long tenantId;
	private Long adminUserId;
	private String token;
}
