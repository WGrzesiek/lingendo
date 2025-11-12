package com.learnwords.deckservice.service.impl;

import com.learnwords.deckservice.dto.*;
import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.enums.DeckOwner;
import com.learnwords.deckservice.enums.LearnAlgorithm;
import com.learnwords.deckservice.enums.SessionStatus;
import com.learnwords.deckservice.exception.exceptions.DeckWithThisNameForThisUserAlreadyExistsException;
import com.learnwords.deckservice.repository.DeckRepository;
import com.learnwords.deckservice.repository.FlashcardRepository;
import com.learnwords.deckservice.repository.SessionRepository;
import com.learnwords.deckservice.service.DeckService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Implementacja serwisu zarządzającego taliami (decks).
 * 
 * <p>Serwis odpowiedzialny za operacje CRUD na taliach oraz zarządzanie ich konfiguracją.
 * Obsługuje tworzenie, usuwanie, edycję i pobieranie talii wraz z ich szczegółami.
 * 
 * <p>Główne funkcjonalności:
 * <ul>
 *   <li>Tworzenie i usuwanie talii</li>
 *   <li>Edycja nazwy, widoczności i właściciela talii</li>
 *   <li>Filtrowanie talii według różnych kryteriów</li>
 *   <li>Zarządzanie ustawieniami nauki (algorytm, liczba fiszek na sesję)</li>
 *   <li>Pobieranie szczegółowych informacji o taliach</li>
 * </ul>
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-10
 * @see DeckService
 * @see Deck
 * @see DeckDto
 * @see DeckDetailsDto
 */
@Slf4j
@Service
public class DeckServiceImpl implements DeckService {
    private final DeckRepository deckRepository;
    private final FlashcardRepository flashcardRepository;
    private final SessionRepository sessionRepository;

    public DeckServiceImpl(
            DeckRepository deckRepository,
            FlashcardRepository flashcardRepository,
            SessionRepository sessionRepository) {
        this.deckRepository = deckRepository;
        this.flashcardRepository = flashcardRepository;
        this.sessionRepository = sessionRepository;
    }

    /**
     * Tworzy nową talię dla użytkownika
     * 
     * @param userId ID użytkownika tworzącego talię
     * @param createDeckDto DTO z danymi nowej talii
     * @return true jeśli talia została utworzona pomyślnie, false w przeciwnym razie
     * @throws DeckWithThisNameForThisUserAlreadyExistsException gdy talia o podanej nazwie już istnieje dla tego użytkownika
     */
    @Override
    @Transactional
    public boolean createDeck(String userId, CreateDeckDto createDeckDto) throws DeckWithThisNameForThisUserAlreadyExistsException {
        log.info("Tworzenie tali: {}", createDeckDto.getDeckName());
        String deckId = UUID.randomUUID().toString();
        try {
            if(deckRepository.existsByNameAndUserId(createDeckDto.getDeckName(), userId)) {
                log.error("Talia o nazwie '{}' już istnieje dla tego usera", createDeckDto.getDeckName());
                throw new DeckWithThisNameForThisUserAlreadyExistsException("Talia o tej nazwie już istnieje dla tego użytkownika");
            }
            deckRepository.save(
                    Deck.builder()
                            .id(deckId)
                            .name(createDeckDto.getDeckName())
                            .description(createDeckDto.getDescription())
                            .userId(userId)
                            .howManyFlashcardsForOneSession(createDeckDto.getHowManyFlashcardsForOneSession())
                            .isPublic(createDeckDto.getIsPublic())
                            .wordCount(0)
                            .learnAlgorithm(createDeckDto.getLearnAlgorithm())
                            .languageFrom(createDeckDto.getLanguageFrom())
                            .languageTo(createDeckDto.getLanguageTo())
                            .build()
            );
            log.info("Talia '{}' została pomyślnie utworzona przez użytkownika {}", createDeckDto.getDeckName(), userId);
            return true;
        }
        catch (Exception e) {
            log.error("Błąd podczas sprawdzania istnienia talii: {}", e.getMessage());
            throw new RuntimeException("Błąd podczas sprawdzania istnienia talii: {}", e);
        }
    }
    /**
     * Usuwa talię na podstawie podanego ID
     * 
     * @param deckId ID talii do usunięcia
     * @return true jeśli talia została usunięta pomyślnie, false jeśli talia nie istnieje
     */
    @Override
    @Transactional
    public boolean deleteDeck(String deckId) {
        try{
            if(!deckRepository.existsById(deckId)){
                log.error("Talia o ID '{}' nie istnieje", deckId);
                return false;
            }
            deckRepository.deleteById(deckId);
            log.info("Talia o ID '{}' została pomyślnie usunięta", deckId);
            return true;
        }
        catch (DataAccessException e){
            log.error("Błąd dostępu do danych podczas usuwania talii: {}", e.getMessage());
            throw new RuntimeException("Błąd dostępu do danych podczas usuwania talii: {}", e);
        }
        catch (Exception e){
            log.error("Błąd podczas usuwania talii: {}", e.getMessage());
            throw new RuntimeException("Błąd podczas usuwania talii: {}", e);
        }
    }

