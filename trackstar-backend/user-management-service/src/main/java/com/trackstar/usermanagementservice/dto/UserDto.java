package com.trackstar.usermanagementservice.dto;

import lombok.Data;

/**
 * Data Transfer Object for User entity.
 * Used to transfer user data between the controller and service layers.
 */
@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
}
