package com.learnwords.apigateway.service;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface AuthenticationService {
    String generateToken(String username, String id, Collection<? extends GrantedAuthority> authorities);

}