    /**
     * Zmienia nazwę talii
     * 
     * @param deckId ID talii do przemianowania
     * @param newName Nowa nazwa talii
     * @return Nowa nazwa talii jeśli operacja się powiodła, null jeśli talia nie istnieje
     */
    @Override
    public String renameDeck(String deckId, String newName) {
        try{
            if(!deckRepository.existsById(deckId)){
                log.error("Talia o ID '{}' nie istnieje", deckId);
                return null;
            }
            Deck deck = deckRepository.findById(deckId).orElseThrow();
            deck.setName(newName);
            deckRepository.save(deck);
            log.info("Talia o ID '{}' została pomyślnie przemianowana na '{}'", deckId, newName);
            return newName;
        }catch (DataAccessException e) {
            log.error("Błąd dostępu do danych podczas zmiany nazwy talii: {}", e.getMessage());
            throw new RuntimeException("Błąd dostępu do danych podczas zmiany nazwy talii: {}", e);
            }
        catch(Exception e){
                log.error("Błąd podczas zmiany nazwy talii: {}", e.getMessage());
                throw new RuntimeException("Błąd podczas zmiany nazwy talii: {}", e);
            }
    }

    /**
     * Zmienia widoczność talii (publiczna/prywatna)
     * 
     * @param deckId ID talii
     * @param isPublic true jeśli talia ma być publiczna, false jeśli prywatna
     * @return true jeśli operacja się powiodła, false jeśli talia nie istnieje
     */
    @Override
    public boolean changeDeckVisibility(String deckId, boolean isPublic) {
        try {
            if(!deckRepository.existsById(deckId)){
                return false;
            }
            Deck deck = deckRepository.findById(deckId).orElseThrow();
            deck.setPublic(isPublic);
            deckRepository.save(deck);
            return true;
        }
        catch (DataAccessException e){
            log.error("Błąd dostępu do danych podczas zmiany widoczności talii: {}", e.getMessage());
            throw new RuntimeException("Błąd dostępu do danych podczas zmiany widoczności talii: {}", e);
        }
        catch (Exception e) {
            log.error("Błąd podczas zmiany widoczności talii: {}", e.getMessage());
            throw new RuntimeException("Błąd podczas zmiany widoczności talii: {}", e);
        }

    }

    /**
     * Zmienia właściciela talii (STUDENT/TEACHER)
     * 
     * @param deckId ID talii
     * @param newOwner Nowy typ właściciela (STUDENT lub TEACHER)
     * @return Nowy typ właściciela jeśli operacja się powiodła, null jeśli talia nie istnieje
     */
    @Override
    public DeckOwner changeDeckOwner(String deckId, DeckOwner newOwner) {
        try{
            if(!deckRepository.existsById(deckId)){
                log.error("Talia o ID '{}' nie istnieje", deckId);
                return null;
            }
            Deck deck = deckRepository.findById(deckId).orElseThrow();
            deck.setOwner(newOwner);
            deckRepository.save(deck);
            log.info("Właściciel talii o ID '{}' został pomyślnie zmieniony na '{}'", deckId, newOwner);
            return newOwner;
        }
        catch (DataAccessException e){
            log.error("Błąd dostępu do danych podczas zmiany właściciela talii: {}", e.getMessage());
            throw new RuntimeException("Błąd dostępu do danych podczas zmiany właściciela talii: {}", e);
        }
        catch (Exception e){
            log.error("Błąd podczas zmiany właściciela talii: {}", e.getMessage());
            throw new RuntimeException("Błąd podczas zmiany właściciela talii: {}", e);
        }
    }

