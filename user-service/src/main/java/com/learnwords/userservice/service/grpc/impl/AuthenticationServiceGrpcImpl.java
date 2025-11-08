package com.learnwords.userservice.service.grpc.impl;

import com.learnwords.auth.v1.*;
import com.learnwords.userservice.exception.exceptions.WrongPasswordException;
import com.learnwords.userservice.service.UserService;
import com.learnwords.userservice.service.grpc.AuthenticationServiceGrpc;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


@Slf4j
@GrpcService
public class AuthenticationServiceGrpcImpl extends AuthServiceGrpc.AuthServiceImplBase implements AuthenticationServiceGrpc {

    private final UserService userService;

    public AuthenticationServiceGrpcImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void authenticate(AuthenticateRequest request, StreamObserver<AuthenticateResponse> responseObserver) {
        try{
            if (request.getUsername().isBlank() || request.getPassword().isBlank()) {
                responseObserver.onError(new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("Username and password must be provided")));
                return;
            }

            var userDetails = userService.authenticate(request.getUsername(), request.getPassword());

            if (!userDetails.isEnabled()) {
                responseObserver.onError(new StatusRuntimeException(Status.PERMISSION_DENIED.withDescription("User account is disabled")));
                return;
            }

            var roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

            var response = AuthenticateResponse.newBuilder()
                    .setUserId(userDetails.getId())
                    .setUsername(userDetails.getUsername())
                    .addAllRoles(roles)
                    .setIsEnabled(true)
                    .putClaims("accountType", userDetails.getAccountType())
                    .putClaims("userType", userDetails.getUserType())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (WrongPasswordException | UsernameNotFoundException e) {
            log.warn("Authentication failed for username='{}'", request.getUsername());
            responseObserver.onError(new StatusRuntimeException(Status.UNAUTHENTICATED.withDescription("Invalid username or password")));
        } catch (Exception e) {
            log.error("Internal auth error for username='{}'", request.getUsername(), e);
            responseObserver.onError(new StatusRuntimeException(Status.UNAUTHENTICATED.withDescription("Invalid username or password")));
        }
    }

    @Override
    public void getUserById(GetUserByIdRequest request, StreamObserver<GetUserByIdResponse> responseObserver) {
        try {
            if (request.getUserId() == null || request.getUserId().isBlank()) {
                responseObserver.onError(new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("User ID must be provided")));
                return;
            }

            var userDetails = userService.getUserInfo(request.getUserId());

            var response = GetUserByIdResponse.newBuilder()
                    .setUserId(userDetails.getId())
                    .setUsername(userDetails.getUsername())
                    .setIsEnabled(userDetails.isEnabled())
                    .putClaims("accountType", userDetails.getAccountType())
                    .putClaims("userType", userDetails.getUserType())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

    } catch (Exception e) {
        log.error("Internal error while fetching user by ID='{}'", request.getUserId(), e);
        responseObserver.onError(new StatusRuntimeException(Status.UNAUTHENTICATED.withDescription("Internal error while fetching user")));
    }

    }
}
