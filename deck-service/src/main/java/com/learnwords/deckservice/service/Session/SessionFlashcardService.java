package com.learnwords.deckservice.service.Session;

import com.learnwords.deckservice.dto.SessionFlashcardDto;
import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.entity.Flashcard;
import com.learnwords.deckservice.entity.Session;
import com.learnwords.deckservice.service.Session.FlashcardFetchStrategy.FlashcardFetchStrategy;

import java.util.List;
import java.util.Optional;

/**
 * Serwis zarządzania fiszkami w kontekście sesji nauki.
 * 
 * <p>Odpowiada za:
 * <ul>
 *   <li>Dodawanie fiszek do sesji według strategii (ALPHABETICAL, RANDOM itp.)</li>
 *   <li>Pobieranie fiszek przypisanych do sesji</li>
 *   <li>Śledzenie postępu fiszek w sesji (czy odpowiedziano, czy poprawnie)</li>
 *   <li>Zarządzanie pominiętymi fiszkami</li>
 * </ul>
 * 
 * <p>Fiszki w sesji mają dodatkowy kontekst:
 * <ul>
 *   <li>Czy już odpowiedziano na fiszkę w tej sesji</li>
 *   <li>Czy odpowiedź była poprawna</li>
 *   <li>Kiedy fiszka została dodana do sesji</li>
 * </ul>
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-12
 * @see SessionFlashcardDto
 * @see FlashcardFetchStrategy
 */
public interface SessionFlashcardService {
    
    /**
     * Dodaje fiszki do sesji według wybranej strategii.
     * 
     * <p>Pobiera fiszki z talii, sortuje je według strategii (ALPHABETICAL, RANDOM, UNLEARNED_FIRST itp.)
     * i dodaje do sesji. Liczba fiszek zależy od ustawienia flashcardsPerSession w talii.
     * 
     * <p>Strategie sortowania:
     * <ul>
     *   <li>ALPHABETICAL - alfabetycznie po słowie</li>
     *   <li>RANDOM - losowa kolejność</li>
     *   <li>REVERSE_ALPHABETICAL - odwrotnie alfabetycznie</li>
     *   <li>UNLEARNED_FIRST - nienauczone najpierw, potem reszta</li>
     * </ul>
     * 
     * @param session sesja, do której dodawane są fiszki
     * @param deck talia, z której pobierane są fiszki
     * @param flashcardFetchStrategy strategia wyboru i sortowania fiszek
     * @return liczba dodanych fiszek do sesji
     * @throws RuntimeException jeśli talia jest pusta lub wystąpi błąd DB
     */
    String addFlashcardsToSession(Session session, Deck deck, FlashcardFetchStrategy flashcardFetchStrategy);
    
    /**
     * Pobiera wszystkie fiszki przypisane do sesji z pełnymi danymi słówek.
     * 
     * <p>Zwraca listę fiszek w sesji wraz z:
     * <ul>
     *   <li>Pełnymi danymi słówka (przez gRPC z Vocabulary Service)</li>
     *   <li>Stanem fiszki (correctAnswers, totalAttempts, isLearned)</li>
     *   <li>Kontekstem sesji (answeredInSession, wasCorrect)</li>
     * </ul>
     * 
     * @param sessionId ID sesji
     * @return lista fiszek w sesji z pełnymi danymi
     * @throws RuntimeException jeśli sesja nie istnieje
     */
    List<SessionFlashcardDto> getSessionFlashcards(String sessionId);
    
    /**
     * Pobiera postęp pojedynczej fiszki w sesji.
     * 
     * <p>Zwraca informacje czy użytkownik już odpowiedział na tę fiszkę
     * w bieżącej sesji i czy odpowiedź była poprawna.
     * 
     * @param sessionId ID sesji
     * @param flashcardId ID fiszki
     * @return DTO z postępem fiszki w sesji
     * @throws RuntimeException jeśli sesja/fiszka nie istnieje lub fiszka nie jest w sesji
     */
    Optional<SessionFlashcardDto> getFlashcardProgress(String sessionId, String flashcardId);
    
    /**
     * Pomija fiszkę w sesji.
     * 
     * <p>Oznacza fiszkę jako pominiętą w bieżącej sesji.
     * Inkrementuje licznik skipped w sesji.
     * 
     * @param sessionId ID sesji
     * @param flashcardId ID fiszki do pominięcia
     * @throws RuntimeException jeśli sesja/fiszka nie istnieje lub sesja nie jest aktywna
     */
    void skipFlashcard(String sessionId, String flashcardId);
    
    /**
     * Pobiera łączną liczbę fiszek w sesji.
     * 
     * @param sessionId ID sesji
     * @return liczba fiszek w sesji
     * @throws RuntimeException jeśli sesja nie istnieje
     */
    int getTotalFlashcardsInSession(String sessionId);
    
    /**
     * Pobiera liczbę fiszek na które już odpowiedziano w sesji.
     * 
     * @param sessionId ID sesji
     * @return liczba fiszek z odpowiedzią
     * @throws RuntimeException jeśli sesja nie istnieje
     */
    int getAnsweredFlashcardsCount(String sessionId);
}
