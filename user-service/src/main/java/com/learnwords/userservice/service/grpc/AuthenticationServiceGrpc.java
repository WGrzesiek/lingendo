package com.learnwords.userservice.service.grpc;

import com.learnwords.auth.v1.*;
import io.grpc.stub.StreamObserver;


public interface AuthenticationServiceGrpc {
    void authenticate(AuthenticateRequest request, StreamObserver<AuthenticateResponse> output);
    void getUserById(GetUserByIdRequest request, StreamObserver<GetUserByIdResponse> output);
    void getUserNameById(GetUserByIdRequest request, StreamObserver<GetUserNameByIdResponse> responseObserver);
}
