package com.fleetmanager.gateway.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfiguration {

	@Bean
    public CorsWebFilter corsWebFilter(
        @Value("${cors.allowed-origins:http://localhost:3000}") String allowedOrigins) {

        org.springframework.web.cors.CorsConfiguration config =
                new org.springframework.web.cors.CorsConfiguration();

        config.setAllowedOriginPatterns(
                Arrays.asList(allowedOrigins.split(",")));

        config.setAllowCredentials(true);
        config.setAllowedMethods(
                Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));

        config.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With",
                "Origin"
        ));

        config.setExposedHeaders(
                Arrays.asList("X-User-Id", "X-Tenant-Id", "X-User-Role"));

        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
