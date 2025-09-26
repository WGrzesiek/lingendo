package com.learnwords.userservice.service.grpc;

import com.learnwords.auth.v1.AuthenticateResponse;
import com.learnwords.auth.v1.AuthenticateRequest;
import io.grpc.stub.StreamObserver;


public interface AuthenticationServiceGrpc {
    void authenticate(AuthenticateRequest request, StreamObserver<AuthenticateResponse> output);
}
