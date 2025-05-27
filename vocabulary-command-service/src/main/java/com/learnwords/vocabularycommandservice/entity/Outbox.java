package com.learnwords.vocabularycommandservice.entity;


import com.learnwords.common.AggregateType;
import com.learnwords.common.EventStatus;
import com.learnwords.common.EventType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.Instant;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "outbox",
        indexes = {
                @Index(name = "idx_outbox_status", columnList = "eventStatus"),
                @Index(name = "idx_outbox_created", columnList = "createdAt")
        })
public class Outbox {
    @Id
    @Column(length = 36, updatable = false)
    private String eventId;

    @Column(nullable = false, length = 36)
    private String aggregateId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AggregateType aggregateType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EventType eventType;

    @Column(columnDefinition = "jsonb", nullable = false)
    @Type(JsonBinaryType.class)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private EventStatus eventStatus = EventStatus.CREATED;

    @Column(nullable = false)
    @Builder.Default
    private int retryCount = 0;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
