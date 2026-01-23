package com.trackstar.usermanagementservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Represents a user in the system.
 * This will be a JPA entity mapped to the 'users' table.
 */
@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    private Long id;

    private String username;
    private String password; // Hashed password
    private String email;

    // This column is crucial for our multi-tenancy strategy.
    private String tenantId;
}
