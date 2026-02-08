package com.learnwords.deckservice.service.impl;

import com.learnwords.common.dto.SentenceDto;
import com.learnwords.common.dto.WordDto;
import com.learnwords.deckservice.dto.session.FlashcardSessionNumber;
import com.learnwords.deckservice.dto.sessionFlashcard.SessionFlashcardDto;
import com.learnwords.deckservice.entity.*;
import com.learnwords.deckservice.exception.exceptions.InvalidSessionIdException;
import com.learnwords.deckservice.exception.exceptions.SessionNotFoundException;
import com.learnwords.deckservice.repository.*;
import com.learnwords.deckservice.service.UserProgressService;
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
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private final DeckEnrollmentRepository deckEnrollmentRepository;
    private final UserFlashcardProgressRepository userFlashcardProgressRepository;
    private final UserProgressService userProgressService;
    private final VocabularyGrpcClient vocabularyGrpcClient;


    public SessionFlashcardServiceImpl(
            SessionFlashcardRepository sessionFlashcardRepository,
            SessionRepository sessionRepository,
            AlgorithmFactory algorithmFactory,
            FlashcardFetchStrategyServiceImpl flashcardFetchStrategyServiceImpl,
            DeckEnrollmentRepository deckEnrollmentRepository,
            UserFlashcardProgressRepository userFlashcardProgressRepository,
            UserProgressService userProgressService,
            VocabularyGrpcClient vocabularyGrpcClient

            ) {
        this.sessionFlashcardRepository = sessionFlashcardRepository;
        this.sessionRepository = sessionRepository;
        this.algorithmFactory = algorithmFactory;
        this.flashcardFetchStrategyServiceImpl = flashcardFetchStrategyServiceImpl;
        this.deckEnrollmentRepository = deckEnrollmentRepository;
        this.userFlashcardProgressRepository = userFlashcardProgressRepository;
        this.userProgressService = userProgressService;
        this.vocabularyGrpcClient = vocabularyGrpcClient;

    }

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
                    return new InvalidSessionIdException("Nieprawidłowy identyfikator zapisu do talii: " + enrollmentId);
                }
        );
        if (!enrollment.getDeck().equals(session.getEnrollment().getDeck())) {
            throw new IllegalStateException("Session i enrollment nie pasują do tej samej talii");
        }
        AbstractAlgorithm algorithm = algorithmFactory.get(enrollment.getPreferredAlgorithm());

        List<UserFlashcardProgress> progresses = userFlashcardProgressRepository.findByEnrollment_Id(enrollmentId);
        Set<String> flashcardIdsWithProgress = progresses.stream()
                .map(p -> p.getFlashcard().getId())
                .collect(Collectors.toSet());

        List<Flashcard> allDeckFlashcards = new ArrayList<>(enrollment.getDeck().getFlashcards());


        List<String> flashcardIdsWithoutProgress = allDeckFlashcards.stream()
                .map(Flashcard::getId)
                .filter(id -> !flashcardIdsWithProgress.contains(id))
                .toList();



        if (!flashcardIdsWithoutProgress.isEmpty()) {
            log.info("Znaleziono {} fiszek bez progresu w talii {}. Inicjalizuję...",
                    flashcardIdsWithoutProgress.size(), enrollment.getDeck().getId());

            userProgressService.initializeSessionFlashcardsState(enrollment.getId(), flashcardIdsWithoutProgress, userId);

            progresses = userFlashcardProgressRepository.findByEnrollment_Id(enrollmentId);
        } else {
            log.info("Wszystkie fiszki w talii {} mają już progres. Nic nie inicjalizuję.", enrollment.getDeck().getId());
        }

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

    @Override
    public SessionFlashcardDto getSessionFlashcardsWithWords(String sessionId) {
        List<SessionFlashcard> cards = sessionFlashcardRepository.findBySession_Id(sessionId);
        log.info("Pobrano {} fiszek dla sesji {}", cards.size(), sessionId);
        var words = vocabularyGrpcClient.batchGetWordsByIds(
                cards.stream()
                        .map(card -> card.getFlashcard().getWordId())
                        .toList()
        );

        List<WordDto> wordDtos = words.getWordsList().stream()
                .map(w -> WordDto.builder()
                        .id(w.getId())
                        .word(w.getWord())
                        .translations(w.getTranslationsList())
                        .sentences(w.getSentencesList().stream()
                                .map(s -> SentenceDto.builder()
                                        .id(s.getId())
                                        .sentence(s.getSentence())
                                        .translation(s.getTranslation())
                                        .build())
                                .toList())
                        .sentencesAI(w.getSentencesAiList().stream()
                                .map(s -> SentenceDto.builder()
                                        .id(s.getId())
                                        .sentence(s.getSentence())
                                        .translation(s.getTranslation())
                                        .build())
                                .toList())
                        .build()
                )
                .toList();
        Map<String, WordDto> wordById = wordDtos.stream()
                .collect(Collectors.toMap(WordDto::id, Function.identity()));


        Map<List<String>, List<WordDto>> flashcardWithWords = cards.stream()
                .map(card -> {
                    String flashcardId = card.getFlashcard().getId();
                    String wordId = card.getFlashcard().getWordId();

                    WordDto wordDto = Optional.ofNullable(wordById.get(wordId))
                            .orElseThrow(() -> new IllegalStateException(
                                    "Brak WordDto dla wordId: " + wordId
                            ));

                    return Map.entry(
                            List.of(flashcardId, wordId),
                            List.of(wordDto) // <- typ: List<WordDto>
                    );
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));

        return SessionFlashcardDto.builder()
                .sessionId(sessionId)
                .flashcardWithWords(flashcardWithWords)
                .build();

    }

    @Override
    public List<FlashcardSessionNumber> getFlashcardSessionNumbersByIds(List<String> flashcardIds) {
        if (flashcardIds == null || flashcardIds.isEmpty()) {
            return Collections.emptyList();
        }
        return sessionFlashcardRepository.getFlashcardSessionNumbersByIds(flashcardIds);
    }

}
