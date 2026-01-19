package com.learnwords.koogservice.persistence.entity

import com.learnwords.koogservice.enums.EventStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table (name = "sentence_generation_item")
class SentenceGenerationItem(

    @Id
    @Column(name = "item_id", nullable = false, unique = true)
    val itemId: String? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    val job: SentenceGenerationJob,

    @Column(name = "word_id", nullable = false)
    val wordId: String,

    @Column(name = "language_from", nullable = false)
    val languageFrom: String,

    @Column(name = "language_to", nullable = false)
    val languageTo: String,

    @Column(name = "level", nullable = false)
    val level: String,

    @Column(name = "category", nullable = false)
    val category: String,

    @Column(name = "prompt_version", nullable = false)
    val promptVersion: Int,

    @Column(name = "model", nullable = false)
    val model: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: EventStatus,

    @Column(name = "attempts", nullable = false)
    val attempts: Int,

    @Column(name = "result_json", columnDefinition = "jsonb",)
    val resultJson: String?,

    @Column(name = "error_code")
    val errorCode: String?,

    @Column(name = "error_message")
    val errorMessage: String?,

    @Column(name = "cost_estimate")
    val costEstimate: Double?,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now()


    )