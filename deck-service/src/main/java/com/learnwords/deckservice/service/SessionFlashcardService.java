package com.learnwords.deckservice.service;

import com.learnwords.deckservice.dto.sessionFlashcard.SessionFlashcardDto;
import com.learnwords.deckservice.entity.SessionFlashcard;

import java.util.List;

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
 * @version 2.0
 * @since 2025-11-24
 * @see SessionFlashcardDto
 * @see FlashcardFetchStrategy
 */
public interface SessionFlashcardService {

    void populateSessionWithFlashcards(String sessionId, String enrollmentId, FlashcardFetchStrategy flashcardFetchStrategy, String userId);
    List<SessionFlashcard> getSessionFlashcards(String sessionId);
    SessionFlashcardDto getSessionFlashcardsWithWords(String sessionId);
    //TODO wypelnienie wszystkich sesji fiszkai z danego decka
}
