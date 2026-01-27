package com.fleetmanager.auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class PasswordResetToken {
    @Id
    private Long id;
    // TODO: Add password reset token fields
}
