package com.learnwords.userservice.service.grpc;

import com.learnwords.auth.v1.AuthenticateResponse;
import com.learnwords.auth.v1.AuthenticateRequest;
import com.learnwords.auth.v1.GetUserByIdRequest;
import com.learnwords.auth.v1.GetUserByIdResponse;
import io.grpc.stub.StreamObserver;


public interface AuthenticationServiceGrpc {
    void authenticate(AuthenticateRequest request, StreamObserver<AuthenticateResponse> output);
    void getUserById(GetUserByIdRequest request, StreamObserver<GetUserByIdResponse> output);
}
