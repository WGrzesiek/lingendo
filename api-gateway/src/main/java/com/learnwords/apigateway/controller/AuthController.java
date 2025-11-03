package com.learnwords.apigateway.controller;

import com.learnwords.apigateway.dto.LoginRequest;
import com.learnwords.apigateway.dto.TokenRes;
import com.learnwords.apigateway.entity.RefreshSession;
import com.learnwords.apigateway.service.GrpcClient.impl.UserGrpcClientImpl;
import com.learnwords.apigateway.service.RefreshTokenStore;
import com.learnwords.apigateway.service.TokenService;

import com.learnwords.auth.v1.AuthenticateRequest;
import org.springframework.beans.factory.annotation.Value;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/gateway")
public class AuthController {

    private final UserGrpcClientImpl userService;
    private final TokenService tokens;
    private final RefreshTokenStore store;
    @Value("${security.jwt.refresh-ttl}")
    Duration refreshTtl;


    public AuthController(UserGrpcClientImpl userService, TokenService tokens, RefreshTokenStore store) {
        this.userService = userService;
        this.tokens = tokens;
        this.store = store;

    }

    @PostMapping("/login")
    public Mono<ResponseEntity<TokenRes>> login(@Valid @RequestBody LoginRequest loginRequest, ServerHttpResponse rsp) {
        var req = AuthenticateRequest.newBuilder()
                .setUsername(loginRequest.username())
                .setPassword(loginRequest.password())
                .build();
        String deviceId = UUID.randomUUID().toString();
        var res = userService.authenticate(req);
        var access = tokens.createAccessToken(res.getUserId(), res.getRolesList());
        var refresh = tokens.createRefreshToken(res.getUserId(), deviceId);

        return tokens.parseRefresh(refresh).flatMap(opt -> {
            if (opt.isEmpty()) return Mono.just(ResponseEntity.status(500).build());
            var p = opt.get();
            var session = new RefreshSession(
                    p.jti(), p.userId(), p.deviceId(), null, null,
                    Instant.now().plus(refreshTtl), Instant.now(), Instant.now()
            );
            return store.save(session, refreshTtl).map(ok -> {
                setRefreshCookie(rsp, refresh, refreshTtl);
                return ResponseEntity.ok(new TokenRes(access));
            });
        });
    }

    private static void setRefreshCookie(ServerHttpResponse rsp, String token, Duration ttl) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", token)
                .httpOnly(true)
                .secure(true)
                .path("/auth")
                .sameSite("Strict")
                .maxAge(ttl)
                .build();
        rsp.addCookie(cookie);
    }

    private static void clearRefreshCookie(ServerHttpResponse rsp) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/auth")
                .sameSite("Strict")
                .maxAge(Duration.ZERO)
                .build();
        rsp.addCookie(cookie);
    }
}
//    @PostMapping(path = "/login/web")
//    public ResponseEntity<Map<String,String>> login(@Valid @RequestBody LoginRequest loginRequest, ServerHttpResponse response) {
//        var req = AuthenticateRequest.newBuilder()
//                .setUsername(loginRequest.username())
//                .setPassword(loginRequest.password())
//                .build();
//        var res = userService.authenticate(req);
//        Map<String, Object> session = authenticationService.login(res);
//
//
//        ResponseCookie jwtCookie = ResponseCookie.from(tokenCookieName, (String) session.get("token"))
//                .httpOnly(true)
//                .secure(false)
//                .path("/")
//                .maxAge(Duration.ofMillis((Long) session.get("expireIn")))
//                .build();
//
//        ResponseCookie sessionCookie = ResponseCookie.from("SESSIONID", (String) session.get("sessionId"))
//                .httpOnly(true)
//                .secure(false)
//                .path("/")
//                .maxAge(Duration.ofMillis((Long) session.get("expireIn")))
//                .build();
//        response.addCookie(jwtCookie);
//        response.addCookie(sessionCookie);
//
//        log.info("User logged in successfully: {}", res.getUsername());
//
//        return ResponseEntity.ok(Map.of(
//                "message", "Authorization successful",
//                "username", res.getUsername()
//        ));
//    }}
