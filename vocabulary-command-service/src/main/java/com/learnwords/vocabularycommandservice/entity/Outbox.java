package com.learnwords.vocabularycommandservice.entity;


import com.learnwords.common.EventStatus;
import com.learnwords.common.EventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;

import lombok.NoArgsConstructor;

import java.util.Date;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "outbox")
public class Outbox {
    @Id
    private String eventId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EventType eventType;

    @Column(nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EventStatus eventStatus;

    private String aggregateId;
    private int retryCount;
    private Date updatedAt;
    private Date createdAt;



}
