package com.fleetmanager.auth.service;

import com.fleetmanager.auth.dto.request.LoginRequest;
import com.fleetmanager.auth.dto.response.LoginResponse;
import com.fleetmanager.auth.entity.User;
import com.fleetmanager.auth.enums.UserStatus;
import com.fleetmanager.auth.exception.InvalidCredentialsException;
import com.fleetmanager.auth.repository.TenantRepository;
import com.fleetmanager.auth.repository.UserRepository;
import com.fleetmanager.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public LoginResponse login(LoginRequest request) {

        var tenant = tenantRepository.findBySubdomain(request.getSubdomain())
                .orElseThrow(() -> new InvalidCredentialsException());

        User user = userRepository
                .findByEmailAndTenantId(request.getEmail(), tenant.getId())
                .orElseThrow(() -> new InvalidCredentialsException());

        if (user.getStatus() != UserStatus.ACTIVE ||
            !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        user.setLastLogin(java.time.LocalDateTime.now());

        String token = jwtUtil.generateToken(user);

        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .tenantId(user.getTenantId())
                .build();
    }
}
