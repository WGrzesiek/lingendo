package com.learnwords.userservice.security;

import com.learnwords.userservice.service.AuthenticationService;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import io.jsonwebtoken.ExpiredJwtException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationService authenticationService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = extractToken(request);
            if (token != null) {
                UserDetails userDetails = authenticationService.validateToken(token);
                log.info("Uwierzytelnianie użytkownika: {}", userDetails.getUsername());
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                if(userDetails instanceof AppUserDetails){
                    request.setAttribute("userId", ((AppUserDetails) userDetails).getId());
                }
            }
        } catch (ExpiredJwtException ex) {
            log.warn("JWT wygasł: {}", ex.getMessage());
        }catch (MalformedJwtException ex){
            log.warn("Nieprawidłowy format JWT: {}", ex.getMessage());
        } catch (SignatureException ex) {
            log.warn("Błąd podpisu JWT: {}", ex.getMessage());
        } catch (Exception ex) {
            log.warn("Nieznany problem z JWT", ex);
        }
        filterChain.doFilter(request, response);
    }


    private String extractToken(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(bearerToken != null && bearerToken.startsWith("Bearer")){
            return bearerToken.substring(7);
        }
        return null;
    }
}