package com.learnwords.apigateway.service.GrpcClient.impl;

import com.learnwords.apigateway.service.GrpcClient.UserGrpcClient;
import com.learnwords.auth.v1.AuthenticateResponse;
import com.learnwords.auth.v1.AuthenticateRequest;
import com.learnwords.auth.v1.AuthServiceGrpc;
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
//public class UserGrpcClientImpl implements UserGrpcClient {
public class UserGrpcClientImpl {

    @GrpcClient("auth")
    private AuthServiceGrpc.AuthServiceBlockingStub blockingStub;


//    @Override
//    public Mono<AuthenticateResponse> authenticate(String username, String password) {
//        return Mono.fromCallable(() -> {
//            try {
//                var request = AuthenticateRequest.newBuilder()
//                        .setUsername(username)
//                        .setPassword(password)
//                        .build();
//                var withDeadline = blockingStub.withDeadline(Deadline.after(800, TimeUnit.MILLISECONDS));
//                return withDeadline.authenticate(request);
//            } catch (Exception e) {
//                log.error("Failed to authenticate user: {}", username, e);
//                throw e;
//            }
//        }).subscribeOn(Schedulers.boundedElastic());
//    }
//    @Override
    public AuthenticateResponse authenticate(AuthenticateRequest request) {
        return blockingStub.authenticate(request);
    }

}
