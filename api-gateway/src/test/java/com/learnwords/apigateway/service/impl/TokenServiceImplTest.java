package com.learnwords.apigateway.service.impl;

import com.learnwords.apigateway.entity.RefreshSession;
import com.learnwords.apigateway.service.RefreshTokenStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.security.KeyPairGenerator;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;

class TokenServiceImplTest {

    private final InMemoryRefreshTokenStore store = new InMemoryRefreshTokenStore();
    private TokenServiceImpl tokens;

    @BeforeEach
    void setUp() throws Exception {
        var generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);

        tokens = new TokenServiceImpl(generator.generateKeyPair(), store);
        tokens.issuer = "learnwords-api";
        tokens.kid = "test-key";
        tokens.accessAudience = "lingendo-api";
        tokens.refreshAudience = "lingendo-refresh";
        tokens.accessTtl = Duration.ofMinutes(15);
        tokens.refreshTtl = Duration.ofDays(30);
    }

    @Test
    void accessTokenCannotBeUsedAsRefreshToken() {
        String access = tokens.createAccessToken("user-1", "BASIC", "NORMAL");

        StepVerifier.create(tokens.parseRefresh(access))
                .assertNext(parsed -> assertThat(parsed).isEmpty())
                .verifyComplete();
    }

    @Test
    void refreshTokenContainsValidatedIdentityAndLifetime() {
        String refresh = tokens.createRefreshToken(
                "user-1",
                "device-1",
                "BASIC",
                "NORMAL"
        );

        StepVerifier.create(tokens.parseRefresh(refresh))
                .assertNext(parsed -> {
                    assertThat(parsed).isPresent();
                    assertThat(parsed.orElseThrow().userId()).isEqualTo("user-1");
                    assertThat(parsed.orElseThrow().deviceId()).isEqualTo("device-1");
                    assertThat(parsed.orElseThrow().expiration()).isAfter(parsed.orElseThrow().issuedAt());
                })
                .verifyComplete();
    }

    @Test
    void usedRefreshTokenCannotBeRotatedTwice() {
        String oldRefresh = tokens.createRefreshToken(
                "user-1",
                "device-1",
                "BASIC",
                "NORMAL"
        );
        var oldPayload = tokens.parseRefresh(oldRefresh).block().orElseThrow();
        store.save(new RefreshSession(
                oldPayload.jti(),
                oldPayload.userId(),
                oldPayload.deviceId(),
                oldPayload.accountType(),
                oldPayload.userType(),
                oldPayload.expiration(),
                oldPayload.issuedAt(),
                oldPayload.issuedAt()
        ), Duration.ofDays(30)).block();

        String firstReplacement = tokens.createRefreshToken(
                "user-1", "device-1", "BASIC", "NORMAL"
        );
        String replayReplacement = tokens.createRefreshToken(
                "user-1", "device-1", "BASIC", "NORMAL"
        );

        StepVerifier.create(tokens.rotateRefresh(oldRefresh, firstReplacement, Duration.ofDays(30)))
                .expectNext(true)
                .verifyComplete();
        StepVerifier.create(tokens.rotateRefresh(oldRefresh, replayReplacement, Duration.ofDays(30)))
                .expectNext(false)
                .verifyComplete();
    }

    private static final class InMemoryRefreshTokenStore implements RefreshTokenStore {
        private final Map<String, RefreshSession> sessions = new ConcurrentHashMap<>();

        @Override
        public Mono<Boolean> save(RefreshSession session, Duration ttl) {
            sessions.put(session.getId(), session);
            return Mono.just(true);
        }

        @Override
        public Mono<Boolean> exists(String jti) {
            return Mono.just(sessions.containsKey(jti));
        }

        @Override
        public Mono<RefreshSession> getByJti(String jti) {
            return Mono.justOrEmpty(sessions.get(jti));
        }

        @Override
        public Mono<List<String>> listJtiByUserId(String userId) {
            return Mono.just(sessions.values().stream()
                    .filter(session -> userId.equals(session.getUserId()))
                    .map(RefreshSession::getId)
                    .toList());
        }

        @Override
        public Mono<Boolean> deleteByJti(String jti) {
            return Mono.just(sessions.remove(jti) != null);
        }

        @Override
        public Mono<Boolean> deleteAllByUserId(String userId) {
            sessions.entrySet().removeIf(entry -> userId.equals(entry.getValue().getUserId()));
            return Mono.just(true);
        }

        @Override
        public Mono<Boolean> rotate(String oldJti, RefreshSession newSession, Duration ttl) {
            if (sessions.remove(oldJti) == null) {
                return Mono.just(false);
            }
            sessions.put(newSession.getId(), newSession);
            return Mono.just(true);
        }
    }
}
