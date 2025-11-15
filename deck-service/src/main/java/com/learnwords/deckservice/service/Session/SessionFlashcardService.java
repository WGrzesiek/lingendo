package com.learnwords.deckservice.service.Session;

import com.learnwords.deckservice.dto.SessionFlashcardDto;
import com.learnwords.deckservice.entity.Deck;
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
    

    String addFlashcardsToSession(Session session, Deck deck, FlashcardFetchStrategy flashcardFetchStrategy);
    List<SessionFlashcardDto> getSessionFlashcards(String sessionId);
    Optional<SessionFlashcardDto> getFlashcardProgress(String sessionId, String flashcardId);
    void skipFlashcard(String sessionId, String flashcardId);
    int getTotalFlashcardsInSession(String sessionId);
    int getAnsweredFlashcardsCount(String sessionId);
}
