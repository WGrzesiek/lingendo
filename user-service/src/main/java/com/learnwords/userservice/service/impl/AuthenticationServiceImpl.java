package com.learnwords.userservice.service.impl;

import com.learnwords.userservice.service.AppUserDetailService;
import com.learnwords.userservice.service.AuthenticationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expire-in}")
    private Long expireIn;

    private final AppUserDetailService userService;


    public AuthenticationServiceImpl(AppUserDetailService userService){
        this.userService = userService;
    }

    @Override
    public String generateToken(String username, String id, Collection<? extends GrantedAuthority> authorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", id);
        claims.put("authorities", authorities);
        return Jwts.builder()
                .addClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireIn))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public UserDetails validateToken(String token) {
        String username = extractUsername(token);
        log.info("Validating token for user: {}", username);
        return userService.loadUserByUsername(username);
    }

    private String extractUsername(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public Long getExpireIn() {
        return expireIn;
    }

    private Key getSignKey(){
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
