package com.learnwords.apigateway.service.impl;

import com.learnwords.apigateway.entity.RefreshSession;
import com.learnwords.apigateway.service.RefreshTokenStore;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RefreshTokenStoreImpl implements RefreshTokenStore {

    private static final String REFRESH_KEY = "auth:refresh:%s";
    private static final String USER_IDX    = "auth:sessions:%s";

    private final ReactiveStringRedisTemplate redis;

    public RefreshTokenStoreImpl(ReactiveStringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public Mono<Boolean> save(RefreshSession s, Duration ttl) {
        String k = REFRESH_KEY.formatted(s.getId());
        String idx = USER_IDX.formatted(s.getUserId());
        Map<String, String> map = toMap(s);
        return Mono.when(
                redis.opsForHash().putAll(k, map),
                redis.expire(k, ttl),
                redis.opsForSet().add(idx, s.getId()),
                redis.expire(idx, ttl)
        ).thenReturn(true);
    }

    @Override
    public Mono<Boolean> exists(String jti) {
        return redis.hasKey(REFRESH_KEY.formatted(jti));
    }

    @Override
    public Mono<RefreshSession> getByJti(String jti) {
        String k = REFRESH_KEY.formatted(jti);
        return redis.opsForHash().entries(k)
                .collectMap(Map.Entry::getKey, Map.Entry::getValue)
                .filter(m -> !m.isEmpty())
                .map(RefreshTokenStoreImpl::fromMap);
    }

    @Override
    public Mono<List<String>> listJtiByUserId(String userId) {
        String idx = USER_IDX.formatted(userId);
        return redis.opsForSet().members(idx)
                .collectList();
    }

    @Override
    public Mono<Boolean> deleteByJti(String jti) {
        String k = REFRESH_KEY.formatted(jti);

        return redis.opsForHash().get(k, "userId")
                .cast(String.class)
                .flatMap(uid -> {
                    return redis.delete(k).flatMap(deleted -> {
                        if (deleted == 0) {
                            return Mono.just(false);
                        }
                        return redis.opsForSet()
                                .remove(USER_IDX.formatted(uid), jti)
                                .thenReturn(true);
                    });
                })
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<Boolean> deleteAllByUserId(String userId) {
        String idx = USER_IDX.formatted(userId);

        return redis.opsForSet().members(idx)
                .collectList()
                .flatMap(jtis -> {
                    List<Mono<Long>> deletions = jtis.stream()
                            .map(jti -> redis.delete(REFRESH_KEY.formatted(jti)))
                            .collect(Collectors.toList());
                    return Mono.when(deletions)
                            .then(redis.delete(idx));
                })
                .thenReturn(true);
    }

    @Override
    public Mono<Boolean> rotate(String oldJti, RefreshSession newSession, Duration ttl) {
        // Zapis nowego tokena następuje przed atomowym DEL starego klucza. Tylko jeden
        // równoległy refresh może usunąć stary klucz; przegrane próby sprzątają swój
        // niewydany token i zwracają false.
        return save(newSession, ttl)
                .then(deleteByJti(oldJti))
                .flatMap(rotated -> {
                    if (rotated) {
                        return Mono.just(true);
                    }
                    return deleteByJti(newSession.getId()).thenReturn(false);
                });
    }

    private static Map<String, String> toMap(RefreshSession s) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("id", s.getId());
        m.put("userId", s.getUserId());
        m.put("deviceId", s.getDeviceId());
        m.put("accountType", s.getAccountType());
        m.put("userType", s.getUserType());
        m.put("expiration", s.getExpiration().toString());
        m.put("createdAt", s.getCreatedAt().toString());
        m.put("updatedAt", s.getUpdatedAt().toString());
        return m;
    }

    private static RefreshSession fromMap(Map<Object, Object> m) {
        RefreshSession s = new RefreshSession();
        s.setId((String) m.get("id"));
        s.setUserId((String) m.get("userId"));
        s.setDeviceId((String) m.get("deviceId"));
        s.setAccountType((String) m.get("accountType"));
        s.setUserType((String) m.get("userType"));
        s.setExpiration(Instant.parse((String) m.get("expiration")));
        s.setCreatedAt(Instant.parse((String) m.get("createdAt")));
        s.setUpdatedAt(Instant.parse((String) m.get("updatedAt")));
        return s;
    }
}
