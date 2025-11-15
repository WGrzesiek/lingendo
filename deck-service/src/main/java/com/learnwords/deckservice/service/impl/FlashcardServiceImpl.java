package com.learnwords.deckservice.service.impl;

import com.learnwords.common.KafkaGroup;
import com.learnwords.common.KafkaTopic;
import com.learnwords.common.dto.SendWordFromKafkaDto;
import com.learnwords.common.dto.SentenceDto;
import com.learnwords.common.dto.WordDto;
import com.learnwords.deckservice.dto.FlashcardDto;
import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.entity.Flashcard;
import com.learnwords.deckservice.enums.LearnAlgorithm;
import com.learnwords.deckservice.exception.exceptions.DeckNotFoundException;
import com.learnwords.deckservice.exception.exceptions.FlashcardNotFoundException;
import com.learnwords.deckservice.exception.exceptions.InvalidFlashcardIdException;
import com.learnwords.deckservice.exception.exceptions.InvalidWordDataException;
import com.learnwords.deckservice.exception.exceptions.UserPermissionsMissing;
import com.learnwords.deckservice.repository.DeckRepository;
import com.learnwords.deckservice.repository.FlashcardRepository;
import com.learnwords.deckservice.service.Algorithm.GrzesiekAlgorithm;
import com.learnwords.deckservice.service.FlashcardService;
import com.learnwords.deckservice.service.GrpcClient.VocabularyGrpcClient;
import com.learnwords.vocabulary.v1.Word;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Implementacja serwisu zarządzania fiszkami.
 * 
 * <p>Klasa odpowiedzialna za:
 * <ul>
 *   <li>Tworzenie fiszek na podstawie zdarzeń z Kafki (Outbox Pattern)</li>
 *   <li>Pobieranie fiszek z talii z pełnymi danymi słówek (przez gRPC)</li>
 *   <li>Filtrowanie fiszek według statusu nauki</li>
 *   <li>Aktualizację stanu fiszek (progress, learned, skipped)</li>
 *   <li>Inicjalizację algorytmów nauki dla nowych fiszek</li>
 * </ul>
 * 
 * <p>Integracje:
 * <ul>
 *   <li>Kafka - nasłuchuje na zdarzenia tworzenia słówek</li>
 *   <li>gRPC - pobiera pełne dane słówek z Vocabulary Read Service</li>
 *   <li>Database - persystencja fiszek i aktualizacja talii</li>
 * </ul>
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-12
 * @see FlashcardService
 * @see VocabularyGrpcClient
 */
@Slf4j
@Service
public class FlashcardServiceImpl implements FlashcardService {

    private final FlashcardRepository flashcardRepository;
    private final DeckRepository deckRepository;
    private final GrzesiekAlgorithm grzesiekAlgorithm;
    private final VocabularyGrpcClient vocabularyGrpcClient;

    /**
     * Konstruktor z dependency injection.
     * 
     * @param flashcardRepository repozytorium fiszek
     * @param deckRepository repozytorium talii
     * @param grzesiekAlgorithm algorytm nauki Grzegorza
     * @param vocabularyGrpcClient klient gRPC do komunikacji z Vocabulary Service
     */
    public FlashcardServiceImpl(FlashcardRepository flashcardRepository, DeckRepository deckRepository, GrzesiekAlgorithm grzesiekAlgorithm, VocabularyGrpcClient vocabularyGrpcClient) {
        this.flashcardRepository = flashcardRepository;
        this.deckRepository = deckRepository;
        this.grzesiekAlgorithm = grzesiekAlgorithm;
        this.vocabularyGrpcClient = vocabularyGrpcClient;
    }

