package com.learnwords.apigateway.security;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class AuthContextPropagationFilterTest {

    private final AuthContextPropagationFilter filter = new AuthContextPropagationFilter();

    @Test
    void forwardsRequestOnceWithIdentityFromJwtAndWithoutBrowserCredentials() {
        Jwt jwt = Jwt.withTokenValue("access-token")
                .header("alg", "RS256")
                .subject("trusted-user")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .build();
        ServerWebExchange exchange = MockServerWebExchange
                .from(MockServerHttpRequest.get("/api/v1/decks")
                        .header("X-User-Id", "spoofed-user")
                        .header("X-Client-Id", "spoofed-client")
                        .header("X-Roles", "ADMIN")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                        .header(HttpHeaders.COOKIE, "access=secret")
                        .build())
                .mutate()
                .principal(Mono.just(new JwtAuthenticationToken(jwt)))
                .build();
        AtomicInteger calls = new AtomicInteger();
        AtomicReference<ServerWebExchange> forwarded = new AtomicReference<>();

        StepVerifier.create(filter.filter(exchange, current -> {
                    calls.incrementAndGet();
                    forwarded.set(current);
                    return Mono.empty();
                }))
                .verifyComplete();

        assertThat(calls).hasValue(1);
        assertThat(forwarded.get().getRequest().getHeaders().getFirst("X-User-Id"))
                .isEqualTo("trusted-user");
        assertThat(forwarded.get().getRequest().getHeaders().containsKey("X-Client-Id")).isFalse();
        assertThat(forwarded.get().getRequest().getHeaders().containsKey("X-Roles")).isFalse();
        assertThat(forwarded.get().getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)).isFalse();
        assertThat(forwarded.get().getRequest().getHeaders().containsKey(HttpHeaders.COOKIE)).isFalse();
    }

    @Test
    void stripsSpoofableIdentityFromPublicRequest() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.post("/api/v1/users/register")
                        .header("X-User-Id", "spoofed-user")
                        .build()
        );
        AtomicReference<ServerWebExchange> forwarded = new AtomicReference<>();

        StepVerifier.create(filter.filter(exchange, current -> {
                    forwarded.set(current);
                    return Mono.empty();
                }))
                .verifyComplete();

        assertThat(forwarded.get().getRequest().getHeaders().containsKey("X-User-Id")).isFalse();
    }
}
