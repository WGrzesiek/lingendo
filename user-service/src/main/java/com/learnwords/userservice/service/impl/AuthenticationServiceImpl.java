package com.learnwords.userservice.service.impl;

import com.learnwords.userservice.service.AuthenticationService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.util.*;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {


    @Value("${jwt.expire-in}")
    private Long expireIn;

    private final KeyPair keyPair;

    public AuthenticationServiceImpl(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    @Override
    public String generateToken(String username, String id,
                                Collection<? extends GrantedAuthority> authorities) {

        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
//                .setIssuer("http://localhost:0003")
                .setIssuer("${spring.application.name}")
                .setSubject(username)
//                .setAudience("learnworlds-api")
                .setAudience("${spring.application.name}")
                .setExpiration(new Date(System.currentTimeMillis() + expireIn))
                .claim("user_id", id)
                .claim("authorities", roles)
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                .compact();
    }

    public Long getExpireIn() {
        return expireIn;
    }

}
