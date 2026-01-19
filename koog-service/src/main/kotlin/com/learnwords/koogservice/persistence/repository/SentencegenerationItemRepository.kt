package com.learnwords.koogservice.persistence.repository

import org.springframework.data.repository.Repository
import com.learnwords.koogservice.persistence.entity.SentenceGenerationItem
@org.springframework.stereotype.Repository
interface SentencegenerationItemRepository : Repository<SentenceGenerationItem, String> {
}