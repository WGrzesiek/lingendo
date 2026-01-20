package com.learnwords.koogservice.messaging

import com.learnwords.koogservice.messaging.dto.generated.SentenceGeneratedEventDto
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

/**
 * Publisher do wysyłania eventów przez Kafka.
 * 
 * Obsługuje publikowanie:
 * - Wygenerowanych zdań (ai.sentence.generated)
 */
@Component
class OutboxPublisher(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {
    
    private val log = LoggerFactory.getLogger(OutboxPublisher::class.java)
    
    companion object {
        const val SENTENCES_GENERATED_TOPIC = "ai.sentence.generated"
    }
    
    /**
     * Publikuje event wygenerowanych zdań.
     */
    fun publishGeneratedSentences(event: SentenceGeneratedEventDto) {
        log.debug(
            "Publikuję wygenerowane zdania - eventId: {}, wordId: {}, liczba zdań: {}",
            event.eventId,
            event.wordId,
            event.sentences.size
        )
        
        try {
            kafkaTemplate.send(SENTENCES_GENERATED_TOPIC, event.wordId, event)
        } catch (e: Exception) {
            log.error("Błąd publikowania eventu zdań: {}", e.message, e)
            throw e
        }
    }

}