    /**
     * Przetwarza zdarzenie utworzenia słówka z Kafki i tworzy fiszkę.
     * 
     * <p>Metoda jest Kafka Listenerem nasłuchującym na topik tworzenia słówek.
     * Gdy użytkownik utworzy nowe słówko w Vocabulary Service, zdarzenie jest
     * publikowane przez Outbox Pattern i przechwytywane tutaj.
     * 
     * <p>Proces tworzenia fiszki:
     * <ol>
     *   <li>Generuje UUID dla nowej fiszki</li>
     *   <li>Pobiera talię z bazy danych</li>
     *   <li>Tworzy fiszkę z wordId ze zdarzenia</li>
     *   <li>Inicjalizuje stan algorytmu nauki</li>
     *   <li>Zapisuje fiszkę do bazy</li>
     *   <li>Inkrementuje licznik słówek w talii</li>
     * </ol>
     * 
     * <p>Transakcja zapewnia atomowość - albo wszystko się powiedzie,
     * albo rollback w przypadku błędu.
     * 
     * @param getWordFromKafkaDto DTO ze zdarzenia Kafka zawierające ID słówka i talii
     * @param userId ID użytkownika, który utworzył słówko
     * @throws DeckNotFoundException jeśli talia o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma uprawnień do talii
     */
    @Override
    @Transactional
    @KafkaListener(topics = KafkaTopic.CREATE_VOCABULARY_FOR_DECK_TOPIC, groupId = KafkaGroup.DECK_SERVICE_GROUP, properties = {
            "spring.json.value.default.type=com.learnwords.common.dto.SendWordFromKafkaDto"
    })
    public void processFlashcardCreateFromKafka(SendWordFromKafkaDto getWordFromKafkaDto, String userId) {
        log.info("Otrzymano zdarzenie utworzenia słówka - wordId: '{}', deckId: '{}', userId: '{}'",
                getWordFromKafkaDto.id(), getWordFromKafkaDto.deckId(), userId);

        Deck deck = getDeckIfUserHasPermissions(getWordFromKafkaDto.deckId(), userId);
        String flashcardId = UUID.randomUUID().toString();

        Flashcard flashcard = Flashcard.builder()
                .id(flashcardId)
                .wordId(getWordFromKafkaDto.id())
                .deck(deck)
                .build();
        setInitialFlashcardState(getWordFromKafkaDto.deckId(), flashcard, userId);
        flashcardRepository.save(flashcard);
        log.info("Utworzono fiszkę - flashcardId: '{}', wordId: '{}', deckId: '{}'", 
                flashcardId, getWordFromKafkaDto.id(), getWordFromKafkaDto.deckId());

        deck.setWordCount(deck.getWordCount() + 1);
        deckRepository.save(deck);
        log.info("Zaktualizowano licznik słówek w talii - deckId: '{}', wordCount: {}", 
                getWordFromKafkaDto.deckId(), deck.getWordCount());
    }

    /**
     * Inicjalizuje początkowy stan algorytmu nauki dla nowej fiszki.
     * 
     * <p>Pobiera algorytm nauki przypisany do talii i wywołuje jego metodę
     * inicjalizującą. Zserializowany stan jest zapisywany w fiszce.
     * 
     * <p>Obecnie obsługiwane algorytmy:
     * <ul>
     *   <li>GRZESIEK_ALGORITHM - autorski algorytm interwałowego powtarzania</li>
     * </ul>
     * 
     * @param deckId ID talii, z której pobierany jest algorytm
     * @param flashcard fiszka do inicjalizacji (stan zostanie ustawiony)
     * @param userId ID użytkownika wykonującego operację
     * @throws DeckNotFoundException jeśli talia o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma uprawnień do talii
     */
    @Override
    public void setInitialFlashcardState(String deckId, Flashcard flashcard, String userId) {
        log.debug("Inicjalizacja stanu fiszki - flashcardId: '{}', deckId: '{}', userId: '{}'", 
                flashcard.getId(), deckId, userId);
        
        checkDeckIsExistsAndUserHasPermissions(deckId, userId);
        LearnAlgorithm algorithm = getDeckAlgorithm(deckId);

        switch (algorithm) {
            case GRZESIEK_ALGORITHM -> flashcard.setAlgorithmState(grzesiekAlgorithm.initialize().serialize());
        }
        flashcardRepository.save(flashcard);
        log.debug("Ustawiono początkowy stan algorytmu nauki - flashcardId: '{}', algorithm: '{}'", 
                flashcard.getId(), algorithm);
    }


