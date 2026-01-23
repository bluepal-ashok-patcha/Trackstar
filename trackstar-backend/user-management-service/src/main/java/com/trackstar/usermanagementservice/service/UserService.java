package com.trackstar.usermanagementservice.service;

import com.trackstar.usermanagementservice.dto.UserDto;
import java.util.List;

/**
 * Service interface for managing users.
 * Defines the contract for user-related business operations.
 */
public interface UserService {

    /**
     * Creates a new user.
     * @param userDto the user data transfer object.
     * @return the created user.
     */
    UserDto createUser(UserDto userDto);

    /**
     * Finds a user by their ID.
     * @param id the user ID.
     * @return the user if found, otherwise throws an exception.
     */
    UserDto getUserById(Long id);

    /**
     * Retrieves all users within the current tenant.
     * @return a list of all users.
     */
    List<UserDto> getAllUsers();
}
