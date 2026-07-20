package com.learnwords.apigateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthContextPropagationFilter implements GlobalFilter, Ordered {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String CLIENT_ID_HEADER = "X-Client-Id";
    private static final String ROLES_HEADER = "X-Roles";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerWebExchange sanitizedExchange = exchange.mutate()
                .request(sanitize(exchange.getRequest()))
                .build();

        return exchange.getPrincipal()
                .cast(Authentication.class)
                .map(Authentication::getPrincipal)
                .filter(Jwt.class::isInstance)
                .cast(Jwt.class)
                .map(jwt -> withAuthenticatedUser(sanitizedExchange, jwt))
                .defaultIfEmpty(sanitizedExchange)
                .flatMap(chain::filter);
    }

    private static ServerHttpRequest sanitize(ServerHttpRequest request) {
        return request.mutate()
                .headers(headers -> {
                    headers.remove(USER_ID_HEADER);
                    headers.remove(CLIENT_ID_HEADER);
                    headers.remove(ROLES_HEADER);

                    // Internal services trust only identity added by this gateway. They do not
                    // need browser credentials, so avoid spreading reusable tokens downstream.
                    headers.remove(HttpHeaders.AUTHORIZATION);
                    headers.remove(HttpHeaders.COOKIE);
                })
                .build();
    }

    private static ServerWebExchange withAuthenticatedUser(ServerWebExchange exchange, Jwt jwt) {
        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(headers -> headers.set(USER_ID_HEADER, jwt.getSubject()))
                .build();
        return exchange.mutate().request(request).build();
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 10;
    }
}
