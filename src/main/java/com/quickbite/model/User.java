package com.quickbite.model;

import java.time.LocalDateTime;

public class User extends BaseEntity {
    private String name;
    private String email;
    private String passwordHash;
    private String role; // "ROLE_STUDENT" or "ROLE_STAFF"

    public User() {
        super();
        this.role = "ROLE_STUDENT";
    }

    public User(Long id, String name, String email, String passwordHash, String role, LocalDateTime createdAt) {
        super(id, createdAt);
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}