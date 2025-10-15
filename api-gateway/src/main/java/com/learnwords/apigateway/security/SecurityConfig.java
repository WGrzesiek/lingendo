package com.learnwords.apigateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.web.server.authentication.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Value("${spring.security.oauth2.resourceserver.jwt.cookie-name}")
    String tokenCookieName = "token";


    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        var defaultConverter = new ServerBearerTokenAuthenticationConverter();

        ServerAuthenticationConverter cookieFirst = exchange -> {
            var cookie = exchange.getRequest().getCookies().getFirst(tokenCookieName);
            if (cookie != null && StringUtils.hasText(cookie.getValue())) {
                var raw = cookie.getValue();
                var token = raw.startsWith("Bearer ") ? raw.substring(7) : raw;
                return Mono.just(new BearerTokenAuthenticationToken(token));
            }
            return defaultConverter.convert(exchange);
        };

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) // stateless
                .authorizeExchange(ex -> ex
                        .pathMatchers("/openapi/**","/docs", "/swagger-ui/**", "/v3/api-docs/**", "/.well-known/**", "/login").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .bearerTokenConverter(cookieFirst)
                        .jwt(jwt -> jwt.jwkSetUri(jwkSetUri))
                )
                .build();
    }
}
