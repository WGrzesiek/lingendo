package com.learnwords.apigateway.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.Instant;


@Data
@Builder
@RedisHash("Session")
public class Session implements Serializable {
    private String id;
    private String userId;
    private String token;
    private String accountType;
    private String userType;
    private Instant expiration;
}
