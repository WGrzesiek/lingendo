package com.learnwords.apigateway.service.impl;

import com.learnwords.apigateway.entity.Session;
import com.learnwords.apigateway.service.SessionService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
public class SessionServiceImpl implements SessionService {

    private static final String SESSION_KEY = "session:%s";
    private static final String USER_INDEX_KEY = "idx:user:%s";

    private final StringRedisTemplate redis;

    public SessionServiceImpl(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public Optional<Session> getBySessionId(String sessionId) {
        String key = String.format(SESSION_KEY, sessionId);
        Map<Object, Object> entries = redis.opsForHash().entries(key);
        if (entries.isEmpty()) return Optional.empty();
        return Optional.of(fromMap(entries));
    }

    @Override
    public Optional<Session> getByUserId(String userId) {
        String idxKey = String.format(USER_INDEX_KEY, userId);
        String sessionId = redis.opsForValue().get(idxKey);
        if (sessionId == null) return Optional.empty();
        return getBySessionId(sessionId);
    }

    @Override
    public void create(Session s) {
        String idxKey = String.format(USER_INDEX_KEY, s.getUserId());
        String oldSessionId = redis.opsForValue().get(idxKey);
        if (oldSessionId != null && !oldSessionId.equals(s.getId())) {
            deleteBySessionId(oldSessionId);
        }

        String skey = String.format(SESSION_KEY, s.getId());
        Map<String, String> map = toMap(s);
        redis.opsForHash().putAll(skey, map);

        Duration ttl = ttl(s.getExpiration());
        if (!ttl.isNegative() && !ttl.isZero()) {
            redis.expire(skey, ttl);
        }

        // robimy seta userId -> sessionId
        redis.opsForValue().set(idxKey, s.getId());
        if (!ttl.isNegative() && !ttl.isZero()) {
            redis.expire(idxKey, ttl);
        }

    }




    @Override
    public boolean updateByUserId(String userId, Session patch) {
        String idxKey = String.format(USER_INDEX_KEY, userId);
        String sessionId = redis.opsForValue().get(idxKey);
        if (sessionId == null) return false;

        String skey = String.format(SESSION_KEY, sessionId);

        if (patch.getToken() != null) {
            redis.opsForHash().put(skey, "token", patch.getToken());
        }
        if (patch.getExpiration() != null) {
            String iso = patch.getExpiration().toString();
            redis.opsForHash().put(skey, "expiration", iso);

            Duration ttl = ttl(patch.getExpiration());
            if (!ttl.isNegative() && !ttl.isZero()) {
                redis.expire(skey, ttl);
                redis.expire(idxKey, ttl);
            }
        }
        redis.opsForHash().put(skey, "updatedAt", Instant.now().toString());
        return true;
    }

    @Override
    public boolean deleteBySessionId(String sessionId) {
        String skey = String.format(SESSION_KEY, sessionId);
        Object userId = redis.opsForHash().get(skey, "userId");
        String idxKey = String.format(USER_INDEX_KEY, userId);
        return redis.delete(skey) && redis.delete(idxKey);
    }

    @Override
    public boolean deleteByUserId(String userId) {
        String idxKey = String.format(USER_INDEX_KEY, userId);
        String sessionId = redis.opsForValue().get(idxKey);
        if (sessionId == null) return false;
        return deleteBySessionId(sessionId);
    }

    /* ===================== helpers ===================== */

    private static Duration ttl(Instant expiration) {
        long seconds = expiration.getEpochSecond() - Instant.now().getEpochSecond();
        return Duration.ofSeconds(Math.max(0, seconds));
    }

    private static Map<String, String> toMap(Session s) {
        return Map.of(
                "id", s.getId(),
                "userId", s.getUserId(),
                "token", s.getToken(),
                "accountType", s.getAccountType(),
                "userType", s.getUserType(),
                "expiration", s.getExpiration().toString(),
                "createdAt", s.getCreatedAt().toString(),
                "updatedAt", s.getUpdatedAt().toString()
        );
    }

    private static Session fromMap(Map<Object, Object> m) {
        Session s = new Session();
        s.setId((String) m.get("id"));
        s.setUserId((String) m.get("userId"));
        s.setToken((String) m.get("token"));
        s.setAccountType((String) m.get("accountType"));
        s.setUserType((String) m.get("userType"));
        String exp = (String) m.get("expiration");
        s.setExpiration(Instant.parse(exp));
        String createdAt = (String) m.get("createdAt");
        s.setCreatedAt(Instant.parse(createdAt));
        String updatedAt =(String) m.get("updatedAt");
        s.setUpdatedAt(Instant.parse(updatedAt));
        return s;
    }

}
