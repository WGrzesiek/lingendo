package com.learnwords.deckservice.service.Session.impl;

import com.learnwords.common.dto.WordDto;
import com.learnwords.deckservice.dto.SessionFlashcardDto;
import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.entity.Flashcard;
import com.learnwords.deckservice.entity.Session;
import com.learnwords.deckservice.entity.SessionFlashcard;
import com.learnwords.deckservice.exception.exceptions.InvalidSessionIdException;
import com.learnwords.deckservice.exception.exceptions.NoFlashcardsAvailableException;
import com.learnwords.deckservice.exception.exceptions.SessionNotFoundException;
import com.learnwords.deckservice.exception.exceptions.UserPermissionsMissing;
import com.learnwords.deckservice.repository.SessionFlashcardRepository;
import com.learnwords.deckservice.repository.SessionRepository;
import com.learnwords.deckservice.service.Algorithm.AbstractAlgorithm;
import com.learnwords.deckservice.service.Algorithm.AlgorithmFactory;
import com.learnwords.deckservice.service.GrpcClient.VocabularyGrpcClient;
import com.learnwords.deckservice.service.Session.FlashcardFetchStrategy.FlashcardFetchStrategy;
import com.learnwords.deckservice.service.Session.FlashcardFetchStrategy.impl.FlashcardFetchStrategyServiceImpl;
import com.learnwords.deckservice.service.Session.SessionFlashcardService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    private final VocabularyGrpcClient vocabularyGrpcClient;

    public SessionFlashcardServiceImpl(
            SessionFlashcardRepository sessionFlashcardRepository,
            SessionRepository sessionRepository,
            AlgorithmFactory algorithmFactory,
            FlashcardFetchStrategyServiceImpl flashcardFetchStrategyServiceImpl,
            VocabularyGrpcClient vocabularyGrpcClient) {
        this.sessionFlashcardRepository = sessionFlashcardRepository;
        this.sessionRepository = sessionRepository;
        this.algorithmFactory = algorithmFactory;
        this.flashcardFetchStrategyServiceImpl = flashcardFetchStrategyServiceImpl;
        this.vocabularyGrpcClient = vocabularyGrpcClient;
    }

    /**
     * Dodaje fiszki do sesji według wybranej strategii.
     * 
     * <p>Proces dodawania fiszek:
     * <ol>
     *   <li>Pobiera algorytm nauki przypisany do talii</li>
     *   <li>Filtruje fiszki - wyłącza te na maksymalnym poziomie nauki</li>
     *   <li>Sortuje fiszki według strategii (ALPHABETICAL, RANDOM, UNLEARNED_FIRST itp.)</li>
     *   <li>Ogranicza liczbę fiszek do limitu z ustawień talii (flashcardsPerSession)</li>
     *   <li>Tworzy relacje SessionFlashcard i zapisuje w bazie</li>
     * </ol>
     * 
     * <p>Transakcja zapewnia atomowość - albo wszystkie fiszki zostaną dodane,
     * albo żadna w przypadku błędu.
     * 
     * @param session sesja, do której dodawane są fiszki
     * @param deck talia, z której pobierane są fiszki
     * @param flashcardFetchStrategy strategia wyboru i sortowania fiszek
     * @return ID sesji
     * @throws RuntimeException jeśli brak dostępnych fiszek, błąd sortowania lub błąd DB
     */
    @Override
    @Transactional
    public void addFlashcardsToSession(Session session, Deck deck, FlashcardFetchStrategy flashcardFetchStrategy, String userId) {
        log.info("Dodawanie fiszek do sesji: {} z talii: {}", session.getId(), deck.getId());
        getSessionIfUserHasPermissions(session.getId(), userId);
        AbstractAlgorithm algorithm = algorithmFactory.get(deck.getLearnAlgorithm());

        Set<Flashcard> filteredFlashcards = deck.getFlashcards().stream()
                .filter(flashcard -> !algorithm.deserialize(flashcard.getAlgorithmState()).getStep().isMaxLevel())
                .collect(Collectors.toSet());

        log.debug("Przefiltrowano {} fiszek (wyłączono max level)", filteredFlashcards.size());

        if (filteredFlashcards.isEmpty()) {
            log.warn("Brak fiszek do dodania do sesji - wszystkie są na max level - deckId: '{}'", deck.getId());
            throw new NoFlashcardsAvailableException(deck.getId(), "wszystkie fiszki są na maksymalnym poziomie nauki");
        }

        List<Flashcard> filteredFlashcardsList = new ArrayList<>(filteredFlashcards);

        List<Flashcard> sortedFlashcards = flashcardFetchStrategyServiceImpl.sortFlashcardsByStrategy(
                flashcardFetchStrategy,
                deck.getHowManyFlashcardsForOneSession(),
                filteredFlashcardsList);

        log.debug("Po sortowaniu według strategii {}: {} fiszek", flashcardFetchStrategy, sortedFlashcards.size());

        if (sortedFlashcards == null || sortedFlashcards.isEmpty()) {
            log.error("Brak fiszek po sortowaniu - sprawdź implementację sortFlashcardsByStrategy");
            throw new RuntimeException("Nie udało się posortować fiszek");
        }

        Set<SessionFlashcard> sessionFlashcards = sortedFlashcards.stream()
                .map(flashcard -> SessionFlashcard.builder()
                        .id(UUID.randomUUID().toString())
                        .session(session)
                        .flashcard(flashcard)
                        .build())
                .collect(Collectors.toSet());

        sessionFlashcardRepository.saveAll(sessionFlashcards);

        log.info("Pomyślnie dodano {} fiszek do sesji {}", sessionFlashcards.size(), session.getId());
    }

    /**
     * Pobiera wszystkie fiszki w sesji z pełnymi danymi słówek.
     * 
     * <p>Wykonuje batch request przez gRPC po wszystkie słówka z fiszek w sesji.
     * Jest to zoptymalizowana metoda - jedno wywołanie gRPC dla wielu słówek
     * zamiast N wywołań dla każdego słówka osobno.
     * 
     * <p>Proces:
     * <ol>
     *   <li>Pobiera SessionFlashcard z bazy dla danej sesji</li>
     *   <li>Wyciąga wordIds z fiszek</li>
     *   <li>Wywołuje batch gRPC request po wszystkie słówka naraz</li>
     *   <li>Mapuje każdą fiszkę + słówko na SessionFlashcardDto</li>
     * </ol>
     * 
     * @param sessionId ID sesji
     * @return lista fiszek w sesji z pełnymi danymi słówek (może być pusta)
     * @throws IllegalArgumentException jeśli sessionId jest null/pusty
     * @throws RuntimeException jeśli sesja nie istnieje, błąd gRPC lub błąd DB
     */
    @Override
    public List<SessionFlashcardDto> getSessionFlashcards(String sessionId, String userId) {
        log.debug("Pobieranie fiszek dla sesji: {}", sessionId);

        getSessionIfUserHasPermissions(sessionId, userId);

        List<SessionFlashcard> sessionFlashcards = sessionFlashcardRepository.findBySessionId(sessionId);

        if (sessionFlashcards.isEmpty()) {
            log.debug("Sesja {} nie zawiera żadnych fiszek", sessionId);
            return Collections.emptyList();
        }

        log.debug("Znaleziono {} fiszek w sesji {}", sessionFlashcards.size(), sessionId);

        List<String> wordIds = sessionFlashcards.stream()
                .map(sf -> sf.getFlashcard().getWordId())
                .toList();

        log.debug("Pobieranie {} słówek przez gRPC", wordIds.size());
        var wordsResponse = vocabularyGrpcClient.batchGetWordsByIds(wordIds);
        log.debug("Otrzymano {} słówek z gRPC", wordsResponse.getWordsCount());

        return sessionFlashcards.stream()
                .map(sessionFlashcard -> {
                    var wordProto = wordsResponse.getWordsList().stream()
                            .filter(w -> w.getId().equals(sessionFlashcard.getFlashcard().getWordId()))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException(
                                    "Nie znaleziono słówka: " + sessionFlashcard.getFlashcard().getWordId()));

                    WordDto wordDto = mapProtoToWordDto(wordProto);
                    return SessionFlashcardDto.from(sessionFlashcard, wordDto);
                })
                .toList();
    }

    /**
     * Pobiera postęp pojedynczej fiszki w sesji.
     * 
     * <p>Sprawdza czy dana fiszka jest przypisana do sesji i zwraca jej
     * pełne dane wraz z informacjami o słówku z Vocabulary Service.
     * 
     * <p>Używaj gdy potrzebujesz informacji o konkretnej fiszce w kontekście
     * sesji (np. przed wyświetleniem fiszki do nauki).
     * 
     * @param sessionId ID sesji
     * @param flashcardId ID fiszki
     * @return Optional z SessionFlashcardDto jeśli fiszka jest w sesji, Optional.empty() jeśli nie
     * @throws IllegalArgumentException jeśli sessionId lub flashcardId jest null/pusty
     * @throws RuntimeException jeśli błąd gRPC lub błąd DB
     */
    @Override
    public Optional<SessionFlashcardDto> getFlashcardProgress(String sessionId, String flashcardId, String userId) {
        log.debug("Pobieranie postępu fiszki {} w sesji {}", flashcardId, sessionId);

        getSessionIfUserHasPermissions(sessionId, userId);

        Optional<SessionFlashcard> sessionFlashcard = sessionFlashcardRepository
                .findBySessionIdAndFlashcardId(sessionId, flashcardId);

        if (sessionFlashcard.isEmpty()) {
            log.debug("Fiszka {} nie jest w sesji {}", flashcardId, sessionId);
            return Optional.empty();
        }

        var wordResponse = vocabularyGrpcClient.getWordById(sessionFlashcard.get().getFlashcard().getWordId());
        WordDto wordDto = mapProtoToWordDto(wordResponse.getWord());

        return Optional.of(SessionFlashcardDto.from(sessionFlashcard.get(), wordDto));
}

    /**
     * Pomija fiszkę w sesji.
     * 
     * <p>Oznacza fiszkę jako pominiętą w bieżącej sesji. Wykonuje następujące akcje:
     * <ol>
     *   <li>Sprawdza czy fiszka jest przypisana do sesji</li>
     *   <li>Inkrementuje licznik {@code session.skipped}</li>
     *   <li>Ustawia {@code flashcard.skipped = true}</li>
     * </ol>
     * 
     * <p>Transakcja zapewnia atomowość - oba zapisy (session i flashcard)
     * zostaną wykonane albo żaden.
     * 
     * <p>Pominięte fiszki są liczone w statystykach sesji ale nie wpływają
     * na accuracy (nie są traktowane jako błędne odpowiedzi).
     * 
     * @param sessionId ID sesji
     * @param flashcardId ID fiszki do pominięcia
     * @throws IllegalArgumentException jeśli sessionId lub flashcardId jest null/pusty
     * @throws RuntimeException jeśli fiszka nie jest w sesji, sesja nie istnieje lub błąd DB
     */
    @Override
    @Transactional
    public void skipFlashcard(String sessionId, String flashcardId, String userId) {
        log.debug("Pomijanie fiszki {} w sesji {}", flashcardId, sessionId);
        Session session = getSessionIfUserHasPermissions(sessionId, userId);

        if (flashcardId == null || flashcardId.isBlank()) {
            log.error("SessionId lub flashcardId jest null/pusty");
            throw new IllegalArgumentException("SessionId i flashcardId nie mogą być puste");
        }

        SessionFlashcard sessionFlashcard = sessionFlashcardRepository
                .findBySessionIdAndFlashcardId(sessionId, flashcardId)
                .orElseThrow(() -> new RuntimeException(
                        "Fiszka " + flashcardId + " nie jest w sesji " + sessionId));

        session.setSkipped(session.getSkipped() + 1);
        sessionRepository.save(session);

        Flashcard flashcard = sessionFlashcard.getFlashcard();
        flashcard.setSkipped(true);

        log.info("Pominięto fiszkę {} w sesji {}. Łącznie pominiętych: {}",
                flashcardId, sessionId, session.getSkipped());
    }

    /**
     * Pobiera łączną liczbę fiszek w sesji.
     * 
     * <p>Używa zoptymalizowanego zapytania COUNT zamiast pobierania
     * wszystkich encji i liczenia w aplikacji.
     * 
     * @param sessionId ID sesji
     * @return liczba fiszek w sesji (0 jeśli sesja jest pusta)
     * @throws IllegalArgumentException jeśli sessionId jest null/pusty
     * @throws RuntimeException jeśli błąd DB
     */
    @Override
    public int getTotalFlashcardsInSession(String sessionId, String userId) {
        log.debug("Pobieranie liczby fiszek w sesji: {}", sessionId);

        getSessionIfUserHasPermissions(sessionId, userId);

        int count = sessionFlashcardRepository.countBySessionId(sessionId);
        log.debug("Sesja {} zawiera {} fiszek", sessionId, count);
        return count;

    }

    /**
     * Pobiera liczbę fiszek na które użytkownik udzielił odpowiedzi w sesji.
     * 
     * <p>Zwraca sumę poprawnych i błędnych odpowiedzi:
     * {@code answeredCount = session.correctAnswers + session.wrongAnswers}
     * 
     * <p>Pominięte fiszki (skipped) NIE są liczone jako answered.
     * 
     * @param sessionId ID sesji
     * @return liczba fiszek z odpowiedzią (0 jeśli użytkownik jeszcze nie odpowiedział)
     * @throws IllegalArgumentException jeśli sessionId jest null/pusty
     * @throws RuntimeException jeśli sesja nie istnieje lub błąd DB
     */
    @Override
    public int getAnsweredFlashcardsCount(String sessionId, String userId) {
        log.debug("Pobieranie liczby fiszek z odpowiedzią w sesji: {}", sessionId);

        Session session = getSessionIfUserHasPermissions(sessionId, userId);

        int answered = session.getCorrectAnswers() + session.getWrongAnswers();
        log.debug("W sesji {} udzielono {} odpowiedzi (poprawne: {}, błędne: {})",
                sessionId, answered, session.getCorrectAnswers(), session.getWrongAnswers());
        return answered;
    }

    /**
     * Pobiera sesję jeśli użytkownik ma do niej uprawnienia.
     *
     * <p>Metoda pomocnicza weryfikująca czy użytkownik jest właścicielem sesji.
     *
     * @param sessionId ID sesji
     * @param userId ID użytkownika
     * @return sesja jeśli użytkownik ma uprawnienia
     * @throws InvalidSessionIdException gdy sessionId jest null lub pusty
     * @throws SessionNotFoundException gdy sesja o podanym ID nie istnieje
     * @throws UserPermissionsMissing gdy użytkownik nie ma uprawnień do sesji
     */
    private Session getSessionIfUserHasPermissions(String sessionId, String userId) {
        if (sessionId == null || sessionId.isBlank()) {
            log.error("Próba dostępu do sesji z pustym sessionId - userId: '{}'", userId);
            throw new InvalidSessionIdException();
        }

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> {
                    log.error("Nie znaleziono sesji - sessionId: '{}', userId: '{}'", sessionId, userId);
                    return new SessionNotFoundException(sessionId);
                });

        if (!session.getUserId().equals(userId)) {
            log.warn("Brak uprawnień do sesji - userId: '{}', sessionId: '{}', sessionOwnerId: '{}'",
                    userId, sessionId, session.getUserId());
            throw new UserPermissionsMissing("Użytkownik nie ma uprawnień do tej sesji");
        }

        log.debug("Zweryfikowano uprawnienia do sesji - sessionId: '{}', userId: '{}'", sessionId, userId);
        return session;
    }


    /**
     * Mapuje Proto Word (z gRPC) na domenowy WordDto.
     * 
     * <p>Konwertuje struktury Protocol Buffers otrzymane z Vocabulary Service
     * na zwykłe Java DTOs używane w aplikacji.
     * 
     * <p>Mapuje:
     * <ul>
     *   <li>Podstawowe dane słówka (id, word, translations)</li>
     *   <li>Zdania przykładowe ({@code sentences})</li>
     *   <li>Zdania wygenerowane przez AI ({@code sentencesAi})</li>
     * </ul>
     * 
     * @param wordProto Proto Word z gRPC response
     * @return WordDto z pełnymi danymi (słowo, tłumaczenia, zdania)
     */
    private WordDto mapProtoToWordDto(com.learnwords.vocabulary.v1.Word wordProto) {
        return new WordDto(
                wordProto.getId(),
                wordProto.getWord(),
                wordProto.getTranslationsList(),
                wordProto.getSentencesList().stream()
                        .map(s -> new com.learnwords.common.dto.SentenceDto(
                                s.getId(),
                                s.getSentence(),
                                s.getTranslation()
                        ))
                        .toList(),
                wordProto.getSentencesAiList().stream()
                        .map(s -> new com.learnwords.common.dto.SentenceDto(
                                s.getId(),
                                s.getSentence(),
                                s.getTranslation()
                        ))
                        .toList()
        );
    }
}
