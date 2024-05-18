package com.example.user.domain;

import lombok.Getter;
import org.springframework.security.core.userdetails.User;

@Getter
public class CustomUser extends User {
    private Long id;

    private String profile;

    public CustomUser(com.example.user.domain.User user) {
        super(user.getUsername(), user.getPassword(), user.getAuthorities());
        this.id = user.getId();
        if(user.getProfile() != null) {
            this.profile = user.getProfile().getUploadFileName();
        }
    }
}
