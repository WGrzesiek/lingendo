package com.learnwords.apigateway.service.impl;

import com.learnwords.apigateway.entity.RefreshSession;
import com.learnwords.apigateway.service.RefreshTokenStore;
import com.learnwords.apigateway.service.TokenService;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
class TokenServiceImpl implements TokenService {

    static final String TOKEN_USE_CLAIM = "token_use";
    static final String ACCESS_TOKEN_USE = "access";
    static final String REFRESH_TOKEN_USE = "refresh";

    private final RefreshTokenStore store;
    private final JwtEncoder encoder;
    private final NimbusJwtDecoder refreshDecoder;
    private final String issuer;
    private final String kid;
    private final String accessAudience;
    private final String refreshAudience;
    private final Duration accessTtl;
    private final Duration refreshTtl;

    TokenServiceImpl(
            KeyPair keyPair,
            RefreshTokenStore store,
            @Value("${security.jwt.issuer}") String issuer,
            @Value("${security.jwt.kid}") String kid,
            @Value("${security.jwt.access-audience:lingendo-api}") String accessAudience,
            @Value("${security.jwt.refresh-audience:lingendo-refresh}") String refreshAudience,
            @Value("${security.jwt.access-ttl}") Duration accessTtl,
            @Value("${security.jwt.refresh-ttl}") Duration refreshTtl
    ) {
        this.store = store;
        this.issuer = issuer;
        this.kid = kid;
        this.accessAudience = accessAudience;
        this.refreshAudience = refreshAudience;
        this.accessTtl = accessTtl;
        this.refreshTtl = refreshTtl;

        var rsaKey = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey((RSAPrivateKey) keyPair.getPrivate())
                .algorithm(JWSAlgorithm.RS256)
                .keyID(kid)
                .build();
        this.encoder = new NimbusJwtEncoder(
                new ImmutableJWKSet<SecurityContext>(new JWKSet(rsaKey))
        );

        this.refreshDecoder = NimbusJwtDecoder
                .withPublicKey((RSAPublicKey) keyPair.getPublic())
                .signatureAlgorithm(SignatureAlgorithm.RS256)
                .build();
        this.refreshDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(issuer),
                jwt -> jwt.getAudience().contains(refreshAudience)
                        ? OAuth2TokenValidatorResult.success()
                        : invalidToken("Refresh token has an invalid audience"),
                jwt -> REFRESH_TOKEN_USE.equals(jwt.getClaimAsString(TOKEN_USE_CLAIM))
                        ? OAuth2TokenValidatorResult.success()
                        : invalidToken("Only refresh tokens may refresh a session")
        ));
    }

    @Override
    public String createAccessToken(String userId, String accountType, String userType) {
        var now = Instant.now();
        var claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .audience(List.of(accessAudience))
                .subject(userId)
                .id(UUID.randomUUID().toString())
                .claim(TOKEN_USE_CLAIM, ACCESS_TOKEN_USE)
                .claim("accountType", accountType)
                .claim("userType", userType)
                .issuedAt(now)
                .expiresAt(now.plus(accessTtl))
                .build();
        return encode(claims);
    }

    @Override
    public String createRefreshToken(String userId, String deviceId, String accountType, String userType) {
        var now = Instant.now();
        var jti = UUID.randomUUID().toString();
        var claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .audience(List.of(refreshAudience))
                .subject(userId)
                .id(jti)
                .claim(TOKEN_USE_CLAIM, REFRESH_TOKEN_USE)
                .claim("deviceId", deviceId)
                .claim("accountType", accountType)
                .claim("userType", userType)
                .issuedAt(now)
                .expiresAt(now.plus(refreshTtl))
                .build();
        return encode(claims);
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
            Jwt claims = refreshDecoder.decode(token);
            var userId = claims.getSubject();
            var deviceId = claims.getClaimAsString("deviceId");
            var accountType = claims.getClaimAsString("accountType");
            var userType = claims.getClaimAsString("userType");
            var jti = claims.getId();
            if (isBlank(userId) || isBlank(deviceId) || isBlank(jti)
                    || isBlank(accountType) || isBlank(userType)
                    || claims.getExpiresAt() == null || claims.getIssuedAt() == null
                    || !claims.getExpiresAt().isAfter(claims.getIssuedAt())) {
                return Mono.just(Optional.empty());
            }
            return Mono.just(Optional.of(new RefreshPayload(
                    userId,
                    deviceId,
                    jti,
                    accountType,
                    userType,
                    claims.getExpiresAt(),
                    claims.getIssuedAt()
            )));
        } catch (JwtException | IllegalArgumentException e) {
            return Mono.just(Optional.empty());
        }
    }

    private String encode(JwtClaimsSet claims) {
        var header = JwsHeader.with(SignatureAlgorithm.RS256)
                .keyId(kid)
                .build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    private static OAuth2TokenValidatorResult invalidToken(String description) {
        return OAuth2TokenValidatorResult.failure(
                new OAuth2Error("invalid_token", description, null)
        );
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
