package com.learnwords.apigateway.config;

import com.learnwords.apigateway.service.GrpcClient.impl.UserGrpcClientImpl;
import com.learnwords.auth.v1.AuthServiceGrpc;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/**
 * Native reflection hints for net.devh grpc-spring-boot-starter.
 *
 * The {@code @GrpcClient} stub is injected into the target field reflectively by
 * GrpcClientBeanPostProcessor, and the stub itself is built by reflectively invoking
 * {@code AuthServiceGrpc.newBlockingStub(channel)}. Without these hints the field stays
 * null in the native image, causing a NullPointerException in UserGrpcClientImpl.authenticate.
 */
public class ApiGatewayNativeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        // net.devh sets the @GrpcClient field reflectively.
        hints.reflection().registerType(UserGrpcClientImpl.class,
                MemberCategory.DECLARED_FIELDS,
                MemberCategory.INVOKE_DECLARED_METHODS);
        // net.devh StubFactory reflectively calls AuthServiceGrpc.newBlockingStub(Channel).
        hints.reflection().registerType(AuthServiceGrpc.class,
                MemberCategory.INVOKE_DECLARED_METHODS,
                MemberCategory.INVOKE_PUBLIC_METHODS);
    }
}
