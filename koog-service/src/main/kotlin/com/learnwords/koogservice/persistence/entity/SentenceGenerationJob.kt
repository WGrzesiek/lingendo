package com.learnwords.koogservice.persistence.entity

import com.learnwords.koogservice.enums.EventStatus
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "sentence_generation_job")
class SentenceGenerationJob(

    @Id
    @Column(name = "job_id", nullable = false, unique = true)
    val jobId: String? = null,

    @Column(name = "correlation_id", nullable = false)
    val correlationId: String,

    @Column(name = "items_total", nullable = false)
    val itemsTotal: Int,

    @Column(name = "items_succeeded")
    val itemsSucceeded: Int?,

    @Column(name = "items_failed")
    val itemsFailed: Int?,

    @Column(name = "requested_by_user_id", nullable = false)
    val requestedByUserId: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: EventStatus,

    @Column(name = "schema_version", nullable = false)
    val schemaVersion: Int,

    @Column(name = "requested_at", nullable = false)
    val requestedAt: Instant = Instant.now(),

    @Column(name = "started_at")
    val startedAt: Instant? = null,

    @Column(name = "finished_at")
    val finishedAt: Instant? = null
)
