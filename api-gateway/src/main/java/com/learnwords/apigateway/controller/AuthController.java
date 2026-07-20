package com.learnwords.apigateway.controller;

import com.learnwords.apigateway.dto.LoginRequest;
import com.learnwords.apigateway.dto.TokenRes;
import com.learnwords.apigateway.dto.UserMeDto;
import com.learnwords.apigateway.entity.RefreshSession;
import com.learnwords.apigateway.service.GrpcClient.impl.UserGrpcClientImpl;
import com.learnwords.apigateway.service.RefreshTokenStore;
import com.learnwords.apigateway.service.TokenService;
import com.learnwords.auth.v1.AuthenticateRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/gateway")
public class AuthController {

    private static final String LEGACY_ACCESS_COOKIE = "access_token";
    private static final String LEGACY_REFRESH_COOKIE = "refresh_token";

    private final UserGrpcClientImpl userService;
    private final TokenService tokens;
    private final RefreshTokenStore store;

    @Value("${security.jwt.access-ttl}")
    Duration accessTtl;

    @Value("${security.jwt.refresh-ttl}")
    Duration refreshTtl;

    @Value("${security.jwt.cookie.access-name:access_token}")
    String accessCookieName;

    @Value("${security.jwt.cookie.refresh-name:refresh_token}")
    String refreshCookieName;

    @Value("${security.jwt.cookie.secure:false}")
    boolean secureCookies;

    @Value("${security.jwt.cookie.same-site:Strict}")
    String sameSite;

    public AuthController(UserGrpcClientImpl userService, TokenService tokens, RefreshTokenStore store) {
        this.userService = userService;
        this.tokens = tokens;
        this.store = store;
    }

