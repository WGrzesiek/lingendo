package com.learnwords.koogservice.persistence.entity

import com.learnwords.koogservice.enums.EventStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table( name = "outbox")
class Outbox(
    @Id
    @Column(name = "event_id", nullable = false, unique = true)
    val eventId: String? = null,
    @Column(name = "agregate_type", nullable = false)
    val agregateType: String,
    @Column(name = "agregate_id", nullable = false)
    val agregateId: String,
    @Column(name = "event_type", nullable = false)
    val eventType: String,
    @Column(name = "payload", columnDefinition = "jsonb")
    val payload: String?,
    @Column(name = "status", nullable = false)
    val status: String,
    @Column(name = "retry_count", nullable = false)
    val retry_count: Int = 0,
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now()

)