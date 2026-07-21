package com.learnwords.apigateway.exception;

import com.learnwords.apigateway.dto.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RedisExceptionHandler {

    @ExceptionHandler(RedisConnectionFailureException.class)
    public ResponseEntity<ApiErrorResponse> handle(RedisConnectionFailureException exception) {
        // Connection exceptions may contain endpoint or credential details in their message.
        log.error("Redis session store is unavailable ({})", exception.getClass().getSimpleName());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ApiErrorResponse(
                        HttpStatus.SERVICE_UNAVAILABLE.value(),
                        "Session service temporarily unavailable"
                ));
    }
}
