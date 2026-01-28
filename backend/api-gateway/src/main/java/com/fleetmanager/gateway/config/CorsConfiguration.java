package com.fleetmanager.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfiguration {

    @Bean
    public CorsWebFilter corsWebFilter() {

        // Use FULL package name to avoid clash with this class name
        org.springframework.web.cors.CorsConfiguration config =
                new org.springframework.web.cors.CorsConfiguration();

        // Allow React app
        config.addAllowedOrigin("http://localhost:3000");

        // Allow credentials
        config.setAllowCredentials(true);

        // Allow everything else
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        // Apply to all routes
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
