package com.learnwords.deckservice.service.impl;

import com.learnwords.deckservice.entity.*;
import com.learnwords.deckservice.exception.exceptions.InvalidSessionIdException;
import com.learnwords.deckservice.exception.exceptions.SessionNotFoundException;
import com.learnwords.deckservice.repository.*;
import com.learnwords.deckservice.service.algorithm.AbstractAlgorithm;
import com.learnwords.deckservice.service.algorithm.Algorithm;
import com.learnwords.deckservice.service.algorithm.AlgorithmFactory;
import com.learnwords.deckservice.service.grpcClient.VocabularyGrpcClient;
import com.learnwords.deckservice.service.FlashcardFetchStrategy;
import com.learnwords.deckservice.service.SessionFlashcardService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Implementacja serwisu zarządzania fiszkami w sesjach nauki.
 * 
 * <p>Integruje:
 * <ul>
 *   <li>FlashcardFetchStrategyService - sortowanie fiszek</li>
 *   <li>VocabularyGrpcClient - pobieranie danych słówek</li>
 *   <li>AlgorithmFactory - filtrowanie według algorytmu nauki</li>
 * </ul>
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-12
 */
@Slf4j
@Service
public class SessionFlashcardServiceImpl implements SessionFlashcardService {

    private final SessionFlashcardRepository sessionFlashcardRepository;
    private final SessionRepository sessionRepository;
    private final AlgorithmFactory algorithmFactory;
    private final FlashcardFetchStrategyServiceImpl flashcardFetchStrategyServiceImpl;
    private final VocabularyGrpcClient vocabularyGrpcClient;
    private final DeckEnrollmentRepository deckEnrollmentRepository;
    private final UserFlashcardProgressRepository userFlashcardProgressRepository;
    private final Algorithm algorithm;
    private final FlashcardRepository flashcardRepository;

    public SessionFlashcardServiceImpl(
            SessionFlashcardRepository sessionFlashcardRepository,
            SessionRepository sessionRepository,
            AlgorithmFactory algorithmFactory,
            FlashcardFetchStrategyServiceImpl flashcardFetchStrategyServiceImpl,
            VocabularyGrpcClient vocabularyGrpcClient,
            DeckEnrollmentRepository deckEnrollmentRepository,
            UserFlashcardProgressRepository userFlashcardProgressRepository,
            Algorithm algorithm, FlashcardRepository flashcardRepository) {
        this.sessionFlashcardRepository = sessionFlashcardRepository;
        this.sessionRepository = sessionRepository;
        this.algorithmFactory = algorithmFactory;
        this.flashcardFetchStrategyServiceImpl = flashcardFetchStrategyServiceImpl;
        this.vocabularyGrpcClient = vocabularyGrpcClient;
        this.deckEnrollmentRepository = deckEnrollmentRepository;
        this.userFlashcardProgressRepository = userFlashcardProgressRepository;
        this.algorithm = algorithm;
        this.flashcardRepository = flashcardRepository;
    }

//    /**
//     * Dodaje fiszki do sesji według wybranej strategii.
//     *
//     * <p>Proces dodawania fiszek:
//     * <ol>
//     *   <li>Pobiera algorytm nauki przypisany do talii</li>
//     *   <li>Filtruje fiszki - wyłącza te na maksymalnym poziomie nauki</li>
//     *   <li>Sortuje fiszki według strategii (ALPHABETICAL, RANDOM, UNLEARNED_FIRST itp.)</li>
//     *   <li>Ogranicza liczbę fiszek do limitu z ustawień talii (flashcardsPerSession)</li>
//     *   <li>Tworzy relacje SessionFlashcard i zapisuje w bazie</li>
//     * </ol>
//     *
//     * <p>Transakcja zapewnia atomowość - albo wszystkie fiszki zostaną dodane,
//     * albo żadna w przypadku błędu.
//     *
//     * @param session sesja, do której dodawane są fiszki
//     * @param deck talia, z której pobierane są fiszki
//     * @param flashcardFetchStrategy strategia wyboru i sortowania fiszek
//     * @return ID sesji
//     * @throws RuntimeException jeśli brak dostępnych fiszek, błąd sortowania lub błąd DB
//     */
//    @Override
//    @Transactional
//    public void addFlashcardsToSession(Session session, Deck deck, FlashcardFetchStrategy flashcardFetchStrategy, String userId) {
//        log.info("Dodawanie fiszek do sesji: {} z talii: {}", session.getId(), deck.getId());
//        Session session1 = SessionUtils.getSessionIfUserHasPermissions(deckEnrollmentRepository, sessionRepository, deck.getId(), userId, session.getId());
//        AbstractAlgorithm algorithm = algorithmFactory.get(deck.getLearnAlgorithm());
//
//        Set<Flashcard> filteredFlashcards = deck.getFlashcards().stream()
//                .filter(flashcard -> !algorithm.deserialize(flashcard.getAlgorithmState()).getStep().isMaxLevel())
//                .collect(Collectors.toSet());
//
//        log.debug("Przefiltrowano {} fiszek (wyłączono max level)", filteredFlashcards.size());
//
//        if (filteredFlashcards.isEmpty()) {
//            log.warn("Brak fiszek do dodania do sesji - wszystkie są na max level - deckId: '{}'", deck.getId());
//            throw new NoFlashcardsAvailableException(deck.getId(), "wszystkie fiszki są na maksymalnym poziomie nauki");
//        }
//
//        List<Flashcard> filteredFlashcardsList = new ArrayList<>(filteredFlashcards);
//
//        List<Flashcard> sortedFlashcards = flashcardFetchStrategyServiceImpl.sortFlashcardsByStrategy(
//                flashcardFetchStrategy,
//                deck.getHowManyFlashcardsForOneSession(),
//                filteredFlashcardsList);
//
//        log.debug("Po sortowaniu według strategii {}: {} fiszek", flashcardFetchStrategy, sortedFlashcards.size());
//
//        if (sortedFlashcards == null || sortedFlashcards.isEmpty()) {
//            log.error("Brak fiszek po sortowaniu - sprawdź implementację sortFlashcardsByStrategy");
//            throw new RuntimeException("Nie udało się posortować fiszek");
//        }
//
//        Set<SessionFlashcard> sessionFlashcards = sortedFlashcards.stream()
//                .map(flashcard -> SessionFlashcard.builder()
//                        .id(UUID.randomUUID().toString())
//                        .session(session)
//                        .flashcard(flashcard)
//                        .build())
//                .collect(Collectors.toSet());
//
//        sessionFlashcardRepository.saveAll(sessionFlashcards);
//
//        log.info("Pomyślnie dodano {} fiszek do sesji {}", sessionFlashcards.size(), session.getId());
//    }

