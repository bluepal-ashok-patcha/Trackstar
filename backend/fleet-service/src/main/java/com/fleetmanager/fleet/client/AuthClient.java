package com.fleetmanager.fleet.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "auth-service")
public interface AuthClient {
    // TODO: Define inter-service communication methods
}
