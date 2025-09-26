package com.learnwords.apigateway.service.GrpcClient;

import com.learnwords.auth.v1.AuthenticateResponse;
import reactor.core.publisher.Mono;

public interface UserGrpcClient {
    Mono<AuthenticateResponse> authenticate(String username, String password);
}
