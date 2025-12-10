package com.learnwords.deckservice.service;

import com.learnwords.deckservice.dto.*;
import com.learnwords.deckservice.dto.deck.CreateDeckDto;
import com.learnwords.deckservice.dto.deck.DeckDetailsDto;
import com.learnwords.deckservice.dto.deck.DeckDto;
import com.learnwords.deckservice.enums.DeckOwner;
import com.learnwords.deckservice.enums.DeckVisibility;

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
 * @version 2.0
 * @since 2025-11-24
 * @see com.learnwords.deckservice.entity.Deck
 * @see DeckDto
 * @see DeckDetailsDto
 * @see com.learnwords.deckservice.service.impl.DeckServiceImpl
 */
public interface DeckService {
    void createDeck(String ownerId, CreateDeckDto createDeckDto);
    void deleteDeck(String deckId, String userId);
    String renameDeck(String deckId, String newName, String userId);
    void changeDeckVisibility(String deckId, String userId, DeckVisibility visibility);
    DeckOwner changeDeckOwner(String deckId, String userId, DeckOwner newOwner);

    DeckDto getDeckById(String deckId, String userId);
    List<DeckDto> getDecksByFilter(String userId, DeckVisibility visibility, DeckOwner owner);
    DeckDetailsDto getDeckDetailsById(String deckId, String userId); // Szczegóły statyczne (opis, ilość słów)
    DeckDetailsDto editDeckDetails(String deckId, DeckDetailsDto deckDetailsDto, String userId);
    long getTotalFlashcardsCount(String deckId, String userId);
    boolean isDeckNameTaken(String userId, String deckName);

}
