package com.learnwords.apigateway.exception;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class RedisExceptionHandlerTest {

    private final RedisExceptionHandler handler = new RedisExceptionHandler();

    @Test
    void mapsUnavailableSessionStoreToServiceUnavailableWithoutLeakingDetails() {
        var exception = new RedisConnectionFailureException(
                "redis://user:secret@example.invalid:6379"
        );

        var response = handler.handle(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(503);
        assertThat(response.getBody().message()).isEqualTo("Session service temporarily unavailable");
    }
}
