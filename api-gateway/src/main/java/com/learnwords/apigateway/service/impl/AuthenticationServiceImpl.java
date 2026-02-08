//package com.learnwords.apigateway.service.impl;
//
//import com.learnwords.apigateway.entity.RefreshSession;
//import com.learnwords.apigateway.service.AuthenticationService;
//import com.learnwords.apigateway.service.SessionService;
//import com.learnwords.auth.v1.AuthenticateResponse;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import lombok.Getter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.security.KeyPair;
//import java.time.Instant;
//import java.util.*;
//
//@Slf4j
//@Service
//public class AuthenticationServiceImpl implements AuthenticationService {
//
//    @Value("${spring.application.name}")
//    private String applicationName;
//
//    @Getter
//    @Value("${jwt.expire-in}")
//    private Long expireIn;
//
//    private final KeyPair keyPair;
//    private final SessionService sessionService;
//
//    public AuthenticationServiceImpl(KeyPair keyPair, SessionService sessionService) {
//        this.keyPair = keyPair;
//        this.sessionService = sessionService;
//    }
//
//    private String generateToken(String username){//, String id, Collection<? extends GrantedAuthority> authorities) {
////        List<String> roles = authorities.stream()
////                .map(GrantedAuthority::getAuthority)
////                .toList();
//        return Jwts.builder()
//                .setIssuer(applicationName)
//                .setSubject(username)
//                .setAudience("${spring.application.name}")
//                .setExpiration(new Date(System.currentTimeMillis() + expireIn))
////NOTE             teraz jak redis i dane o user tam to nie potrzebne
////                .claim("user_id", id)
////                .claim("authorities", roles)
//                .signWith(keyPair.getPrivate() ,SignatureAlgorithm.RS256)
//                .compact();
//    }
//
//
//
//    @Override
//    public Map<String, Object> login(AuthenticateResponse response) {
//        String sessionId = UUID.randomUUID().toString();
//        String token = generateToken(response.getUsername());
//        RefreshSession session = RefreshSession.builder()
//                .id(sessionId)
//                .userId(response.getUserId())
//                .token(token)
//                .accountType("TEST")
//                .userType(String.join(",", response.getRolesList()))
//                .expiration(Instant.now().plusMillis(getExpireIn()))
//                .createdAt(Instant.now())
//                .updatedAt(Instant.now())
//                .build();
//        sessionService.create(session);
//        return Map.of(
//                "sessionId", sessionId,
//                "token", token,
//                "expireIn", getExpireIn()
//        );
//    }
//}
