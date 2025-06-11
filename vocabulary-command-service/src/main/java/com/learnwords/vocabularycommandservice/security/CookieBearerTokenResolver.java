package com.learnwords.vocabularycommandservice.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.util.StringUtils;

public class CookieBearerTokenResolver implements BearerTokenResolver {

    private static final String COOKIE_NAME = "token";
    private final DefaultBearerTokenResolver defaultResolver;

    public CookieBearerTokenResolver() {
        this.defaultResolver = new DefaultBearerTokenResolver();
    }

    @Override
    public String resolve(HttpServletRequest request) {
        // 1) najpierw cookie
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (COOKIE_NAME.equals(c.getName()) && StringUtils.hasText(c.getValue())) {
                    return c.getValue();
                }
            }
        }

        // 2) w razie czego standardowy Authorization: Bearer ...
        return defaultResolver.resolve(request);
    }
}