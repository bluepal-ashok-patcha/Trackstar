package com.trackstar.usermanagementservice.controller;

import com.trackstar.usermanagementservice.dto.UserDto;
import com.trackstar.usermanagementservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Creates a new user.
     * Accessible only by users with TENANT_ADMIN role.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.createUser(userDto), HttpStatus.CREATED);
    }

    /**
     * Retrieves a user by their ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Retrieves all users in the tenant.
     * Accessible only by users with TENANT_ADMIN or MANAGER role.
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'MANAGER')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
