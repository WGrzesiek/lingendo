package com.learnwords.apigateway.exception;

import com.learnwords.apigateway.dto.ApiErrorResponse;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GrpcExceptionHandler {

    @ExceptionHandler(StatusRuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handle(StatusRuntimeException exception) {
        Status.Code grpcCode = exception.getStatus().getCode();
        HttpStatus httpStatus = toHttpStatus(grpcCode);
        String message = publicMessage(exception.getStatus(), httpStatus);

        if (httpStatus.is5xxServerError()) {
            log.error("Upstream gRPC call failed with status {}", grpcCode, exception);
        } else {
            log.warn("Upstream gRPC call rejected with status {}: {}", grpcCode, message);
        }

        return ResponseEntity.status(httpStatus)
                .body(new ApiErrorResponse(httpStatus.value(), message));
    }

    private static HttpStatus toHttpStatus(Status.Code code) {
        return switch (code) {
            case INVALID_ARGUMENT, OUT_OF_RANGE -> HttpStatus.BAD_REQUEST;
            case UNAUTHENTICATED -> HttpStatus.UNAUTHORIZED;
            case PERMISSION_DENIED -> HttpStatus.FORBIDDEN;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case ALREADY_EXISTS, ABORTED -> HttpStatus.CONFLICT;
            case FAILED_PRECONDITION -> HttpStatus.UNPROCESSABLE_ENTITY;
            case RESOURCE_EXHAUSTED -> HttpStatus.TOO_MANY_REQUESTS;
            case DEADLINE_EXCEEDED -> HttpStatus.GATEWAY_TIMEOUT;
            case UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
            default -> HttpStatus.BAD_GATEWAY;
        };
    }

    private static String publicMessage(Status status, HttpStatus httpStatus) {
        return switch (status.getCode()) {
            case UNAUTHENTICATED -> "Invalid username or password";
            case UNAVAILABLE -> "Service temporarily unavailable";
            case DEADLINE_EXCEEDED -> "Upstream service timed out";
            default -> {
                String description = status.getDescription();
                if (httpStatus.is4xxClientError() && description != null && !description.isBlank()) {
                    yield description;
                }
                yield "Upstream service request failed";
            }
        };
    }
}
