package com.fleetmanager.auth.config;

import com.fleetmanager.auth.security.TenantFilter;
import com.fleetmanager.auth.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    // Register TenantFilter as a bean so we can wire it into the filter chain
    @Bean
    public TenantFilter tenantFilter(JwtUtil jwtUtil) {
        return new TenantFilter(jwtUtil);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, TenantFilter tenantFilter) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login", "/api/auth/register-tenant","/api/auth/secured-test").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .anyRequest().authenticated()
            );

        // Ensure TenantFilter runs after authentication has been checked/established.
        // If you later add a JwtAuthenticationFilter (service-side), you might want to run tenantFilter AFTER it.
        // For now we place it after UsernamePasswordAuthenticationFilter as a safe spot.
        http.addFilterAfter(tenantFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}