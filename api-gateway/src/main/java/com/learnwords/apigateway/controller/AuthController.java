package com.learnwords.apigateway.controller;

import com.learnwords.apigateway.dto.LoginRequest;
import com.learnwords.apigateway.service.GrpcClient.UserGrpcClient;
import com.learnwords.apigateway.service.GrpcClient.impl.UserGrpcClientImpl;
import com.learnwords.apigateway.service.SessionService;
import com.learnwords.apigateway.service.impl.AuthenticationServiceImpl;

import com.learnwords.auth.v1.AuthenticateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.server.reactive.ServerHttpResponse;

import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/gateway")
public class AuthController {

    private final UserGrpcClientImpl userService;
    private final AuthenticationServiceImpl authenticationService;
    private final SessionService sessionService;

    @Value("${spring.security.oauth2.resourceserver.jwt.cookie-name:access_token}")
    private String tokenCookieName;


    public AuthController(UserGrpcClientImpl userService, AuthenticationServiceImpl authenticationService, SessionService sessionService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.sessionService = sessionService;
    }

    @PostMapping(path = "/login/web")
    public ResponseEntity<Map<String,String>> login(@Valid @RequestBody LoginRequest loginRequest, ServerHttpResponse response) {
        var req = AuthenticateRequest.newBuilder()
                .setUsername(loginRequest.username())
                .setPassword(loginRequest.password())
                .build();
        var res = userService.authenticate(req);
        String token = authenticationService.generateToken(res.getUsername(), res.getUserId(),
                                    res.getRolesList().stream()
                                            .map(role -> (GrantedAuthority) () -> role)
                                            .collect(Collectors.toList())
                            );

                            long expireInMillis = authenticationService.getExpireIn();

                            ResponseCookie jwtCookie = ResponseCookie.from(tokenCookieName, token)
                                    .httpOnly(true)
                                    .secure(false)
                                    .path("/")
                                    .maxAge(Duration.ofMillis(expireInMillis))
                                    .build();
                            sessionService.createSession(res.getUserId(), token, res.getRoles(0), "WEB_USER", expireInMillis);

                            response.addCookie(jwtCookie);

                            log.info("User logged in successfully: {}", res.getUsername());

                            return ResponseEntity.ok(Map.of(
                                    "message", "Authorization successful",
                                    "username", res.getUsername()
                            ));
    }}