    /**
     * Pobiera podstawowe informacje o talii
     * 
     * @param deckId ID talii
     * @return DeckDto z podstawowymi informacjami o talii, null jeśli talia nie istnieje
     */
    @Override
    public DeckDto getDeckById(String deckId) {
        try {
            if (!deckRepository.existsById(deckId)) {
                log.error("Talia o ID '{}' nie istnieje", deckId);
                return null;
            }
            Deck deck = deckRepository.findById(deckId).orElseThrow();
            return new DeckDto(
                    deck.getId(),
                    deck.getName(),
                    deck.isPublic(),
                    deck.getUserId(),
                    deck.getOwner().name(),
                    deck.getWordCount()
            );
        }
        catch (DataAccessException e){
            log.error("Błąd dostępu do danych podczas pobierania talii: {}", e.getMessage());
            throw new RuntimeException("Błąd dostępu do danych podczas pobierania talii: {}", e);
        }
        catch (Exception e){
            log.error("Błąd podczas pobierania talii: {}", e.getMessage());
            throw new RuntimeException("Błąd podczas pobierania talii: {}", e);
        }
    }

    /**
     * Filtruje talie na podstawie podanych parametrów.
     * Wszystkie parametry poza userId są opcjonalne (mogą być null).
     * 
     * @param userId ID użytkownika (opcjonalne)
     * @param isPublic Czy talia jest publiczna (opcjonalne)
     * @param owner Typ właściciela talii (opcjonalne)
     * @return Lista DeckDto spełniających kryteria
     */
    @Override
    public List<DeckDto> getDecksByFilter(String userId, Boolean isPublic, DeckOwner owner) {
        try {
            log.info("Filtrowanie talii: userId={}, isPublic={}, owner={}",
                    userId, isPublic, owner);
            List<Deck> decks;
            decks = deckRepository.findByFilters(userId, isPublic, owner);
            log.info("Znaleziono {} talii spełniających kryteria", decks.size());
            return decks.stream()
                    .map(deck -> new DeckDto(
                            deck.getId(),
                            deck.getName(),
                            deck.isPublic(),
                            deck.getUserId(),
                            deck.getOwner().name(),
                            deck.getWordCount()
                    ))
                    .toList();
                    
        } catch (DataAccessException e) {
            log.error("Błąd dostępu do danych podczas filtrowania talii: {}", e.getMessage());
            throw new RuntimeException("Błąd dostępu do danych podczas filtrowania talii", e);
        } catch (Exception e) {
            log.error("Błąd podczas filtrowania talii: {}", e.getMessage());
            throw new RuntimeException("Błąd podczas filtrowania talii", e);
        }
    }

    /**
     * Pobiera szczegółowe informacje o talii
     * @param deckId ID talii
     * @return DeckDetailsDto z pełnymi informacjami
     */
    @Override
    public DeckDetailsDto getDeckDetailsById(String deckId) {
        try {
            log.info("Pobieranie szczegółów talii o ID: {}", deckId);
            if (!deckRepository.existsById(deckId)) {
                log.error("Talia o ID '{}' nie istnieje", deckId);
                return null;
            }
            Deck deck = deckRepository.findById(deckId).orElseThrow();
            DeckDetailsDto detailsDto = DeckDetailsDto.from(deck);
            log.info("Pomyślnie pobrano szczegóły talii: {}", deckId);
            return detailsDto;
        } catch (DataAccessException e) {
            log.error("Błąd dostępu do danych podczas pobierania szczegółów talii: {}", e.getMessage());
            throw new RuntimeException("Błąd dostępu do danych podczas pobierania szczegółów talii", e);
        } catch (Exception e) {
            log.error("Błąd podczas pobierania szczegółów talii: {}", e.getMessage());
            throw new RuntimeException("Błąd podczas pobierania szczegółów talii", e);
        }
    }

    /**
     * Edytuje szczegóły talii
     * @param deckId ID talii do edycji
     * @param deckDetailsDto DTO z nowymi danymi
     * @return Zaktualizowany DeckDetailsDto
     */
    @Override
    @Transactional
    public DeckDetailsDto editDeckDetails(String deckId, DeckDetailsDto deckDetailsDto) {
        try {
            log.info("Edycja szczegółów talii o ID: {}", deckId);
            if (!deckRepository.existsById(deckId)) {
                log.error("Talia o ID '{}' nie istnieje", deckId);
                return null;
            }
            Deck deck = deckRepository.findById(deckId).orElseThrow();
            deckDetailsDto.updateEntity(deck);
            Deck updatedDeck = deckRepository.save(deck);
            log.info("Pomyślnie zaktualizowano szczegóły talii: {}", deckId);
            return DeckDetailsDto.from(updatedDeck);
        } catch (DataAccessException e) {
            log.error("Błąd dostępu do danych podczas edycji szczegółów talii: {}", e.getMessage());
            throw new RuntimeException("Błąd dostępu do danych podczas edycji szczegółów talii", e);
        } catch (Exception e) {
            log.error("Błąd podczas edycji szczegółów talii: {}", e.getMessage());
            throw new RuntimeException("Błąd podczas edycji szczegółów talii", e);
        }
    }

