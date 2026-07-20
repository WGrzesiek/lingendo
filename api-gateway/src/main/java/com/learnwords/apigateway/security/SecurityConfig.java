package com.learnwords.apigateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.web.server.authentication.ServerBearerTokenAuthenticationConverter;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final String TOKEN_USE_CLAIM = "token_use";

    @Value("${security.jwt.issuer}")
    private String issuer;

    @Value("${security.jwt.access-audience:lingendo-api}")
    private String accessAudience;

    @Value("${security.jwt.cookie.access-name:access_token}")
    private String accessCookieName;

    @Value("${security.cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    /**
     * Lista ścieżek publicznych - nie wymagają tokenu
     */
    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/api/v1/gateway/login",
            "/api/v1/gateway/refresh",
            "/api/v1/gateway/logout",
            "/api/v1/gateway/mobile/login",
            "/api/v1/gateway/mobile/refresh",
            "/api/v1/gateway/mobile/logout",
            "/api/v1/users/register",
            "/login",
            "/refresh",
            "/logout",
            "/register",
            "/actuator/health",
            "/.well-known/jwks.json"
    );

    @Bean
    SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            ReactiveJwtDecoder jwtDecoder,
            CorsConfigurationSource corsConfigurationSource
    ) {
        var defaultConverter = new ServerBearerTokenAuthenticationConverter();

        ServerAuthenticationConverter cookieFirst = exchange -> {
            String path = exchange.getRequest().getPath().value();

            if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS
                    || PUBLIC_PATHS.contains(path)
                    || path.startsWith("/actuator/health/")) {
                return Mono.empty();
            }

            var cookie = exchange.getRequest().getCookies().getFirst(accessCookieName);
            if (cookie != null && StringUtils.hasText(cookie.getValue())) {
                var token = cookie.getValue();
                return Mono.just(new BearerTokenAuthenticationToken(token));
            }
            return defaultConverter.convert(exchange);
        };

        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(ex -> ex
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/.well-known/jwks.json").permitAll()
                        .pathMatchers(
                                "/api-docs/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/openapi/**"
                        ).denyAll()

                        .pathMatchers(HttpMethod.POST, "/api/v1/gateway/login").permitAll()
                        .pathMatchers(HttpMethod.POST, "/login").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/gateway/refresh").permitAll()
                        .pathMatchers(HttpMethod.POST, "/refresh").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/gateway/logout").permitAll()
                        .pathMatchers(HttpMethod.POST, "/logout").permitAll()

                        .pathMatchers(HttpMethod.POST, "/api/v1/gateway/mobile/login").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/gateway/mobile/refresh").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/gateway/mobile/logout").permitAll()

                        .pathMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()
                        .pathMatchers(HttpMethod.POST, "/register").permitAll()

                        .pathMatchers("/actuator/health", "/actuator/health/**").permitAll()

                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .bearerTokenConverter(cookieFirst)
                        .jwt(jwt -> jwt.jwtDecoder(jwtDecoder))
                )
                .build();
    }

    @Bean
    ReactiveJwtDecoder jwtDecoder(KeyPair keyPair) {
        var decoder = NimbusReactiveJwtDecoder
                .withPublicKey((RSAPublicKey) keyPair.getPublic())
                .build();

        OAuth2TokenValidator<Jwt> issuerValidator = JwtValidators.createDefaultWithIssuer(issuer);
        OAuth2TokenValidator<Jwt> audienceValidator = jwt ->
                jwt.getAudience().contains(accessAudience)
                        ? OAuth2TokenValidatorResult.success()
                        : invalidToken("Access token has an invalid audience");
        OAuth2TokenValidator<Jwt> tokenUseValidator = jwt ->
                "access".equals(jwt.getClaimAsString(TOKEN_USE_CLAIM))
                        ? OAuth2TokenValidatorResult.success()
                        : invalidToken("Only access tokens may authenticate API requests");

        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                issuerValidator,
                audienceValidator,
                tokenUseValidator
        ));
        return decoder;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .toList());
        corsConfig.setMaxAge(8000L);
        corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        corsConfig.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With",
                "X-XSRF-TOKEN"
        ));
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return source;
    }

    private static OAuth2TokenValidatorResult invalidToken(String description) {
        return OAuth2TokenValidatorResult.failure(
                new OAuth2Error("invalid_token", description, null)
        );
    }
}