    /**
     * Pobiera wszystkie fiszki z talii wraz z pełnymi danymi słówek.
     * 
     * <p>Metoda wykonuje następujące kroki:
     * <ol>
     *   <li>Sprawdza czy talia istnieje</li>
     *   <li>Pobiera listę fiszek z bazy danych</li>
     *   <li>Wyciąga ID słówek z fiszek</li>
     *   <li>Wywołuje gRPC batch request po pełne dane słówek</li>
     *   <li>Mapuje fiszki + słówka na FlashcardDto</li>
     * </ol>
     * 
     * <p>Używa batch gRPC call dla wydajności - jedno wywołanie dla wszystkich słówek.
     * 
     * @param deckId ID talii, z której pobierane są fiszki
     * @param userId ID użytkownika wykonującego operację
     * @return lista fiszek z pełnymi danymi słówek (słowo, tłumaczenia, zdania)
     * @throws DeckNotFoundException jeśli talia o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma uprawnień do talii
     */
    @Override
    public List<FlashcardDto> getAllFlashcardsFromDeck(String deckId, String userId) {
        log.debug("Pobieranie wszystkich fiszek - deckId: '{}', userId: '{}'", deckId, userId);
        
            checkDeckIsExistsAndUserHasPermissions(deckId, userId);
            
            List<Flashcard> flashcards = flashcardRepository.findByDeckId(deckId);
            if (flashcards.isEmpty()) {
                log.debug("Brak fiszek w talii - deckId: '{}'", deckId);
                return Collections.emptyList();
            }
            
            log.debug("Znaleziono fiszki w talii - deckId: '{}', count: {}", deckId, flashcards.size());
            return mapFlashcardsToDto(flashcards);
    }

    /**
     * Pobiera fiszki z talii według filtrów (learned, skipped).
     * 
     * <p>Umożliwia filtrowanie fiszek według statusu nauki:
     * <ul>
     *   <li>isLearned = true - tylko nauczone fiszki</li>
     *   <li>isSkipped = true - tylko pominięte fiszki</li>
     *   <li>Kombinacje obu flag</li>
     * </ul>
     * 
     * <p>Podobnie jak {@link #getAllFlashcardsFromDeck}, pobiera pełne dane słówek przez gRPC.
     * 
     * @param deckId ID talii
     * @param isLearned flaga filtrująca nauczone fiszki
     * @param isSkipped flaga filtrująca pominięte fiszki
     * @param userId ID użytkownika wykonującego operację
     * @return lista fiszek spełniających kryteria wraz z pełnymi danymi słówek
     * @throws DeckNotFoundException jeśli talia o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma uprawnień do talii
     */
    @Override
    public List<FlashcardDto> getFlashcardsFromDeckByFilter(String deckId, boolean isLearned, boolean isSkipped, String userId) {
        log.debug("Pobieranie fiszek z filtrami - deckId: '{}', userId: '{}', learned: {}, skipped: {}", 
                deckId, userId, isLearned, isSkipped);

            checkDeckIsExistsAndUserHasPermissions(deckId, userId);
            List<Flashcard> flashcards = flashcardRepository.findByFilters(deckId, isLearned, isSkipped);

            if (flashcards.isEmpty()) {
                log.debug("Brak fiszek spełniających kryteria - deckId: '{}', learned: {}, skipped: {}", 
                        deckId, isLearned, isSkipped);
                return Collections.emptyList();
            }

            log.debug("Znaleziono fiszki spełniające kryteria - deckId: '{}', count: {}", deckId, flashcards.size());
            return mapFlashcardsToDto(flashcards);
    }

