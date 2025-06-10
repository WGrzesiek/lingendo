package com.learnwords.userservice.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public interface AuthenticationService {
    String generateToken(String username, String id, Collection<? extends GrantedAuthority> authorities);
    UserDetails validateToken(String token);
}