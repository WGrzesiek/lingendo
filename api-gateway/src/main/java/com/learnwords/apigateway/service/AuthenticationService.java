package com.learnwords.apigateway.service;

import com.learnwords.auth.v1.AuthenticateResponse;

import java.util.Map;

public interface AuthenticationService {
    Map<String, Object> login(AuthenticateResponse response);

}