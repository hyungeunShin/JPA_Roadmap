package com.example.user.domain;

import lombok.Getter;

@Getter
public enum Role {
    USER("ROLE_USER"), ADMIN("ROLE_ADMIN");

    private final String description;

    Role(String description) {
        this.description = description;
    }
}
