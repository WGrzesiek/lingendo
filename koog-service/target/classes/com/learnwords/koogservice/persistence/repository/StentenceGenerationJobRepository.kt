package com.learnwords.koogservice.persistence.repository

import com.learnwords.koogservice.enums.EventStatus
import com.learnwords.koogservice.persistence.entity.SentenceGenerationJob
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface SentenceGenerationJobRepository : JpaRepository<SentenceGenerationJob, String> {
    
    fun findByJobId(jobId: String?): SentenceGenerationJob
    fun findByCorrelationId(correlationId: String): SentenceGenerationJob?
    

    @Modifying
    @Query("""
        UPDATE SentenceGenerationJob j 
        SET j.status = :status, 
            j.startedAt = :startedAt 
        WHERE j.jobId = :jobId
    """)
    fun updateJobStarted(jobId: String?, status: EventStatus, startedAt: Instant): Int
    
    @Modifying
    @Query("""
        UPDATE SentenceGenerationJob j 
        SET j.status = :status, 
            j.itemsSucceeded = :itemsSucceeded, 
            j.itemsFailed = :itemsFailed, 
            j.finishedAt = :finishedAt 
        WHERE j.jobId = :jobId
    """)
    fun updateJobFinished(
        jobId: String?,
        status: EventStatus,
        itemsSucceeded: Int,
        itemsFailed: Int,
        finishedAt: Instant
    ): Int
}