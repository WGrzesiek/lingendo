package com.learnwords.apigateway.service;

import com.learnwords.apigateway.entity.Session;

import java.util.Optional;

public interface SessionService {
    String createSession(String userId, String token, String accountType, String userType, Long expireInMillis);
    Optional<Session> getSession(String sessionId);
    void deleteSession(String sessionId);
}

