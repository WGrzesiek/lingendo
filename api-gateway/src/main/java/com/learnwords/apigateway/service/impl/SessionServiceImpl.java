package com.learnwords.apigateway.service.impl;

import com.learnwords.apigateway.entity.Session;
import com.learnwords.apigateway.repository.SessionRepository;
import com.learnwords.apigateway.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;

    public SessionServiceImpl(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public String createSession(String userId, String token, String accountType, String userType, Long expireInMillis)
    {
        Instant expiration = Instant.now().plusMillis(expireInMillis);
        Session session = Session.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .token(token)
                .accountType(accountType)
                .userType(userType)
                .expiration(expiration)
                .build();
        sessionRepository.save(session);
        return session.getId();
    }

    @Override
    public Optional<Session> getSession(String sessionId) {
        return Optional.empty();
    }

    @Override
    public void deleteSession(String sessionId) {

    }
}
