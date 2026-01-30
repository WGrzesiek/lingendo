package com.learnwords.apigateway.service;

import com.learnwords.apigateway.entity.RefreshSession;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

public interface RefreshTokenStore {
    Mono<Boolean> save(RefreshSession session, Duration ttl);
    Mono<Boolean> exists(String jti);
    Mono<RefreshSession> getByJti(String jti);
    Mono<List<String>> listJtiByUserId(String userId);
    Mono<Boolean> deleteByJti(String jti);
    Mono<Boolean>deleteAllByUserId(String userId);
    Mono<Boolean> rotate(String oldJti, RefreshSession newSession, Duration ttl);
}
