package com.fleetmanager.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "TrackStar Auth Service API",
        version = "1.0",
        description = "Authentication and Tenant Management APIs"
    )
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // ⭐ KEY LINE
                .servers(List.of(new Server().url("/")));
    }
}