    /**
     * Pobiera łączną liczbę fiszek w talii
     * 
     * @param deckId ID talii
     * @return Liczba fiszek w talii, 0 jeśli talia nie istnieje
     */
    @Override
    public long getTotalFlashcardsCount(String deckId) {
        log.info("Pobieranie łącznej liczby fiszek dla talii o ID: {}", deckId);
        try{
            if(!deckRepository.existsById(deckId)){
                log.error("Talia o ID '{}' nie istnieje", deckId);
                return 0;
            }
            Deck deck = deckRepository.findById(deckId).orElseThrow();
            return deck.getWordCount();
        }
        catch (DataAccessException e){
            log.error("Błąd dostępu do danych podczas pobierania łącznej liczby fiszek: {}", e.getMessage());
            throw new RuntimeException("Błąd dostępu do danych podczas pobierania łącznej liczby fiszek: {}", e);
        }
        catch (Exception e){
            log.error("Błąd podczas pobierania łącznej liczby fiszek: {}", e.getMessage());
            throw new RuntimeException("Błąd podczas pobierania łącznej liczby fiszek: {}", e);
        }
    }

    /**
     * Aktualizuje algorytm nauki dla talii
     * 
     * @param deckId ID talii
     * @param algorithm Nowy algorytm nauki (np. LEITNER, SM2)
     */
    @Override
    public void updateLearnAlgorithm(String deckId, LearnAlgorithm algorithm) {
        try {
            if (!deckRepository.existsById(deckId)) {
                log.error("Talia o ID '{}' nie istnieje", deckId);
                return;
            }
            Deck deck = deckRepository.findById(deckId).orElseThrow();
            deck.setLearnAlgorithm(algorithm);
            deckRepository.save(deck);
            log.info("Algorytm nauki dla talii o ID '{}' został pomyślnie zaktualizowany na '{}'", deckId, algorithm);
        } catch (DataAccessException e) {
            log.error("Błąd dostępu do danych podczas aktualizacji algorytmu nauki: {}", e.getMessage());
            throw new RuntimeException("Błąd dostępu do danych podczas aktualizacji algorytmu nauki: {}", e);
        } catch (Exception e) {
            log.error("Błąd podczas aktualizacji algorytmu nauki: {}", e.getMessage());
            throw new RuntimeException("Błąd podczas aktualizacji algorytmu nauki: {}", e);
        }
    }

    /**
     * Aktualizuje liczbę fiszek wyświetlanych podczas jednej sesji nauki
     * 
     * @param deckId ID talii
     * @param count Nowa liczba fiszek na sesję (musi być większa od 0)
     */
    @Override
    public void updateFlashcardsPerSession(String deckId, Long count) {
        try {
            if (!deckRepository.existsById(deckId)) {
                log.error("Talia o ID '{}' nie istnieje", deckId);
                return;
            }
            Deck deck = deckRepository.findById(deckId).orElseThrow();
            deck.setHowManyFlashcardsForOneSession(count);
            deckRepository.save(deck);
            log.info("Liczba fiszek na sesję dla talii o ID '{}' została pomyślnie zaktualizowana na '{}'", deckId, count);
        } catch (DataAccessException e) {
            log.error("Błąd dostępu do danych podczas aktualizacji liczby fiszek na sesję: {}", e.getMessage());
            throw new RuntimeException("Błąd dostępu do danych podczas aktualizacji liczby fiszek na sesję: {}", e);
        } catch (Exception e) {
            log.error("Błąd podczas aktualizacji liczby fiszek na sesję: {}", e.getMessage());
            throw new RuntimeException("Błąd podczas aktualizacji liczby fiszek na sesję: {}", e);
        }
    }

