package com.learnwords.apigateway.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class GrpcExceptionHandlerTest {

    private final GrpcExceptionHandler handler = new GrpcExceptionHandler();

    @Test
    void mapsInvalidCredentialsToUnauthorizedWithoutLeakingUpstreamDetails() {
        var exception = new StatusRuntimeException(
                Status.UNAUTHENTICATED.withDescription("Internal authentication detail")
        );

        var response = handler.handle(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(401);
        assertThat(response.getBody().message()).isEqualTo("Invalid username or password");
    }

    @Test
    void mapsUnavailableServiceToServiceUnavailable() {
        var response = handler.handle(new StatusRuntimeException(Status.UNAVAILABLE));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Service temporarily unavailable");
    }

    @Test
    void mapsUnexpectedGrpcFailuresToBadGatewayWithoutLeakingDetails() {
        var exception = new StatusRuntimeException(
                Status.INTERNAL.withDescription("database password was rejected")
        );

        var response = handler.handle(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Upstream service request failed");
    }
}