    /**
     * Aktualizuje słówko przypisane do fiszki.
     * 
     * <p>Zmienia wordId w fiszce na nowy. Używane gdy użytkownik
     * chce zmienić słówko w istniejącej fiszce.
     * 
     * @param flashcardId ID fiszki do aktualizacji
     * @param newWord nowe słówko (używane jest tylko ID)
     * @param userId ID użytkownika wykonującego operację
     * @throws InvalidFlashcardIdException jeśli flashcardId jest null lub pusty
     * @throws InvalidWordDataException jeśli newWord lub jego ID jest null
     * @throws FlashcardNotFoundException jeśli fiszka o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma uprawnień do fiszki
     */
    @Override
    @Transactional
    public void updateFlashcard(String flashcardId, WordDto newWord, String userId) {
        log.debug("Aktualizacja słówka w fiszce - flashcardId: '{}', userId: '{}'", flashcardId, userId);
        
        if (newWord == null || newWord.id() == null) {
            log.error("Próba aktualizacji fiszki z pustymi danymi słówka - flashcardId: '{}'", flashcardId);
            throw new InvalidWordDataException();
        }

        Flashcard flashcard = getFlashcardIfUserHasPermissions(flashcardId, userId);
        String oldWordId = flashcard.getWordId();
        flashcard.setWordId(newWord.id());
        flashcardRepository.save(flashcard);
        log.info("Zaktualizowano słówko w fiszce - flashcardId: '{}', oldWordId: '{}', newWordId: '{}'", 
                flashcardId, oldWordId, newWord.id());
    }

    /**
     * Resetuje postęp nauki fiszki do stanu początkowego.
     * 
     * <p>Ustawia:
     * <ul>
     *   <li>correctAnswers = 0</li>
     *   <li>totalAttempts = 0</li>
     *   <li>learned = false</li>
     *   <li>skipped = false</li>
     * </ul>
     * 
     * <p>Używane gdy użytkownik chce zacząć naukę fiszki od nowa.
     * 
     * @param flashcardId ID fiszki do zresetowania
     * @param userId ID użytkownika wykonującego operację
     * @throws FlashcardNotFoundException jeśli fiszka o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma uprawnień do fiszki
     */
    @Override
    @Transactional
    public void resetFlashcardProgress(String flashcardId, String userId) {
        log.debug("Resetowanie postępu fiszki - flashcardId: '{}', userId: '{}'", flashcardId, userId);

        Flashcard flashcard = getFlashcardIfUserHasPermissions(flashcardId, userId);
        flashcard.setCorrectAnswers(0);
        flashcard.setTotalAttempts(0);
        flashcard.setLearned(false);
        flashcard.setSkipped(false);
        flashcardRepository.save(flashcard);
        log.info("Zresetowano postęp fiszki - flashcardId: '{}'", flashcardId);
    }

    /**
     * Oznacza fiszkę jako nauczoną lub nienauczoną.
     * 
     * <p>Zmienia flagę 'learned' fiszki. Używane gdy użytkownik
     * ręcznie oznaczy fiszkę jako opanowaną lub chce ją cofnąć do nauki.
     * 
     * @param flashcardId ID fiszki
     * @param learned nowy status nauki (true = nauczona, false = do nauki)
     * @param userId ID użytkownika wykonującego operację
     * @throws FlashcardNotFoundException jeśli fiszka o podanym ID nie istnieje
     * @throws UserPermissionsMissing jeśli użytkownik nie ma uprawnień do fiszki
     */
    @Override
    @Transactional
    public void markAsLearned(String flashcardId, boolean learned, String userId) {
        log.debug("Oznaczanie fiszki jako nauczona - flashcardId: '{}', userId: '{}', learned: {}", 
                flashcardId, userId, learned);
        
        Flashcard flashcard = getFlashcardIfUserHasPermissions(flashcardId, userId);
        flashcard.setLearned(learned);
        flashcardRepository.save(flashcard);
        log.info("Zaktualizowano status nauki fiszki - flashcardId: '{}', learned: {}", flashcardId, learned);
    }