    @Override
    public UserDeckCountDto getUserDeckCount(String userId) {
        if (userId == null || userId.isBlank()) {
            log.error("UserId is null or blank");
            throw new IllegalArgumentException("UserId nie może być pusty");
        }
        
        try {
            log.debug("Getting deck count for user: {}", userId);
            long totalDecks = deckRepository.countByUserId(userId);
            long publicDecks = deckRepository.countByUserIdAndIsPublic(userId, true);
            long privateDecks = deckRepository.countByUserIdAndIsPublic(userId, false);
            
            log.info("User {} has {} total decks ({} public, {} private)", userId, totalDecks, publicDecks, privateDecks);
            return new UserDeckCountDto(userId, totalDecks, publicDecks, privateDecks);
        } catch (DataAccessException e) {
            log.error("Database error while getting deck count for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Błąd dostępu do bazy danych podczas pobierania liczby talii", e);
        } catch (Exception e) {
            log.error("Error while getting deck count for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Błąd podczas pobierania liczby talii użytkownika", e);
        }
    }

    @Override
    public DeckStatisticsDto getDeckStatistics(String deckId) {
        if (deckId == null || deckId.isBlank()) {
            log.error("DeckId is null or blank");
            throw new IllegalArgumentException("DeckId nie może być pusty");
        }
        
        try {
            log.debug("Getting statistics for deck: {}", deckId);
            Deck deck = deckRepository.findById(deckId)
                    .orElseThrow(() -> new RuntimeException("Talia o ID " + deckId + " nie została znaleziona"));
            
            long totalFlashcards = flashcardRepository.countByDeckId(deckId);
            long learnedFlashcards = flashcardRepository.countByDeckIdAndIsLearned(deckId, true);
            long unlearnedFlashcards = totalFlashcards - learnedFlashcards;
            double progressPercentage = totalFlashcards > 0 
                    ? Math.round((learnedFlashcards * 100.0 / totalFlashcards) * 100.0) / 100.0 
                    : 0.0;
            
            long totalSessions = sessionRepository.countByDeckId(deckId);
            long completedSessions = sessionRepository.countByDeckIdAndStatus(deckId, SessionStatus.COMPLETED);
            
            DeckStatisticsDto stats = DeckStatisticsDto.builder()
                    .deckId(deckId)
                    .deckName(deck.getName())
                    .totalFlashcards((int) totalFlashcards)
                    .learnedFlashcards((int) learnedFlashcards)
                    .unlearnedFlashcards((int) unlearnedFlashcards)
                    .progressPercentage(progressPercentage)
                    .totalSessions((int) totalSessions)
                    .completedSessions((int) completedSessions)
                    .build();
            
            log.info("Statistics for deck {}: {} flashcards ({}% learned), {} sessions ({} completed)", 
                    deckId, totalFlashcards, progressPercentage, totalSessions, completedSessions);
            return stats;
        } catch (DataAccessException e) {
            log.error("Database error while getting statistics for deck {}: {}", deckId, e.getMessage());
            throw new RuntimeException("Błąd dostępu do bazy danych podczas pobierania statystyk talii", e);
        } catch (Exception e) {
            log.error("Error while getting statistics for deck {}: {}", deckId, e.getMessage());
            throw new RuntimeException("Błąd podczas pobierania statystyk talii", e);
        }
    }

    @Override
    public boolean isDeckNameTaken(String userId, String deckName) {
        if (userId == null || userId.isBlank()) {
            log.error("UserId is null or blank");
            throw new IllegalArgumentException("UserId nie może być pusty");
        }
        if (deckName == null || deckName.isBlank()) {
            log.error("DeckName is null or blank");
            throw new IllegalArgumentException("Nazwa talii nie może być pusta");
        }
        
        try {
            log.debug("Checking if deck name '{}' is taken for user {}", deckName, userId);
            boolean isTaken = deckRepository.existsByNameAndUserId(deckName, userId);
            log.info("Deck name '{}' for user {} is {}", deckName, userId, isTaken ? "taken" : "available");
            return isTaken;
        } catch (DataAccessException e) {
            log.error("Database error while checking deck name availability: {}", e.getMessage());
            throw new RuntimeException("Błąd dostępu do bazy danych podczas sprawdzania nazwy talii", e);
        } catch (Exception e) {
            log.error("Error while checking deck name availability: {}", e.getMessage());
            throw new RuntimeException("Błąd podczas sprawdzania dostępności nazwy talii", e);
        }
    }
}

