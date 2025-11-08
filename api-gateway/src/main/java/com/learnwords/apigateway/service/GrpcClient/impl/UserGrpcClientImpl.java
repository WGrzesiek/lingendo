package com.learnwords.apigateway.service.GrpcClient.impl;

import com.learnwords.apigateway.service.GrpcClient.UserGrpcClient;
import com.learnwords.auth.v1.*;
import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserGrpcClientImpl {

    @GrpcClient("auth")
    private AuthServiceGrpc.AuthServiceBlockingStub blockingStub;


    public AuthenticateResponse authenticate(AuthenticateRequest request) {
        return blockingStub.authenticate(request);
    }

    public GetUserByIdResponse getUserById(String userId) {
        var request = GetUserByIdRequest.newBuilder()
                .setUserId(userId)
                .build();
        return blockingStub.getUserById(request);
    }

}
