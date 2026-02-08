package com.learnwords.koogservice.persistence.repository

import com.learnwords.koogservice.persistence.entity.Outbox
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface OutboxRepository : JpaRepository<Outbox, String> {
    
    fun findByStatus(status: String): List<Outbox>
    
    @Modifying
    @Query("UPDATE Outbox o SET o.status = :status, o.updatedAt = CURRENT_TIMESTAMP WHERE o.eventId = :eventId")
    fun updateStatus(eventId: String, status: String): Int
    
    @Modifying
    @Query("DELETE FROM Outbox o WHERE o.status = 'SUCCESS' AND o.createdAt < :beforeDate")
    fun deleteSuccessfulEventsBefore(beforeDate: java.time.Instant): Int
}