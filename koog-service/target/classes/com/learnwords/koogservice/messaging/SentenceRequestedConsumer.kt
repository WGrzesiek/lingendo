package com.learnwords.koogservice.messaging

import com.learnwords.koogservice.application.SentenceGenerationService
import com.learnwords.koogservice.messaging.dto.SentenceGenerationRequestDto
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

/**
 * Konsument Kafka nasłuchujący żądań generowania zdań.
 * 
 * Odbiera wiadomości z topicu `ai.sentence.request` i deleguje 
 * przetwarzanie do serwisu generowania zdań.
 */
@Component
class SentenceRequestedConsumer(
    private val sentenceGenerationService: SentenceGenerationService
) {
    
    private val log = LoggerFactory.getLogger(SentenceRequestedConsumer::class.java)
    
    companion object {
        const val TOPIC = "ai.sentence.request"
        const val GROUP_ID = "koog-service-group"
    }
    
    /**
     * Przetwarza żądanie generowania zdań.
     * 
     * @param request DTO żądania z Kafka
     */
    @KafkaListener(
        topics = [TOPIC],
        groupId = GROUP_ID,
        properties = [
            "spring.json.value.default.type=com.learnwords.koogservice.messaging.dto.SentenceGenerationRequestDto"
        ]
    )
    fun consume(request: SentenceGenerationRequestDto) {
        log.info(
            "Otrzymano żądanie generowania zdań - correlationId: {}, liczba słówek: {}",
            request.id,
            request.words.size
        )
        
        try {
            sentenceGenerationService.processGenerationRequest(request)
            log.info(
                "Zakończono przetwarzanie żądania - correlationId: {}",
                request.id
            )
        } catch (e: Exception) {
            log.error(
                "Błąd podczas przetwarzania żądania - correlationId: {}, błąd: {}",
                request.id,
                e.message,
                e
            )
        }
    }
}