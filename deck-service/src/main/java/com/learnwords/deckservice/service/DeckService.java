package com.learnwords.deckservice.service;

import com.learnwords.deckservice.dto.*;
import com.learnwords.deckservice.enums.DeckOwner;
import com.learnwords.deckservice.enums.LearnAlgorithm;

import java.util.List;

/**
 * Serwis zarządzania taliami fiszek.
 * 
 * <p>Odpowiada za pełny cykl życia talii w systemie:
 * <ul>
 *   <li>Tworzenie i usuwanie talii</li>
 *   <li>Zarządzanie metadanymi talii (nazwa, opis, widoczność, właściciel)</li>
 *   <li>Pobieranie talii według różnych filtrów (użytkownik, widoczność, właściciel)</li>
 *   <li>Pobieranie szczegółowych informacji o talii</li>
 *   <li>Konfigurację algorytmów nauki i ustawień sesji</li>
 *   <li>Statystyki talii (liczba fiszek, postęp nauki)</li>
 *   <li>Sprawdzanie unikalności nazw talii dla użytkownika</li>
 * </ul>
 * 
 * <p>Talia może mieć różne statusy i konfiguracje:
 * <ul>
 *   <li>Widoczność - publiczna (dostępna dla wszystkich) lub prywatna</li>
 *   <li>Właściciel - USER (tworzone przez użytkownika) lub SYSTEM (talię systemowe)</li>
 *   <li>Algorytm nauki - strategia nauki fiszek (np. GRZESIEK_ALGORITHM)</li>
 *   <li>Liczba fiszek na sesję - konfiguracja długości sesji nauki</li>
 * </ul>
 * 
 * <p>Statystyki talii:
 * <ul>
 *   <li>Całkowita liczba fiszek</li>
 *   <li>Liczba nauczonych fiszek</li>
 *   <li>Liczba pominiętych fiszek</li>
 *   <li>Postęp nauki (procent opanowanych fiszek)</li>
 *   <li>Liczba talii użytkownika</li>
 * </ul>
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-15
 * @see com.learnwords.deckservice.entity.Deck
 * @see DeckDto
 * @see DeckDetailsDto
 * @see DeckStatisticsDto
 * @see com.learnwords.deckservice.service.impl.DeckServiceImpl
 */
public interface DeckService {
    void createDeck(String userId, CreateDeckDto createDeckDto);
    void deleteDeck(String deckId, String userId);
    String renameDeck(String deckId, String newName, String userId);
    boolean changeDeckVisibility(String deckId, String userId, boolean isPublic);
    DeckOwner changeDeckOwner(String deckId, String userId, DeckOwner newOwner);
    DeckDto getDeckById(String deckId, String userId);
    List<DeckDto> getDecksByFilter(String userId, Boolean isPublic, DeckOwner owner);
    default List<DeckDto> getDecksByFilter(String userId, DeckOwner owner) {
        return getDecksByFilter(userId, null, owner);
    }
    default List<DeckDto> getDecksByFilter(String userId) {
        return getDecksByFilter(userId, null, null);
    }
    List<DeckDto> getPublicDecks();
    DeckDetailsDto getDeckDetailsById(String deckId, String userId);
    DeckDetailsDto editDeckDetails(String deckId, DeckDetailsDto deckDetailsDto, String userId);
    long getTotalFlashcardsCount(String deckId, String userId);
    String updateLearnAlgorithm(String deckId, LearnAlgorithm algorithm, String userId);
    Long updateFlashcardsPerSession(String deckId, Long count, String userId);
    UserDeckCountDto getUserDeckCount(String userId);
    DeckStatisticsDto getDeckStatistics(String deckId, String userId);
    boolean isDeckNameTaken(String userId, String deckName);
}
