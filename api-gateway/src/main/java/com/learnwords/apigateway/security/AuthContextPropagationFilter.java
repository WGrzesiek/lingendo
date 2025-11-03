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
//NOTE zaraz nie bedzie potrzebne bo w redis siedzi info o userze
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

                    // Wyciągnij co potrzebujesz z JWT
                    String userId   = jwt.getSubject();                          // "sub"
                    String clientId = jwt.getClaimAsString("user_id");         // albo inny claim
//                    var roles       = jwt.getClaimAsStringList("roles");         // przykład listy

                    ServerHttpRequest mutatedReq = exchange.getRequest().mutate()
                            .headers(h -> {
                                // Nie ufaj klientowi -> czyść potencjalne spoofowane nagłówki
                                h.remove("X-User-Id");
                                h.remove("X-Client-Id");
//                                h.remove("X-Roles");

                                if (userId != null)   h.add("X-User-Id", userId);
                                if (clientId != null) h.add("X-Client-Id", clientId);
//                                if (roles != null && !roles.isEmpty()) {
//                                    h.add("X-Roles", String.join(",", roles));
//                                }
                                // opcjonalnie trace-id/log-correlation tutaj
                            })
                            .build();

                    return chain.filter(exchange.mutate().request(mutatedReq).build());
                })
                // brak principal? przepuść dalej bez modyfikacji
                .switchIfEmpty(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        // Uruchom po Spring Security, ale jeszcze przed właściwymi route filters
        return Ordered.LOWEST_PRECEDENCE - 10;
    }
}