    @Override
    @Transactional
    public void populateSessionWithFlashcards(String sessionId, String enrollmentId, FlashcardFetchStrategy flashcardFetchStrategy, String userId) {
        log.info("Wypełnianie sesji fiszkami - sessionId: {}, enrollmentId: {}, strategy: {}, userId: {}",
                sessionId, enrollmentId, flashcardFetchStrategy, userId);
        Session session = sessionRepository.findById(sessionId).orElseThrow(
                () -> {
                    log.error("Nie znaleziono sesji o ID: {}", sessionId);
                    return new SessionNotFoundException(sessionId);
                }
        );
        DeckEnrollment enrollment = deckEnrollmentRepository.findById(enrollmentId).orElseThrow(
                () -> {
                    log.error("Nie znaleziono zapisu do talii o ID: {}", enrollmentId);
                    throw new InvalidSessionIdException("Nieprawidłowy identyfikator zapisu do talii: " + enrollmentId);
                }
        );
        if (!enrollment.getDeck().equals(session.getEnrollment().getDeck())) {
            throw new IllegalStateException("Session i enrollment nie pasują do tej samej talii");
        }
        AbstractAlgorithm algorithm = algorithmFactory.get(enrollment.getPreferredAlgorithm());

        List<UserFlashcardProgress> progresses = userFlashcardProgressRepository.findByEnrollment_Id(enrollmentId);


        List<Flashcard> candidateFlashcards = progresses.stream()
                .filter(p -> !p.isSkipped())
                .filter(p -> !p.isLearned())
                .filter(p -> !algorithm.deserialize(p.getAlgorithmState()).getStep().isMaxLevel())
                .map(UserFlashcardProgress::getFlashcard)
                .toList();
        log.debug("Znaleziono {} kandydatów na fiszki do sesji", candidateFlashcards.size());
        List<Flashcard> sortedFlashcards = flashcardFetchStrategyServiceImpl.sortFlashcardsByStrategy(
                flashcardFetchStrategy,
                enrollment.getDeck().getHowManyFlashcardsForOneSession(),
                candidateFlashcards);
        log.debug("Po sortowaniu według strategii {}: {} fiszek", flashcardFetchStrategy, sortedFlashcards.size());
        List<SessionFlashcard> sessionFlashcards = sortedFlashcards.stream()
                .map(flashcard -> SessionFlashcard.builder()
                        .id(UUID.randomUUID().toString())
                        .session(session)
                        .flashcard(flashcard)
                        .build()
                )
                .toList();

        if (session.getSessionFlashcards() == null) {
            session.setSessionFlashcards(new ArrayList<>());
        }
        session.getSessionFlashcards().addAll(sessionFlashcards);

        sessionRepository.save(session);

        log.info("Sesja {} została wypełniona {} fiszkami", sessionId, sessionFlashcards.size());

    }


    @Override
    public List<SessionFlashcard> getSessionFlashcards(String sessionId) {
        List<SessionFlashcard> cards = sessionFlashcardRepository.findBySession_Id(sessionId);
        log.info("Pobrano {} fiszek dla sesji {}", cards.size(), sessionId);
        return cards;
    }

}
