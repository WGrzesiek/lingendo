package com.learnwords.koogservice.persistence.repository

import org.springframework.data.repository.Repository
import com.learnwords.koogservice.persistence.entity.SentenceGenerationJob

@org.springframework.stereotype.Repository
interface StentenceGenerationJobRepository : Repository<SentenceGenerationJob, String> {
}