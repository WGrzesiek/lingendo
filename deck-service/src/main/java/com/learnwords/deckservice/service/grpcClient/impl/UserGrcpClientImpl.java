package com.learnwords.deckservice.service.grpcClient.impl;

import com.learnwords.auth.v1.AuthServiceGrpc;
import com.learnwords.auth.v1.GetUserByIdRequest;
import com.learnwords.auth.v1.GetUserNameByIdResponse;
import com.learnwords.deckservice.service.grpcClient.UserGrcpClient;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
@Component
public class UserGrcpClientImpl implements UserGrcpClient {
    private static final long GRPC_DEADLINE_MS = 800;

    @GrpcClient("user-service")
    private AuthServiceGrpc.AuthServiceBlockingStub blockingStub;
    @Override
    public GetUserNameByIdResponse getUserNameById(String userId) {
        var request = GetUserByIdRequest.newBuilder()
                .setUserId(userId)
                .build();
        return blockingStub
                .withDeadlineAfter(GRPC_DEADLINE_MS, TimeUnit.MILLISECONDS)
                .getUserNameById(request);
    }
}
