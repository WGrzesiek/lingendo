package com.learnwords.userservice.security;

import com.learnwords.userservice.entity.User;
import com.learnwords.userservice.enums.UserType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


public class AppUserDetails implements UserDetails {
    private final User user;

    public AppUserDetails(User user){
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = switch (user.getUserType()) {
            case ADMIN -> UserType.ADMIN.name();
            case NORMAL -> UserType.NORMAL.name();
        };
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public String getId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
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
        return user.isEnabled();
    }

    public String getUserType() {
        return user.getUserType().name();
    }

    public String getAccountType() {
        return user.getAccountType().name();
    }
}
