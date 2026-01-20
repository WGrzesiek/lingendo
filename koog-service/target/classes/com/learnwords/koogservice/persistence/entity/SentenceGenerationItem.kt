package com.learnwords.koogservice.persistence.entity

import com.learnwords.koogservice.enums.EventStatus
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant

/**
 * Encja reprezentująca pojedynczy element zadania generowania.
 * 
 * Każdy item odpowiada jednemu słówku, dla którego generowane są zdania.
 */
@Entity
@Table(name = "sentence_generation_item")
class SentenceGenerationItem(

    @Id
    @Column(name = "item_id", nullable = false, unique = true)
    val itemId: String? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    var job: SentenceGenerationJob = SentenceGenerationJob(),

    @Column(name = "word_id", nullable = false)
    val wordId: String = "",

    @Column(name = "language_from", nullable = false)
    val languageFrom: String = "",

    @Column(name = "language_to", nullable = false)
    val languageTo: String = "",

    @Column(name = "level", nullable = false)
    val level: String = "B1",

    @Column(name = "category", nullable = false)
    val category: String = "general",

    @Column(name = "prompt_version", nullable = false)
    val promptVersion: Int = 1,

    @Column(name = "model", nullable = false)
    val model: String = "gpt-4o-mini",

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: EventStatus = EventStatus.PENDING,

    @Column(name = "attempts", nullable = false)
    var attempts: Int = 0,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "result_json", columnDefinition = "jsonb")
    var resultJson: String? = null,

    @Column(name = "error_code")
    var errorCode: String? = null,

    @Column(name = "error_message")
    var errorMessage: String? = null,

    @Column(name = "cost_estimate")
    var costEstimate: Double? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
)