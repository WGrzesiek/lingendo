package com.learnwords.apigateway.service;

import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public interface TokenService {
    String createAccessToken(String userId, String accountType, String userType);
    String createRefreshToken(String userId, String deviceId, String accountType, String userType);
    Mono<Boolean> rotateRefresh(String oldToken, String newToken, Duration ttl); // Redis
    Mono<Boolean> revokeRefresh(String token);
    Mono<Optional<RefreshPayload>> parseRefresh(String token);
    record RefreshPayload(
            String userId,
            String deviceId,
            String jti,
            String accountType,
            String userType,
            Instant expiration,
            Instant issuedAt
    ) {}
}
