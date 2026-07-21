package com.learnwords.koogservice.application

import com.learnwords.koogservice.messaging.dto.SentenceGenerationRequestDto
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

/**
 * Odbiera żądanie z listenera Kafka i uruchamia właściwe generowanie zdań asynchronicznie
 * (poza wątkiem konsumenta). Dzięki temu offset commituje się natychmiast, a długie wywołania
 * OpenAI nie powodują rebalance/redelivery Kafki.
 */
@Component
class SentenceGenerationDispatcher(
    private val sentenceGenerationService: SentenceGenerationService
) {
    private val log = LoggerFactory.getLogger(SentenceGenerationDispatcher::class.java)

    @Async("koogTaskExecutor")
    fun dispatch(request: SentenceGenerationRequestDto) {
        try {
            sentenceGenerationService.processGenerationRequest(request)
        } catch (e: Exception) {
            log.error(
                "Błąd asynchronicznego przetwarzania - correlationId: {}, błąd: {}",
                request.id, e.message, e
            )
        }
    }
}
