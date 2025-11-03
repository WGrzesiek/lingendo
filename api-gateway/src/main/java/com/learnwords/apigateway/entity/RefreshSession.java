package com.learnwords.apigateway.entity;

import lombok.*;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshSession {
    /** jti z refresh tokena */
    private String id;

    private String userId;
    private String deviceId;
    private String accountType;
    private String userType;

    private Instant expiration;
    private Instant createdAt;
    private Instant updatedAt;
}
