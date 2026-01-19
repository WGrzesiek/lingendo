package com.learnwords.koogservice.persistence.repository

import org.springframework.data.repository.Repository
import com.learnwords.koogservice.persistence.entity.Outbox
@org.springframework.stereotype.Repository
interface OutboxRepository : Repository<Outbox, String> {
}