package com.learnwords.apigateway.security;

import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthContextPropagationFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Po Spring Security Authentication jest już na exchange/principal
        return exchange.getPrincipal()
                .cast(Authentication.class)
                .flatMap(auth -> {
                    Object principal = auth.getPrincipal();
                    if (!(principal instanceof Jwt jwt)) {
                        return chain.filter(exchange);
                    }

                    String userId   = jwt.getSubject();
                    ServerHttpRequest mutatedReq = exchange.getRequest().mutate()
                            .headers(h -> {
                                // Nie ufaj klientowi -> czyść potencjalne spoofowane nagłówki
                                h.remove("X-User-Id");

                                if (userId != null)   h.add("X-User-Id", userId);
                            })
                            .build();

                    return chain.filter(exchange.mutate().request(mutatedReq).build());
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        // Uruchom po Spring Security, ale jeszcze przed właściwymi route filters
        return Ordered.LOWEST_PRECEDENCE - 10;
    }
}

