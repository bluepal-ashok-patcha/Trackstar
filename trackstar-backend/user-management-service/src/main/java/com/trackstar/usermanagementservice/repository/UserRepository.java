package com.trackstar.usermanagementservice.repository;

import com.trackstar.usermanagementservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the User entity.
 * It provides CRUD operations and custom query methods.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their username.
     * The multi-tenancy filter will automatically be applied to this query.
     * @param username the username to search for.
     * @return an Optional containing the user if found.
     */
    Optional<User> findByUsername(String username);
}
