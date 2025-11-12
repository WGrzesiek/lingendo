package com.learnwords.deckservice.service.impl;

import com.learnwords.common.KafkaGroup;
import com.learnwords.common.KafkaTopic;
import com.learnwords.common.dto.SentenceDto;
import com.learnwords.common.dto.WordDto;
import com.learnwords.deckservice.dto.FlashcardDto;
import com.learnwords.deckservice.dto.GetWordFromKafkaDto;
import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.entity.Flashcard;
import com.learnwords.deckservice.enums.LearnAlgorithm;
import com.learnwords.deckservice.repository.DeckRepository;
import com.learnwords.deckservice.repository.FlashcardRepository;
import com.learnwords.deckservice.service.Algorithm.GrzesiekAlgorithm;
import com.learnwords.deckservice.service.FlashcardService;
import com.learnwords.deckservice.service.GrpcClient.VocabularyGrpcClient;
import com.learnwords.vocabulary.v1.Word;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
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
     * @throws RuntimeException jeśli talia nie istnieje lub wystąpi błąd bazy danych
     */
    @Override
    @Transactional
    @KafkaListener(topics = KafkaTopic.CREATE_VOCABULARY_TOPIC, groupId = KafkaGroup.DECK_SERVICE_GROUP, properties = {
            "spring.json.value.default.type=com.learnwords.common.dto.WordDto"
    })
    public void processFlashcardCreateFromKafka(GetWordFromKafkaDto getWordFromKafkaDto) {
        log.info("Otrzymano event: {}", KafkaTopic.CREATE_VOCABULARY_TOPIC);
        String flashcardId = UUID.randomUUID().toString();
        Flashcard flashcard = new Flashcard();

        try {
            Deck deck = deckRepository.findById(getWordFromKafkaDto.deckId())
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono talii o id: " + getWordFromKafkaDto.deckId()));

            flashcard.setId(flashcardId);
            flashcard.setWordId(getWordFromKafkaDto.id());
            flashcard.setDeck(deck);
            setInitialFlashcardState(getWordFromKafkaDto.deckId(), flashcard);
            flashcardRepository.save(flashcard);
            log.info("Zapisano fiszke o id: {}", flashcardId);
            deck.setWordCount(deck.getWordCount() + 1);
            deckRepository.save(deck);
            log.info("Zaktualizowano talię o id: {}", getWordFromKafkaDto.deckId());
        }
        catch (DataAccessException e) {
            log.error("Błąd dostępu do bazy danych: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd dostępu do bazy danych: " + e.getMessage(), e);
        }
        catch (Exception e) {
            log.error("Błąd podczas tworzenia fiszki: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd podczas tworzenia fiszki: " + e.getMessage(), e);
        }
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
     * @throws RuntimeException jeśli talia nie istnieje lub wystąpi błąd bazy danych
     */
    @Override
    public void setInitialFlashcardState(String deckId, Flashcard flashcard) {
        try{
            LearnAlgorithm algorithm = deckRepository.findById(deckId)
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono talii o id: " + deckId))
                    .getLearnAlgorithm();
            switch (algorithm) {
                case GRZESIEK_ALGORITHM -> flashcard.setAlgorithmState(grzesiekAlgorithm.initialize().serialize());
            }
            log.info("Ustawianie początkowego stanu fiszki dla talii: {}", deckId);
        }
        catch (DataAccessException e) {
            log.error("Błąd dostępu do bazy danych: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd dostępu do bazy danych: " + e.getMessage(), e);
        }
        catch (Exception e) {
            log.error("Błąd podczas ustawiania początkowego stanu fiszki: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd podczas ustawiania początkowego stanu fiszki: " + e.getMessage(), e);
        }
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
     * @return lista fiszek z pełnymi danymi słówek (słowo, tłumaczenia, zdania)
     * @throws RuntimeException jeśli talia nie istnieje lub wystąpi błąd gRPC/DB
     */
    @Override
    public List<FlashcardDto> getAllFlashcardsFromDeck(String deckId) {
        log.debug("Pobieranie wszystkich fiszek z talii: {}", deckId);
        
        try {
            if (deckId == null || deckId.isBlank()) {
                log.error("DeckId jest null lub pusty");
                throw new IllegalArgumentException("DeckId nie może być pusty");
            }
            
            if (!deckRepository.existsById(deckId)) {
                log.error("Nie znaleziono talii o id: {}", deckId);
                throw new RuntimeException("Nie znaleziono talii o id: " + deckId);
            }
            
            List<Flashcard> flashcards = flashcardRepository.findByDeckId(deckId);
            log.debug("Znaleziono {} fiszek w talii {}", flashcards.size(), deckId);
            
            if (flashcards.isEmpty()) {
                log.debug("Talia {} nie zawiera żadnych fiszek", deckId);
                return Collections.emptyList();
            }
            
            return mapFlashcardsToDto(flashcards);
            
        } catch (DataAccessException e) {
            log.error("Błąd dostępu do bazy danych: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd dostępu do bazy danych: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Błąd podczas pobierania fiszek z talii: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd podczas pobierania fiszek z talii: " + e.getMessage(), e);
        }
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
     * @return lista fiszek spełniających kryteria wraz z pełnymi danymi słówek
     * @throws RuntimeException jeśli talia nie istnieje lub wystąpi błąd gRPC/DB
     */
    @Override
    public List<FlashcardDto> getFlashcardsFromDeckByFilter(String deckId, boolean isLearned, boolean isSkipped) {
        log.debug("Pobieranie fiszek z talii {} z filtrami: learned={}, skipped={}", deckId, isLearned, isSkipped);
        
        try {
            if (deckId == null || deckId.isBlank()) {
                log.error("DeckId jest null lub pusty");
                throw new IllegalArgumentException("DeckId nie może być pusty");
            }
            
            if (!deckRepository.existsById(deckId)) {
                log.error("Nie znaleziono talii o id: {}", deckId);
                throw new RuntimeException("Nie znaleziono talii o id: " + deckId);
            }
            
            List<Flashcard> flashcards = flashcardRepository.findByFilters(deckId, isLearned, isSkipped);
            log.debug("Znaleziono {} fiszek w talii {} spełniających kryteria", flashcards.size(), deckId);
            
            if (flashcards.isEmpty()) {
                log.debug("Brak fiszek spełniających kryteria w talii {}", deckId);
                return Collections.emptyList();
            }
            
            return mapFlashcardsToDto(flashcards);
            
        } catch (DataAccessException e) {
            log.error("Błąd dostępu do bazy danych: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd dostępu do bazy danych: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Błąd podczas pobierania fiszek z talii: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd podczas pobierania fiszek z talii: " + e.getMessage(), e);
        }
    }

    /**
     * Aktualizuje słówko przypisane do fiszki.
     * 
     * <p>Zmienia wordId w fiszce na nowy. Używane gdy użytkownik
     * chce zmienić słówko w istniejącej fiszce.
     * 
     * @param flashcardId ID fiszki do aktualizacji
     * @param newWord nowe słówko (używane jest tylko ID)
     * @throws RuntimeException jeśli fiszka nie istnieje lub wystąpi błąd DB
     */
    @Override
    @Transactional
    public void updateFlashcard(String flashcardId, WordDto newWord) {
        log.debug("Aktualizacja fiszki {} na nowe słówko {}", flashcardId, newWord.id());
        
        try {
            if (flashcardId == null || flashcardId.isBlank()) {
                log.error("FlashcardId jest null lub pusty");
                throw new IllegalArgumentException("FlashcardId nie może być pusty");
            }
            
            if (newWord == null || newWord.id() == null) {
                log.error("NewWord lub newWord.id() jest null");
                throw new IllegalArgumentException("NewWord nie może być null");
            }
            
            Flashcard flashcard = flashcardRepository.findById(flashcardId)
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono fiszki o id: " + flashcardId));
            flashcard.setWordId(newWord.id());
            flashcardRepository.save(flashcard);
            log.info("Zaktualizowano fiszkę o id: {} na słówko {}", flashcardId, newWord.id());
        } catch (DataAccessException e) {
            log.error("Błąd dostępu do bazy danych: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd dostępu do bazy danych: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Błąd podczas aktualizacji fiszki: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd podczas aktualizacji fiszki: " + e.getMessage(), e);
        }
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
     * @throws RuntimeException jeśli fiszka nie istnieje lub wystąpi błąd DB
     */
    @Override
    @Transactional
    public void resetFlashcardProgress(String flashcardId) {
        log.debug("Resetowanie postępu fiszki: {}", flashcardId);
        
        try {
            if (flashcardId == null || flashcardId.isBlank()) {
                log.error("FlashcardId jest null lub pusty");
                throw new IllegalArgumentException("FlashcardId nie może być pusty");
            }
            
            Flashcard flashcard = flashcardRepository.findById(flashcardId)
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono fiszki o id: " + flashcardId));
            flashcard.setCorrectAnswers(0);
            flashcard.setTotalAttempts(0);
            flashcard.setLearned(false);
            flashcard.setSkipped(false);
            flashcardRepository.save(flashcard);
            log.info("Zresetowano postęp fiszki o id: {}", flashcardId);
        } catch (DataAccessException e) {
            log.error("Błąd dostępu do bazy danych: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd dostępu do bazy danych: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Błąd podczas resetowania postępu fiszki: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd podczas resetowania postępu fiszki: " + e.getMessage(), e);
        }
    }

    /**
     * Oznacza fiszkę jako nauczoną lub nienauczoną.
     * 
     * <p>Zmienia flagę 'learned' fiszki. Używane gdy użytkownik
     * ręcznie oznaczy fiszkę jako opanowaną lub chce ją cofnąć do nauki.
     * 
     * @param flashcardId ID fiszki
     * @param learned nowy status nauki (true = nauczona, false = do nauki)
     * @throws RuntimeException jeśli fiszka nie istnieje lub wystąpi błąd DB
     */
    @Override
    @Transactional
    public void markAsLearned(String flashcardId, boolean learned) {
        log.debug("Oznaczanie fiszki {} jako learned={}", flashcardId, learned);
        
        try {
            if (flashcardId == null || flashcardId.isBlank()) {
                log.error("FlashcardId jest null lub pusty");
                throw new IllegalArgumentException("FlashcardId nie może być pusty");
            }
            
            Flashcard flashcard = flashcardRepository.findById(flashcardId)
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono fiszki o id: " + flashcardId));
            flashcard.setLearned(learned);
            flashcardRepository.save(flashcard);
            log.info("Ustawiono stan nauczonej fiszki o id: {} na {}", flashcardId, learned);
        } catch (DataAccessException e) {
            log.error("Błąd dostępu do bazy danych: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd dostępu do bazy danych: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Błąd podczas oznaczania fiszki jako nauczonej: {}", e.getMessage(), e);
            throw new RuntimeException("Błąd podczas oznaczania fiszki jako nauczonej: " + e.getMessage(), e);
        }
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
        
        log.debug("Pobieranie {} słówek przez gRPC batch request", wordIds.size());
        var wordsResponse = vocabularyGrpcClient.batchGetWordsByIds(wordIds);
        log.debug("Otrzymano {} słówek z gRPC", wordsResponse.getWordsCount());
        
        return flashcards.stream()
                .map(flashcard -> {
                    var wordProto = wordsResponse.getWordsList().stream()
                            .filter(w -> w.getId().equals(flashcard.getWordId()))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Nie znaleziono słówka: " + flashcard.getWordId()));
                    
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
}
