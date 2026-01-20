package com.learnwords.koogservice.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant

/**
 * Encja Outbox do wzorca transactional outbox.
 * 
 * Przechowuje eventy do wysłania przez Kafka.
 */
@Entity
@Table(name = "outbox")
class Outbox(
    @Id
    @Column(name = "event_id", nullable = false, unique = true)
    val eventId: String? = null,
    
    @Column(name = "agregate_type", nullable = false)
    val agregateType: String = "",
    
    @Column(name = "agregate_id", nullable = false)
    val agregateId: String = "",
    
    @Column(name = "event_type", nullable = false)
    val eventType: String = "",
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb")
    val payload: String? = null,
    
    @Column(name = "status", nullable = false)
    var status: String = "PENDING",
    
    @Column(name = "retry_count", nullable = false)
    var retryCount: Int = 0,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
    
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
) {
    // Konstruktor bezargumentowy wymagany przez JPA
    protected constructor() : this(eventId = null)
}