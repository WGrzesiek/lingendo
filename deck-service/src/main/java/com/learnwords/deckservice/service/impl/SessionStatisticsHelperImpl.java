//package com.learnwords.deckservice.service.impl;
//
//import com.learnwords.deckservice.entity.Flashcard;
//import com.learnwords.deckservice.entity.Session;
//import com.learnwords.deckservice.enums.SessionStatus;
//import com.learnwords.deckservice.service.event.FlashcardAnsweredEvent;
//import com.learnwords.deckservice.service.event.FlashcardProgressEvent;
//import com.learnwords.deckservice.service.event.SessionCompletedEvent;
//import com.learnwords.deckservice.service.StatisticsEventPublisher;
//import com.learnwords.deckservice.repository.FlashcardRepository;
//import com.learnwords.deckservice.repository.SessionRepository;
//import com.learnwords.deckservice.service.SessionStatisticsHelper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.Duration;
//import java.time.Instant;
//
///**
// * Helper service pokazujący jak integrować StatisticsEventPublisher
// * z logiką sesji nauki
// */
//@Slf4j
//@Service
//public class SessionStatisticsHelperImpl implements SessionStatisticsHelper {
//
//    private final SessionRepository sessionRepository;
//    private final FlashcardRepository flashcardRepository;
//    private final StatisticsEventPublisher statisticsPublisher;
//
//    public SessionStatisticsHelperImpl(
//            SessionRepository sessionRepository,
//            FlashcardRepository flashcardRepository,
//            StatisticsEventPublisher statisticsPublisher) {
//        this.sessionRepository = sessionRepository;
//        this.flashcardRepository = flashcardRepository;
//        this.statisticsPublisher = statisticsPublisher;
//    }
//
//    /**
//     * Rejestruje odpowiedź na flashcard i wysyła event do Statistics Service
//     */
//    @Transactional
//    public void recordFlashcardAnswer(
//            String sessionId,
//            String flashcardId,
//            boolean isCorrect) {
//
//        Session session = sessionRepository.findById(sessionId)
//                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));
//
//        Flashcard flashcard = flashcardRepository.findById(flashcardId)
//                .orElseThrow(() -> new RuntimeException("Flashcard not found: " + flashcardId));
//
//        if (isCorrect) {
//            session.setCorrectAnswers(session.getCorrectAnswers() + 1);
//        } else {
//            session.setWrongAnswers(session.getWrongAnswers() + 1);
//        }
//        sessionRepository.save(session);
//
//        flashcard.setTotalAttempts(flashcard.getTotalAttempts() + 1);
//        if (isCorrect) {
//            flashcard.setCorrectAnswers(flashcard.getCorrectAnswers() + 1);
//        }
//        flashcardRepository.save(flashcard);
//        FlashcardAnsweredEvent event = FlashcardAnsweredEvent.builder()
//                .flashcardId(flashcardId)
//                .wordId(flashcard.getWordId())
//                .deckId(session.getDeck().getId())
//                .userId(session.getUserId())
//                .sessionId(sessionId)
//                .isCorrect(isCorrect)
//                .attemptNumber(flashcard.getTotalAttempts())
//                .answeredAt(Instant.now())
//                .build();
//
//        statisticsPublisher.publishFlashcardAnswered(event);
//
//        log.info("Odpowiedź zarejestrowana: sessionId={}, flashcardId={}, correct={}",
//                sessionId, flashcardId, isCorrect);
//    }
//
//    /**
//     * Oznacza flashcard jako opanowany i wysyła event
//     */
//    @Transactional
//    public void markFlashcardAsLearned(String flashcardId) {
//        Flashcard flashcard = flashcardRepository.findById(flashcardId)
//                .orElseThrow(() -> new RuntimeException("Flashcard not found: " + flashcardId));
//
//        flashcard.setLearned(true);
//        flashcardRepository.save(flashcard);
//        double accuracy = flashcard.getTotalAttempts() > 0
//                ? (double) flashcard.getCorrectAnswers() / flashcard.getTotalAttempts() * 100
//                : 0.0;
//        FlashcardProgressEvent event = FlashcardProgressEvent.builder()
//                .flashcardId(flashcardId)
//                .wordId(flashcard.getWordId())
//                .deckId(flashcard.getDeck().getId())
//                .userId(flashcard.getDeck().getUserId())
//                .isLearned(true)
//                .isSkipped(flashcard.isSkipped())
//                .correctAnswers(flashcard.getCorrectAnswers())
//                .totalAttempts(flashcard.getTotalAttempts())
//                .accuracy(accuracy)
//                .updatedAt(Instant.now())
//                .build();
//
//        statisticsPublisher.publishFlashcardProgress(event);
//
//        log.info("Flashcard oznaczony jako opanowany: flashcardId={}, accuracy={}%",
//                flashcardId, String.format("%.2f", accuracy));
//    }
//
//    /**
//     * Kończy sesję i wysyła event do Statistics Service
//     */
//    @Transactional
//    public void completeSession(String sessionId) {
//        Session session = sessionRepository.findById(sessionId)
//                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));
//
//        // Oblicz czas trwania sesji
//        long durationSeconds = Duration.between(session.getCreatedAt(), Instant.now()).getSeconds();
//
//        // Zaktualizuj sesję
//        session.setStatus(SessionStatus.COMPLETED);
//        session.setCompletedAt(Instant.now());
//        session.setDurationSeconds(durationSeconds);
//        sessionRepository.save(session);
//
//        // Wyślij event do Statistics Service
//        SessionCompletedEvent event = SessionCompletedEvent.builder()
//                .sessionId(sessionId)
//                .deckId(session.getDeck().getId())
//                .userId(session.getUserId())
//                .totalFlashcards(session.getTotalFlashcards())
//                .correctAnswers(session.getCorrectAnswers())
//                .wrongAnswers(session.getWrongAnswers())
//                .skipped(session.getSkipped())
//                .durationSeconds(durationSeconds)
//                .completedAt(session.getCompletedAt())
//                .startedAt(session.getCreatedAt())
//                .build();
//
//        statisticsPublisher.publishSessionCompleted(event);
//
//        log.info("Sesja zakończona: sessionId={}, correctAnswers={}/{}, duration={}s",
//                sessionId,
//                session.getCorrectAnswers(),
//                session.getTotalFlashcards(),
//                durationSeconds);
//    }
//
//    /**
//     * Porzuca sesję (użytkownik wyszedł bez zakończenia)
//     */
//    @Transactional
//    public void abandonSession(String sessionId) {
//        Session session = sessionRepository.findById(sessionId)
//                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));
//
//        session.setStatus(SessionStatus.ABANDONED);
//        sessionRepository.save(session);
//
//        log.info("Sesja porzucona: sessionId={}", sessionId);
//        //TODO wysylac tez SessionAbandonedEvent
//    }
//}
