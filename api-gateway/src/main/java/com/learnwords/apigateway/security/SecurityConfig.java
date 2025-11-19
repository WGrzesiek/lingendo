package com.learnwords.apigateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.web.server.authentication.ServerBearerTokenAuthenticationConverter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        var defaultConverter = new ServerBearerTokenAuthenticationConverter();

        ServerAuthenticationConverter cookieFirst = exchange -> {
            var cookie = exchange.getRequest().getCookies().getFirst("access_token");
            if (cookie != null && StringUtils.hasText(cookie.getValue())) {
                var token = cookie.getValue();
                return Mono.just(new BearerTokenAuthenticationToken(token));
            }
            return defaultConverter.convert(exchange);
        };


        return http
                //NOTE potrzebne tylko do deva
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(ex -> ex
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        //NOTE
                        .pathMatchers(
                                "/openapi/**", "/docs", "/swagger-ui/**", "/v3/api-docs/**",
                                "/.well-known/**"
                        ).permitAll()

                        .pathMatchers(HttpMethod.POST, "/api/v1/gateway/login").permitAll()
                        .pathMatchers(HttpMethod.POST, "/login").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/gateway/refresh").permitAll()
                        .pathMatchers(HttpMethod.POST, "/refresh").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/gateway/logout").permitAll()
                        .pathMatchers(HttpMethod.POST, "/logout").permitAll()

                        .pathMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()
                        .pathMatchers(HttpMethod.POST, "/register").permitAll()

                        .pathMatchers("/actuator/health", "/actuator/info", "/actuator/prometheus").permitAll()

                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .bearerTokenConverter(cookieFirst)
                        .jwt(jwt -> jwt
                                .jwkSetUri(jwkSetUri)
                        )
                )
                .build();
    }
}