    /**
     * Mapuje listę fiszek wraz z danymi słówek na FlashcardDto.
     * 
     * <p>Wspólna metoda pomocnicza używana przez {@link #getAllFlashcardsFromDeck}
     * i {@link #getFlashcardsFromDeckByFilter} aby uniknąć duplikacji kodu.
     * 
     * <p>Proces:
     * <ol>
     *   <li>Wyciąga wordIds z fiszek</li>
     *   <li>Wywołuje batch gRPC request po wszystkie słówka naraz</li>
     *   <li>Dla każdej fiszki znajduje odpowiednie słówko z response</li>
     *   <li>Mapuje Proto Word na WordDto</li>
     *   <li>Tworzy FlashcardDto z fiszkę + słówko</li>
     * </ol>
     * 
     * @param flashcards lista fiszek do zmapowania
     * @return lista FlashcardDto z pełnymi danymi słówek
     * @throws RuntimeException jeśli wystąpi błąd gRPC lub słówko nie zostanie znalezione
     */
    private List<FlashcardDto> mapFlashcardsToDto(List<Flashcard> flashcards) {
        List<String> wordIds = flashcards.stream()
                .map(Flashcard::getWordId)
                .toList();
        
        log.debug("Pobieranie słówek przez gRPC - wordCount: {}", wordIds.size());
        var wordsResponse = vocabularyGrpcClient.batchGetWordsByIds(wordIds);
        log.debug("Otrzymano słówka z gRPC - responseCount: {}", wordsResponse.getWordsCount());
        
        return flashcards.stream()
                .map(flashcard -> {
                    var wordProto = wordsResponse.getWordsList().stream()
                            .filter(w -> w.getId().equals(flashcard.getWordId()))
                            .findFirst()
                            .orElseThrow(() -> {
                                log.error("Nie znaleziono słówka w odpowiedzi gRPC - wordId: '{}'", flashcard.getWordId());
                                return new RuntimeException("Nie znaleziono słówka: " + flashcard.getWordId());
                            });
                    
                    WordDto wordDto = mapProtoToWordDto(wordProto);
                    
                    return new FlashcardDto(
                            flashcard.getId(),
                            wordDto,
                            flashcard.getCorrectAnswers(),
                            flashcard.getTotalAttempts(),
                            flashcard.isLearned(),
                            flashcard.isSkipped(),
                            flashcard.getCreatedAt(),
                            flashcard.getUpdatedAt()
                    );
                })
                .toList();
    }

    /**
     * Mapuje Proto Word (z gRPC) na domenowy WordDto.
     * 
     * <p>Konwertuje struktury Protocol Buffers otrzymane z gRPC
     * na zwykłe Java DTOs używane w aplikacji.
     * 
     * @param wordProto Proto Word z gRPC response
     * @return WordDto z pełnymi danymi (słowo, tłumaczenia, zdania)
     */
    private WordDto mapProtoToWordDto(Word wordProto) {
        return new WordDto(
                wordProto.getId(),
                wordProto.getWord(),
                wordProto.getTranslationsList(),
                wordProto.getSentencesList().stream()
                        .map(s -> new SentenceDto(
                                s.getId(),
                                s.getSentence(),
                                s.getTranslation()
                        ))
                        .toList(),
                wordProto.getSentencesAiList().stream()
                        .map(s -> new SentenceDto(
                                s.getId(),
                                s.getSentence(),
                                s.getTranslation()
                        ))
                        .toList()
        );
    }
    /**
     * Pobiera talię jeśli użytkownik ma do niej uprawnienia.
     *
     * @param deckId ID talii
     * @param userId ID użytkownika
     * @return talia jeśli użytkownik ma uprawnienia
     * @throws IllegalArgumentException gdy userId lub deckId są null lub puste
     * @throws DeckNotFoundException gdy talia o podanym ID nie istnieje
     * @throws UserPermissionsMissing gdy użytkownik nie ma uprawnień do talii
     */
    private Deck getDeckIfUserHasPermissions(String deckId, String userId) {
        if (userId == null || userId.isBlank() || deckId == null || deckId.isBlank()) {
            log.error("Próba dostępu do talii z pustym parametrem - userId: '{}', deckId: '{}'", userId, deckId);
            throw new IllegalArgumentException("UserId lub DeckId nie może być pusty");
        }
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> {
                    log.error("Nie znaleziono talii - deckId: '{}'", deckId);
                    return new DeckNotFoundException(deckId);
                });

