package com.learnwords.deckservice.service;

import com.learnwords.deckservice.service.event.FlashcardAnsweredEvent;
import com.learnwords.deckservice.service.event.FlashcardProgressEvent;
import com.learnwords.deckservice.service.event.SessionCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

import static com.learnwords.common.KafkaTopic.*;

@Slf4j
@Service
public class StatisticsEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public StatisticsEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publikuje event po każdej odpowiedzi na flashcard
     */
    public void publishFlashcardAnswered(FlashcardAnsweredEvent event) {
        try {
            log.info("Wysyłanie FlashcardAnsweredEvent dla flashcard: {}, userId: {}", 
                event.getFlashcardId(), event.getUserId());
            
            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(FLASHCARD_ANSWERED_TOPIC, event.getFlashcardId(), event);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.debug("FlashcardAnsweredEvent wysłany pomyślnie: {}", event);
                } else {
                    log.error("Błąd podczas wysyłania FlashcardAnsweredEvent: {}", event, ex);
                }
            });
        } catch (Exception e) {
            log.error("Nieoczekiwany błąd podczas publikowania FlashcardAnsweredEvent", e);
        }
    }

    /**
     * Publikuje event po zakończeniu sesji nauki
     */
    public void publishSessionCompleted(SessionCompletedEvent event) {
        try {
            log.info("Wysyłanie SessionCompletedEvent dla session: {}, userId: {}", 
                event.getSessionId(), event.getUserId());
            
            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(SESSION_COMPLETED_TOPIC, event.getSessionId(), event);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("SessionCompletedEvent wysłany pomyślnie: sessionId={}, correctAnswers={}/{}", 
                        event.getSessionId(), event.getCorrectAnswers(), event.getTotalFlashcards());
                } else {
                    log.error("Błąd podczas wysyłania SessionCompletedEvent: {}", event, ex);
                }
            });
        } catch (Exception e) {
            log.error("Nieoczekiwany błąd podczas publikowania SessionCompletedEvent", e);
        }
    }

    /**
     * Publikuje event o zmianie stanu nauki flashcard (np. oznaczenie jako nauczone)
     */
    public void publishFlashcardProgress(FlashcardProgressEvent event) {
        try {
            log.info("Wysyłanie FlashcardProgressEvent dla flashcard: {}, isLearned: {}", 
                event.getFlashcardId(), event.isLearned());
            
            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(FLASHCARD_PROGRESS_TOPIC, event.getFlashcardId(), event);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.debug("FlashcardProgressEvent wysłany pomyślnie: {}", event);
                } else {
                    log.error("Błąd podczas wysyłania FlashcardProgressEvent: {}", event, ex);
                }
            });
        } catch (Exception e) {
            log.error("Nieoczekiwany błąd podczas publikowania FlashcardProgressEvent", e);
        }
    }
}
