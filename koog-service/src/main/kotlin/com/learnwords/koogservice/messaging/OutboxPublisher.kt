package com.learnwords.koogservice.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import com.learnwords.koogservice.messaging.dto.generated.SentenceGeneratedEventDto
import com.learnwords.koogservice.persistence.entity.Outbox
import com.learnwords.koogservice.persistence.repository.OutboxRepository
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Publisher do zapisywania eventów w tabeli outbox.
 * 
 * Debezium (CDC) automatycznie publikuje eventy do Kafka
 * po wykryciu zmian w tabeli outbox.
 */
@Component
class OutboxPublisher(
    private val outboxRepository: OutboxRepository,
) {
    
    private val log = LoggerFactory.getLogger(OutboxPublisher::class.java)
    
    companion object {
        const val AGGREGATE_TYPE_SENTENCE = "sentence"
        const val EVENT_TYPE_GENERATED = "SentenceGenerated"
    }
    
    /**
     * Zapisuje event wygenerowanych zdań do tabeli outbox.
     * Debezium automatycznie opublikuje go do Kafki.
     */
    @Transactional
    fun publishGeneratedSentences(event: SentenceGeneratedEventDto) {
        log.debug(
            "Zapisuję event do outbox - eventId: {}, wordId: {}, liczba zdań: {}",
            event.eventId,
            event.wordId,
            event.sentences.size
        )
        
        try {

            val payload = json.encodeToString(event)

            val outbox = Outbox(
                eventId = event.eventId,
                agregateType = AGGREGATE_TYPE_SENTENCE,
                agregateId = event.wordId,
                eventType = EVENT_TYPE_GENERATED,
                payload = payload,
                status = "PENDING"
            )
            
            outboxRepository.save(outbox)
            
            log.info(
                "Event zapisany do outbox - eventId: {}, wordId: {}",
                event.eventId,
                event.wordId
            )
        } catch (e: Exception) {
            log.error("Błąd zapisywania eventu do outbox: {}", e.message, e)
            throw e
        }
    }
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        explicitNulls = false
    }
}