        if (!deck.getUserId().equals(userId)) {
            log.warn("Brak uprawnień do talii - userId: '{}', deckId: '{}', deckOwnerId: '{}'", 
                    userId, deckId, deck.getUserId());
            throw new UserPermissionsMissing("Użytkownik nie ma uprawnień do tej talii");
        }
        return deck;
    }

    /**
     * Sprawdza czy talia istnieje i czy użytkownik ma do niej uprawnienia.
     *
     * @param deckId ID talii
     * @param userId ID użytkownika
     * @throws IllegalArgumentException gdy userId lub deckId są null lub puste
     * @throws DeckNotFoundException gdy talia o podanym ID nie istnieje
     * @throws UserPermissionsMissing gdy użytkownik nie ma uprawnień do talii
     */
    private void checkDeckIsExistsAndUserHasPermissions(String deckId, String userId) {
        if (userId == null || userId.isBlank() || deckId == null || deckId.isBlank()) {
            log.error("Próba sprawdzenia uprawnień z pustym parametrem - userId: '{}', deckId: '{}'", userId, deckId);
            throw new IllegalArgumentException("UserId lub DeckId nie może być pusty");
        }
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> {
                    log.error("Nie znaleziono talii - deckId: '{}'", deckId);
                    return new DeckNotFoundException(deckId);
                });

        if (!deck.getUserId().equals(userId)) {
            log.warn("Brak uprawnień do talii - userId: '{}', deckId: '{}', deckOwnerId: '{}'", 
                    userId, deckId, deck.getUserId());
            throw new UserPermissionsMissing("Użytkownik nie ma uprawnień do tej talii");
        }
    }

    /**
     * Pobiera algorytm nauki przypisany do talii.
     *
     * @param deckId ID talii
     * @return algorytm nauki talii
     * @throws DeckNotFoundException gdy talia o podanym ID nie istnieje
     */
    private LearnAlgorithm getDeckAlgorithm(String deckId) {
        return deckRepository.findById(deckId)
                .orElseThrow(() -> {
                    log.error("Nie znaleziono talii podczas pobierania algorytmu - deckId: '{}'", deckId);
                    return new DeckNotFoundException(deckId);
                })
                .getLearnAlgorithm();
    }

    /**
     * Pobiera fiszkę jeśli użytkownik ma do niej uprawnienia.
     *
     * @param flashcardId ID fiszki
     * @param userId ID użytkownika
     * @return fiszka jeśli użytkownik ma uprawnienia
     * @throws InvalidFlashcardIdException gdy flashcardId jest null lub pusty
     * @throws IllegalArgumentException gdy userId jest null lub pusty
     * @throws FlashcardNotFoundException gdy fiszka o podanym ID nie istnieje
     * @throws UserPermissionsMissing gdy użytkownik nie ma uprawnień do fiszki
     */
    private Flashcard getFlashcardIfUserHasPermissions(String flashcardId, String userId) {
        if (flashcardId == null || flashcardId.isBlank()) {
            log.error("Próba dostępu do fiszki z pustym flashcardId");
            throw new InvalidFlashcardIdException();
        }
        if (userId == null || userId.isBlank()) {
            log.error("Próba dostępu do fiszki z pustym userId - flashcardId: '{}'", flashcardId);
            throw new IllegalArgumentException("UserId nie może być pusty");
        }
        Flashcard flashcard = flashcardRepository.findById(flashcardId)
                .orElseThrow(() -> {
                    log.error("Nie znaleziono fiszki - flashcardId: '{}'", flashcardId);
                    return new FlashcardNotFoundException(flashcardId);
                });
        if (!flashcard.getDeck().getUserId().equals(userId)) {
            log.warn("Brak uprawnień do fiszki - userId: '{}', flashcardId: '{}', deckOwnerId: '{}'", 
                    userId, flashcardId, flashcard.getDeck().getUserId());
            throw new UserPermissionsMissing("Użytkownik nie ma uprawnień do tej fiszki");
        }
        return flashcard;
    }
}