    /**
     * Web login. Tokens remain only in HttpOnly cookies and are never exposed to JavaScript.
     */
    @PostMapping("/login")
    public Mono<ResponseEntity<Void>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            ServerHttpResponse response
    ) {
        return authenticateAndCreateSession(loginRequest)
                .map(issued -> {
                    setAuthCookies(response, issued);
                    return ResponseEntity.noContent().<Void>build();
                })
                .switchIfEmpty(Mono.error(new IllegalStateException("Could not create login session")));
    }

    /**
     * Native clients cannot use browser cookies, so they receive tokens on a separate endpoint.
     */
    @PostMapping("/mobile/login")
    public Mono<ResponseEntity<TokenRes>> mobileLogin(@Valid @RequestBody LoginRequest loginRequest) {
        return authenticateAndCreateSession(loginRequest)
                .map(issued -> ResponseEntity.ok(issued.asResponse()))
                .switchIfEmpty(Mono.error(new IllegalStateException("Could not create login session")));
    }

    @PostMapping("/refresh")
    public Mono<ResponseEntity<Void>> refresh(ServerHttpRequest request, ServerHttpResponse response) {
        String oldRefresh = cookieValue(request, refreshCookieName);
        if (oldRefresh == null) {
            clearAuthCookies(response);
            return Mono.just(ResponseEntity.status(401).<Void>build());
        }

        return rotateSession(oldRefresh)
                .map(issued -> {
                    setAuthCookies(response, issued);
                    return ResponseEntity.noContent().<Void>build();
                })
                .switchIfEmpty(Mono.fromSupplier(() -> {
                    clearAuthCookies(response);
                    return ResponseEntity.status(401).<Void>build();
                }));
    }

    @PostMapping("/mobile/refresh")
    public Mono<ResponseEntity<TokenRes>> mobileRefresh(ServerHttpRequest request) {
        String oldRefresh = bearerToken(request);
        if (oldRefresh == null) {
            return Mono.just(ResponseEntity.status(401).build());
        }

        return rotateSession(oldRefresh)
                .map(issued -> ResponseEntity.ok(issued.asResponse()))
                .switchIfEmpty(Mono.just(ResponseEntity.status(401).build()));
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(ServerHttpRequest request, ServerHttpResponse response) {
        String refreshToken = cookieValue(request, refreshCookieName);
        clearAuthCookies(response);

        if (refreshToken == null) {
            return Mono.just(ResponseEntity.noContent().build());
        }

        return tokens.revokeRefresh(refreshToken)
                .onErrorReturn(false)
                .thenReturn(ResponseEntity.noContent().build());
    }

    @PostMapping("/mobile/logout")
    public Mono<ResponseEntity<Void>> mobileLogout(ServerHttpRequest request) {
        String refreshToken = bearerToken(request);
        if (refreshToken == null) {
            return Mono.just(ResponseEntity.noContent().build());
        }
        return tokens.revokeRefresh(refreshToken)
                .onErrorReturn(false)
                .thenReturn(ResponseEntity.noContent().build());
    }

    @GetMapping("/me")
    public Mono<ResponseEntity<UserMeDto>> me(@AuthenticationPrincipal Jwt jwt) {
        return Mono.fromCallable(() -> userService.getUserById(jwt.getSubject()))
                .subscribeOn(Schedulers.boundedElastic())
                .map(user -> ResponseEntity.ok(new UserMeDto(
                        user.getUserId(),
                        user.getUsername(),
                        user.getClaimsOrThrow("accountType"),
                        user.getClaimsOrThrow("userType"),
                        user.getIsEnabled()
                )));
    }

    private Mono<IssuedTokens> authenticateAndCreateSession(LoginRequest loginRequest) {
        var request = AuthenticateRequest.newBuilder()
                .setUsername(loginRequest.username())
                .setPassword(loginRequest.password())
                .build();

        return Mono.fromCallable(() -> userService.authenticate(request))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(auth -> createAndStoreTokens(
                        auth.getUserId(),
                        UUID.randomUUID().toString(),
                        auth.getClaimsOrThrow("accountType"),
                        auth.getClaimsOrThrow("userType")
                ));
    }

    private Mono<IssuedTokens> createAndStoreTokens(
            String userId,
            String deviceId,
            String accountType,
            String userType
    ) {
        String access = tokens.createAccessToken(userId, accountType, userType);
        String refresh = tokens.createRefreshToken(userId, deviceId, accountType, userType);

        return tokens.parseRefresh(refresh)
                .flatMap(optional -> optional
                        .map(payload -> {
                            var session = new RefreshSession(
                                    payload.jti(),
                                    payload.userId(),
                                    payload.deviceId(),
                                    payload.accountType(),
                                    payload.userType(),
                                    payload.expiration(),
                                    payload.issuedAt(),
                                    Instant.now()
                            );
                            return store.save(session, refreshTtl)
                                    .filter(Boolean::booleanValue)
                                    .map(ignored -> new IssuedTokens(access, refresh));
                        })
                        .orElseGet(Mono::empty));
    }

    private Mono<IssuedTokens> rotateSession(String oldRefresh) {
        return tokens.parseRefresh(oldRefresh)
                .flatMap(optional -> optional
                        .map(payload -> {
                            String access = tokens.createAccessToken(
                                    payload.userId(),
                                    payload.accountType(),
                                    payload.userType()
                            );
                            String refresh = tokens.createRefreshToken(
                                    payload.userId(),
                                    payload.deviceId(),
                                    payload.accountType(),
                                    payload.userType()
                            );
                            return tokens.rotateRefresh(oldRefresh, refresh, refreshTtl)
                                    .filter(Boolean::booleanValue)
                                    .map(ignored -> new IssuedTokens(access, refresh));
                        })
                        .orElseGet(Mono::empty));
    }

    private void setAuthCookies(ServerHttpResponse response, IssuedTokens issued) {
        setCookie(response, accessCookieName, issued.accessToken(), accessTtl);
        setCookie(response, refreshCookieName, issued.refreshToken(), refreshTtl);

        if (!LEGACY_ACCESS_COOKIE.equals(accessCookieName)) {
            clearCookie(response, LEGACY_ACCESS_COOKIE);
        }
        if (!LEGACY_REFRESH_COOKIE.equals(refreshCookieName)) {
            clearCookie(response, LEGACY_REFRESH_COOKIE);
        }
    }

    private void clearAuthCookies(ServerHttpResponse response) {
        clearCookie(response, accessCookieName);
        clearCookie(response, refreshCookieName);
        if (!LEGACY_ACCESS_COOKIE.equals(accessCookieName)) {
            clearCookie(response, LEGACY_ACCESS_COOKIE);
        }
        if (!LEGACY_REFRESH_COOKIE.equals(refreshCookieName)) {
            clearCookie(response, LEGACY_REFRESH_COOKIE);
        }
    }

    private void setCookie(ServerHttpResponse response, String name, String value, Duration ttl) {
        response.addCookie(ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secureCookies)
                .path("/")
                .sameSite(sameSite)
                .maxAge(ttl)
                .build());
    }

    private void clearCookie(ServerHttpResponse response, String name) {
        response.addCookie(ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(secureCookies)
                .path("/")
                .sameSite(sameSite)
                .maxAge(Duration.ZERO)
                .build());
    }

    private static String cookieValue(ServerHttpRequest request, String name) {
        var cookie = request.getCookies().getFirst(name);
        return cookie == null || cookie.getValue().isBlank() ? null : cookie.getValue();
    }

    private static String bearerToken(ServerHttpRequest request) {
        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        String token = authorization.substring(7).trim();
        return token.isEmpty() ? null : token;
    }

    private record IssuedTokens(String accessToken, String refreshToken) {
        TokenRes asResponse() {
            return new TokenRes(accessToken, refreshToken);
        }
    }
}
