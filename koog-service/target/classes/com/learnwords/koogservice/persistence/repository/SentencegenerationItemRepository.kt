package com.learnwords.koogservice.persistence.repository

import com.learnwords.koogservice.enums.EventStatus
import com.learnwords.koogservice.persistence.entity.SentenceGenerationItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface SentenceGenerationItemRepository : JpaRepository<SentenceGenerationItem, String> {

    @Modifying
    @Query("""
        UPDATE SentenceGenerationItem i 
        SET i.status = :status, 
            i.resultJson = :resultJson, 
            i.attempts = i.attempts + 1, 
            i.updatedAt = CURRENT_TIMESTAMP 
        WHERE i.itemId = :itemId
    """)
    fun updateItemResult(itemId: String?, status: EventStatus, resultJson: String?): Int
    

    fun countByJobJobIdAndStatus(jobId: String?, status: EventStatus): Long


}