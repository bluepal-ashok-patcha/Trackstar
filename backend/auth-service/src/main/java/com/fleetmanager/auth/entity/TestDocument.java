package com.fleetmanager.auth.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "test_document")
public class TestDocument extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    public Long getId() { 
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
