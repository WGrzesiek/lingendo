package com.learnwords.deckservice.service;

import com.learnwords.common.dto.SendWordFromKafkaDto;
import com.learnwords.common.dto.WordDto;
import com.learnwords.deckservice.dto.flashcard.FlashcardDto;
import com.learnwords.deckservice.entity.Flashcard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Serwis zarządzania fiszkami.
 * 
 * <p>Odpowiada za pełny cykl życia fiszek w systemie:
 * <ul>
 *   <li>Tworzenie fiszek na podstawie zdarzeń z Kafki (Outbox Pattern)</li>
 *   <li>Pobieranie fiszek z talii z pełnymi danymi słówek przez gRPC</li>
 *   <li>Filtrowanie fiszek według statusu nauki (learned, skipped)</li>
 *   <li>Aktualizację słówek przypisanych do fiszek</li>
 *   <li>Resetowanie postępu nauki fiszek</li>
 *   <li>Oznaczanie fiszek jako nauczone</li>
 *   <li>Inicjalizację algorytmów nauki dla nowych fiszek</li>
 * </ul>
 * 
 * <p>Integracje:
 * <ul>
 *   <li>Kafka - nasłuchuje na zdarzenia tworzenia słówek z Vocabulary Service</li>
 *   <li>gRPC - pobiera pełne dane słówek (tłumaczenia, zdania) z Vocabulary Read Service</li>
 *   <li>Database - persystencja fiszek i aktualizacja liczników w taliach</li>
 *   <li>Algorytmy nauki - inicjalizacja i zarządzanie stanem algorytmów (np. Grzesiek Algorithm)</li>
 * </ul>
 * 
 * <p>Fiszka łączy talię z słówkiem i przechowuje:
 * <ul>
 *   <li>Postęp nauki (correctAnswers, totalAttempts)</li>
 *   <li>Status (learned, skipped)</li>
 *   <li>Stan algorytmu nauki (serializowany JSON)</li>
 *   <li>Powiązanie z talią i słówkiem (deckId, wordId)</li>
 * </ul>
 * 
 * @author Grzegorz Wawrzeń
 * @version 2.0
 * @since 2025-11-24
 * @see Flashcard
 * @see FlashcardDto
 * @see com.learnwords.deckservice.service.impl.FlashcardServiceImpl
 */
public interface FlashcardService {

    void addFlashcardToDeck(String deckId, String wordId, String userId);
    void removeFlashcardFromDeck(String deckId, String flashcardId, String userId);
    void processFlashcardCreateFromKafka(SendWordFromKafkaDto sendWordFromKafkaDto);
    void updateFlashcardContent(String flashcardId, WordDto newWord, String userId);
    FlashcardDto getFlashcardById(String flashcardId, String userId);
    Page<FlashcardDto> getFlashcardsFromDeckPaged(String deckId, String userId, int page, int size);
    List<FlashcardDto> getAllFlashcardsFromDeck(String deckId, String userId);
    List<FlashcardDto> getFlashcardsByIds(List<String> flashcardIds);
}

//    public void addFlashcardToDeck(String deckId, String wordId, String userId);
//    public void removeFlashcardFromDeck(String deckId, String flashcardId, String userId);
//    public void processFlashcardCreateFromKafka(SendWordFromKafkaDto sendWordFromKafkaDto);
//    public List<FlashcardDto> getAllFlashcardsFromDeck(String deckId, String userId);
//    public List<FlashcardDto> getFlashcardsFromDeckByFilter(String deckId, boolean isLearned, boolean isSkipped, String userId);


