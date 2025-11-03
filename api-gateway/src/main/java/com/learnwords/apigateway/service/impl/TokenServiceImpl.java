package com.learnwords.apigateway.service.impl;

import com.learnwords.apigateway.entity.RefreshSession;
import com.learnwords.apigateway.security.RsaKeyConfig;
import com.learnwords.apigateway.service.RefreshTokenStore;
import com.learnwords.apigateway.service.TokenService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.KeyPair;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
class TokenServiceImpl implements TokenService {

    private final KeyPair keyPair;
    private final RefreshTokenStore store;

    TokenServiceImpl(KeyPair keyPair, RefreshTokenStore store) {
        this.keyPair = keyPair;
        this.store = store;
    }

    @Value("${security.jwt.issuer}") String issuer;
    @Value("${security.jwt.kid}") String kid;
    @Value("${security.jwt.access-ttl}") Duration accessTtl;
    @Value("${security.jwt.refresh-ttl}") Duration refreshTtl;

    @Override
    public String createAccessToken(String userId, Collection<String> roles) {
        var now = new Date();
        var exp = new Date(now.getTime() + accessTtl.toMillis());
        return Jwts.builder()
                .setHeaderParam("kid", kid)
                .setIssuer(issuer)
                .setSubject(userId)
                .claim("roles", roles)
                .setIssuedAt(now).setExpiration(exp)
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                .compact();
    }

    @Override
    public String createRefreshToken(String userId, String deviceId) {
        var now = new Date();
        var exp = new Date(now.getTime() + refreshTtl.toMillis());
        var jti = UUID.randomUUID().toString();
        return Jwts.builder()
                .setHeaderParam("kid", kid)
                .setIssuer(issuer)
                .setSubject(userId)
                .setId(jti)
                .claim("deviceId", deviceId)
                .setIssuedAt(now).setExpiration(exp)
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                .compact();
    }

    @Override
    public Mono<Boolean> rotateRefresh(String oldToken, String newToken, Duration ttl) {
        return parseRefresh(oldToken).flatMap(oldOpt -> oldOpt
                .map(old -> parseRefresh(newToken).flatMap(newOpt -> newOpt
                        .map(n -> store.rotate(old.jti(), new RefreshSession(
                                        n.jti(), n.userId(), n.deviceId(), /* accountType */ null, /* userType */ null,
                                        nExp(newToken), iat(newToken), Instant.now()),
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
            var claims = Jwts.parserBuilder().setSigningKey(keyPair.getPublic()).build()
                    .parseClaimsJws(token).getBody();
            var userId = claims.getSubject();
            var deviceId = claims.get("deviceId", String.class);
            var jti = claims.getId();
            return Mono.just(Optional.of(new RefreshPayload(userId, deviceId, jti)));
        } catch (Exception e) {
            return Mono.just(Optional.empty());
        }
    }

    private static Instant nExp(String jwt) {
        var c = Jwts.parserBuilder().build().parseClaimsJwt(jwt.split("\\.")[0] + "." + jwt.split("\\.")[1] + ".").getBody();
        return c.getExpiration().toInstant();
    }
    private static Instant iat(String jwt) {
        var c = Jwts.parserBuilder().build().parseClaimsJwt(jwt.split("\\.")[0] + "." + jwt.split("\\.")[1] + ".").getBody();
        return c.getIssuedAt().toInstant();
    }
}

