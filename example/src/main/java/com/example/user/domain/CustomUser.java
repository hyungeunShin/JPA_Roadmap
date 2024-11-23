package com.example.user.domain;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUser implements UserDetails {
    private final Long id;
    private final String username;
    private final String profile;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUser(com.example.user.domain.User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.profile = user.getProfile() != null ? user.getProfile().getUploadFileName() : null;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getDescription()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
