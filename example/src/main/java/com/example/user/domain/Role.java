package com.example.user.domain;

public enum Role {
    USER("ROLE_USER"), ADMIN("ROLE_ADMIN");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
