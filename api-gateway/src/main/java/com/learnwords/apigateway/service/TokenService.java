package com.learnwords.apigateway.service;

import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;

public interface TokenService {
    String createAccessToken(String userId, Collection<String> roles);
    String createRefreshToken(String userId, String deviceId);
    Mono<Boolean> rotateRefresh(String oldToken, String newToken, Duration ttl); // Redis
    Mono<Boolean> revokeRefresh(String token);
    Mono<Optional<RefreshPayload>> parseRefresh(String token);
    record RefreshPayload(String userId, String deviceId, String jti) {}
}
