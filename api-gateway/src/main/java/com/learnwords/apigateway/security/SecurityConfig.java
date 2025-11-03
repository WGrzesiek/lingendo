package com.learnwords.apigateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * Jeśli korzystasz z JWKS endpointu (np. "/.well-known/jwks.json"),
     * podaj tutaj pełny URL (zalecane) lub poprawną ścieżkę względną.
     */
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {



        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) // stateless
                .authorizeExchange(ex -> ex
                        .pathMatchers(
                                "/openapi/**", "/docs", "/swagger-ui/**", "/v3/api-docs/**",
                                "/.well-known/**"
                        ).permitAll()

                        .pathMatchers(HttpMethod.POST, "/api/v1/gateway/login").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/gateway/refresh").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/gateway/logout").permitAll()

                        .pathMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()

                        .pathMatchers("/actuator/health", "/actuator/info", "/actuator/prometheus").permitAll()

                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwkSetUri(jwkSetUri)
                        )
                )
                .build();
    }
}
