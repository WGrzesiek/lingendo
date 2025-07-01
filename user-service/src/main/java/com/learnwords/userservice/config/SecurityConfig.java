package com.learnwords.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain api(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/.well-known/**", "/api/v1/users/login", "/api/v1/users/register")
                        .permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(authConverter())));

        return http.build();
    }

    private JwtAuthenticationConverter authConverter() {
        var scopes = new JwtGrantedAuthoritiesConverter();
        scopes.setAuthoritiesClaimName("authorities");
        scopes.setAuthorityPrefix("");              // ROLE_ADMIN, a nie SCOPE_
        var conv = new JwtAuthenticationConverter();
        conv.setJwtGrantedAuthoritiesConverter(scopes);
        return conv;
    }




}
