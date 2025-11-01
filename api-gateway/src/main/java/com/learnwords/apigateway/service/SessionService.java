package com.learnwords.apigateway.service;

import com.learnwords.apigateway.entity.Session;

import java.util.Optional;

public interface SessionService {
    void create(Session session);
    Optional<Session> getBySessionId(String sessionId);
    Optional<Session> getByUserId(String userId);
    boolean updateByUserId(String userId, Session patch);
    boolean deleteBySessionId(String sessionId);
    boolean deleteByUserId(String userId);
}

