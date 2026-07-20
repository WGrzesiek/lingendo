package com.learnwords.apigateway.service.impl;

import com.learnwords.apigateway.entity.RefreshSession;
import com.learnwords.apigateway.service.RefreshTokenStore;
import com.learnwords.apigateway.service.TokenService;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.KeyPair;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
class TokenServiceImpl implements TokenService {

    static final String TOKEN_USE_CLAIM = "token_use";
    static final String ACCESS_TOKEN_USE = "access";
    static final String REFRESH_TOKEN_USE = "refresh";

    private final KeyPair keyPair;
    private final RefreshTokenStore store;

    TokenServiceImpl(KeyPair keyPair, RefreshTokenStore store) {
        this.keyPair = keyPair;
        this.store = store;
    }

    @Value("${security.jwt.issuer}") String issuer;
    @Value("${security.jwt.kid}") String kid;
    @Value("${security.jwt.access-audience:lingendo-api}") String accessAudience;
    @Value("${security.jwt.refresh-audience:lingendo-refresh}") String refreshAudience;
    @Value("${security.jwt.access-ttl}") Duration accessTtl;
    @Value("${security.jwt.refresh-ttl}") Duration refreshTtl;

    @Override
    public String createAccessToken(String userId, String accountType, String userType) {
        var now = new Date();
        var exp = new Date(now.getTime() + accessTtl.toMillis());
        return Jwts.builder()
                .header().keyId(kid).and()
                .issuer(issuer)
                .audience().add(accessAudience).and()
                .subject(userId)
                .id(UUID.randomUUID().toString())
                .claim(TOKEN_USE_CLAIM, ACCESS_TOKEN_USE)
                .claim("accountType", accountType)
                .claim("userType", userType)
                .issuedAt(now)
                .expiration(exp)
                .signWith(keyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();
    }

    @Override
    public String createRefreshToken(String userId, String deviceId, String accountType, String userType) {
        var now = new Date();
        var exp = new Date(now.getTime() + refreshTtl.toMillis());
        var jti = UUID.randomUUID().toString();
        return Jwts.builder()
                .header().keyId(kid).and()
                .issuer(issuer)
                .audience().add(refreshAudience).and()
                .subject(userId)
                .id(jti)
                .claim(TOKEN_USE_CLAIM, REFRESH_TOKEN_USE)
                .claim("deviceId", deviceId)
                .claim("accountType",accountType)
                .claim("userType", userType)
                .issuedAt(now)
                .expiration(exp)
                .signWith(keyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();
    }

    @Override
    public Mono<Boolean> rotateRefresh(String oldToken, String newToken, Duration ttl) {
        return parseRefresh(oldToken).flatMap(oldOpt -> oldOpt
                .map(old -> parseRefresh(newToken).flatMap(newOpt -> newOpt
                        .map(n -> store.rotate(old.jti(), new RefreshSession(
                                        n.jti(), n.userId(), n.deviceId(), n.accountType(), n.userType(),
                                        n.expiration(), n.issuedAt(), Instant.now()),
                                ttl))
                        .orElseGet(() -> Mono.just(false))))
                .orElseGet(() -> Mono.just(false)));
    }

    @Override
    public Mono<Boolean> revokeRefresh(String token) {
        return parseRefresh(token).flatMap(opt ->
                opt.map(p -> store.deleteByJti(p.jti())).orElseGet(() -> Mono.just(false)));
    }

    @Override
    public Mono<Optional<RefreshPayload>> parseRefresh(String token) {
        try {
            var claims = Jwts.parser()
                    .verifyWith(keyPair.getPublic())
                    .require(TOKEN_USE_CLAIM, REFRESH_TOKEN_USE)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            var userId = claims.getSubject();
            var deviceId = claims.get("deviceId", String.class);
            var accountType = claims.get("accountType", String.class);
            var userType = claims.get("userType", String.class);
            var jti = claims.getId();
            if (!issuer.equals(claims.getIssuer())
                    || claims.getAudience() == null
                    || !claims.getAudience().contains(refreshAudience)
                    || isBlank(userId) || isBlank(deviceId) || isBlank(jti)
                    || isBlank(accountType) || isBlank(userType)
                    || claims.getExpiration() == null || claims.getIssuedAt() == null
                    || !claims.getExpiration().after(claims.getIssuedAt())) {
                return Mono.just(Optional.empty());
            }
            return Mono.just(Optional.of(new RefreshPayload(
                    userId,
                    deviceId,
                    jti,
                    accountType,
                    userType,
                    claims.getExpiration().toInstant(),
                    claims.getIssuedAt().toInstant()
            )));
        } catch (Exception e) {
            return Mono.just(Optional.empty());
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
