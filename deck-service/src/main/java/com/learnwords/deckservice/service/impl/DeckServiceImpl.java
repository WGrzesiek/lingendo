package com.learnwords.deckservice.service.impl;

import com.learnwords.deckservice.dto.*;
import com.learnwords.deckservice.dto.dashboard.StudentMyCourseListItemDto;
import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.enums.DeckOwner;
import com.learnwords.deckservice.enums.LearnAlgorithm;
import com.learnwords.deckservice.enums.SessionStatus;
import com.learnwords.deckservice.exception.exceptions.DeckNotFoundException;
import com.learnwords.deckservice.exception.exceptions.DeckWithThisNameForThisUserAlreadyExistsException;
import com.learnwords.deckservice.exception.exceptions.UserPermissionsMissing;
import com.learnwords.deckservice.repository.DeckRepository;
import com.learnwords.deckservice.repository.FlashcardRepository;
import com.learnwords.deckservice.repository.SessionRepository;
import com.learnwords.deckservice.service.DeckService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Formula;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public void createDeck(String userId, CreateDeckDto createDeckDto) {

        log.info("Tworzenie talii: {}", createDeckDto.getDeckName());
        assertDeckNameIsFree(userId, createDeckDto.getDeckName());
        Deck deck = Deck.builder()
                .id(UUID.randomUUID().toString())
                .name(createDeckDto.getDeckName())
                .description(createDeckDto.getDescription())
                .userId(userId)
                .howManyFlashcardsForOneSession(createDeckDto.getHowManyFlashcardsForOneSession())
                .isPublic(createDeckDto.getIsPublic())
                .wordCount(0)
                .learnAlgorithm(createDeckDto.getLearnAlgorithm())
                .languageFrom(createDeckDto.getLanguageFrom())
                .languageTo(createDeckDto.getLanguageTo())
                .owner(createDeckDto.getOwner())
                .build();

        deckRepository.save(deck);

        log.info("Talia '{}' została utworzona przez użytkownika {}", createDeckDto.getDeckName(), userId);
    }


    /**
     * Usuwa talię na podstawie podanego ID oraz sprawdza czy użytkownik ma do tego uprawnienia
     * 
     * @param deckId ID talii do usunięcia
     * @param userId ID użytkownika próbującego usunąć talię
     */
    @Override
    @Transactional
    public void deleteDeck(String deckId, String userId) {
        getDeckIfUserHasPermissions(deckId, userId);
        deckRepository.deleteById(deckId);
        log.info("Talia o ID '{}' została pomyślnie usunięta", deckId);
    }

    /**
     * Zmienia nazwę talii
     * 
     * @param deckId ID talii do przemianowania
     * @param newName Nowa nazwa talii
     * @param userId ID użytkownika próbującego zmienić nazwę talii
     * @return Nowa nazwa talii jeśli operacja się powiodła
     */
    @Override
    @Transactional
    public String renameDeck(String deckId, String newName, String userId) {
        Deck deck = getDeckIfUserHasPermissions(deckId, userId);
        assertDeckNameIsFree(userId, newName);
        deck.setName(newName);
        deckRepository.save(deck);
        log.info("Talia o ID '{}' została pomyślnie przemianowana na '{}'", deckId, newName);
        return newName;
    }

    /**
     * Zmienia widoczność talii (publiczna/prywatna)
     * 
     * @param deckId ID talii
     * @param userId ID użytkownika próbującego zmienić widoczność talii
     * @param isPublic true jeśli talia ma być publiczna, false jeśli prywatna
     * @return true jeśli operacja się powiodła
     */
    @Override
    @Transactional
    public boolean changeDeckVisibility(String deckId, String userId, boolean isPublic) {
        Deck deck = getDeckIfUserHasPermissions(deckId, userId);
        deck.setPublic(isPublic);
        deckRepository.save(deck);
        log.info("Widoczność talii o ID '{}' została pomyślnie zmieniona na '{}'", deckId, isPublic);
        return true;
    }

    /**
     * Zmienia właściciela talii
     * 
     * @param deckId ID talii
     * @param userId ID użytkownika próbującego zmienić właściciela talii
     * @param newOwner Nowy typ właściciela
     * @return Nowy typ właściciela jeśli operacja się powiodła
     */
    @Override
    @Transactional
    public DeckOwner changeDeckOwner(String deckId, String userId, DeckOwner newOwner) {
        Deck deck = getDeckIfUserHasPermissions(deckId, userId);
        deck.setOwner(newOwner);
        deckRepository.save(deck);
        log.info("Właściciel talii o ID '{}' został pomyślnie zmieniony na '{}'", deckId, newOwner);
        return newOwner;
    }

    /**
     * Pobiera podstawowe informacje o talii
     * 
     * @param deckId ID talii
     * @param userId ID użytkownika próbującego pobrać talię
     * @return DeckDto z podstawowymi informacjami o talii
     */
    @Override
    public DeckDto getDeckById(String deckId, String userId) {
        Deck deck = getDeckIfUserHasPermissions(deckId, userId);
        log.info("Pomyślnie pobrano talię o ID '{}'", deckId);
        return mapToDeckDto(deck);
    }


    /**
     * Filtruje talie na podstawie podanych parametrów.
     * Parametr userId jest wymagany, pozostałe są opcjonalne.
     * 
     * @param userId ID użytkownika (wymagany)
     * @param isPublic Czy talia jest publiczna (opcjonalne)
     * @param owner Typ właściciela talii (opcjonalne)
     * @return Lista DeckDto spełniających kryteria
     */
    @Override
    public List<DeckDto> getDecksByFilter(String userId, Boolean isPublic, DeckOwner owner) {
        log.info("Filtrowanie talii: userId={}, isPublic={}, owner={}",
                userId, isPublic, owner);
        if(userId == null || userId.isBlank()) {
            log.error("UserId jest pusty");
            throw new IllegalArgumentException("UserId nie może być pusty");
        }
        List<Deck> decks = deckRepository.findByFilters(userId, isPublic, owner);
        log.info("Znaleziono {} talii spełniających kryteria", decks.size());
        return decks.stream()
                .map(this::mapToDeckDto)
                .toList();
    }

    /**
     * Pobiera wszystkie publiczne talie.
     * 
     * @return Lista wszystkich publicznych talii
     */
    @Override
    public List<DeckDto> getPublicDecks() {
        log.info("Pobieranie wszystkich publicznych talii");
        List<Deck> decks = deckRepository.findByIsPublic(true);
        log.info("Znaleziono {} publicznych talii", decks.size());
        return decks.stream()
                .map(this::mapToDeckDto)
                .toList();
    }

    /**
     * Pobiera szczegółowe informacje o talii
     * @param deckId ID talii
     * @param userId ID użytkownika próbującego pobrać szczegóły talii
     * @return DeckDetailsDto z pełnymi informacjami
     */
    @Override
    public DeckDetailsDto getDeckDetailsById(String deckId, String userId) {
        Deck deck = getDeckIfUserHasPermissions(deckId, userId);
        DeckDetailsDto detailsDto = DeckDetailsDto.from(deck);
        log.info("Pomyślnie pobrano szczegóły talii: {}", deckId);
        return detailsDto;
    }

    /**
     * Edytuje szczegóły talii
     * @param deckId ID talii do edycji
     * @param deckDetailsDto DTO z nowymi danymi
     * @param userId ID użytkownika próbującego edytować talię
     * @return Zaktualizowany DeckDetailsDto
     */
    @Override
    @Transactional
    public DeckDetailsDto editDeckDetails(String deckId, DeckDetailsDto deckDetailsDto, String userId) {
        Deck deck = getDeckIfUserHasPermissions(deckId, userId);
        deckDetailsDto.updateEntity(deck);
        Deck updatedDeck = deckRepository.save(deck);
        log.info("Pomyślnie zaktualizowano szczegóły talii: {}", deckId);
        return DeckDetailsDto.from(updatedDeck);
    }

    /**
     * Pobiera łączną liczbę fiszek w talii
     * 
     * @param deckId ID talii
     * @param userId ID użytkownika próbującego pobrać liczbę fiszek
     * @return Liczba fiszek w talii, 0 jeśli talia nie istnieje
     */
    @Override
    public long getTotalFlashcardsCount(String deckId, String userId) {
        Deck deck = getDeckIfUserHasPermissions(deckId, userId);
        log.info("Pobieranie łącznej liczby fiszek dla talii o ID: {}", deckId);
        return deck.getWordCount();
    }

    /**
     * Aktualizuje algorytm nauki dla talii
     * 
     * @param deckId ID talii
     * @param algorithm Nowy algorytm nauki
     * @param userId ID użytkownika próbującego zaktualizować algorytm nauki
     * @return Nazwa zaktualizowanego algorytmu
     */
    @Override
    @Transactional
    public String updateLearnAlgorithm(String deckId, LearnAlgorithm algorithm, String userId) {
        Deck deck = getDeckIfUserHasPermissions(deckId, userId);
        deck.setLearnAlgorithm(algorithm);
        deckRepository.save(deck);
        log.info("Algorytm nauki dla talii o ID '{}' został pomyślnie zaktualizowany na '{}'", deckId, algorithm);
        return algorithm.name();
    }

    /**
     * Aktualizuje liczbę fiszek wyświetlanych podczas jednej sesji nauki
     * 
     * @param deckId ID talii
     * @param count Nowa liczba fiszek na sesję
     * @param userId ID użytkownika próbującego zaktualizować liczbę fiszek na sesję
     * @return Nowa liczba fiszek na sesję
     */
    @Override
    @Transactional
    public Long updateFlashcardsPerSession(String deckId, Long count, String userId) {
        Deck deck = getDeckIfUserHasPermissions(deckId, userId);
        deck.setHowManyFlashcardsForOneSession(count);
        deckRepository.save(deck);
        log.info("Liczba fiszek na sesję dla talii o ID '{}' została pomyślnie zaktualizowana na '{}'", deckId, count);
        return count;
    }

    /**
     * Pobiera liczbę talii użytkownika
     *
     * @param userId ID użytkownika
     * @return UserDeckCountDto z liczbą talii
     */
    @Override
    public UserDeckCountDto getUserDeckCount(String userId) {
        if (userId == null || userId.isBlank()) {
            log.error("UserId jest pusty");
            throw new IllegalArgumentException("UserId nie może być pusty");
        }
        log.debug("Pobieranie liczby talii dla użytkownika: {}", userId);
        long totalDecks = deckRepository.countByUserId(userId);
        long publicDecks = deckRepository.countByUserIdAndIsPublic(userId, true);
        long privateDecks = deckRepository.countByUserIdAndIsPublic(userId, false);
        
        log.info("Użytkownik {} ma {} talii ({} publicznych, {} prywatnych)", userId, totalDecks, publicDecks, privateDecks);
        return new UserDeckCountDto(userId, totalDecks, publicDecks, privateDecks);
    }

    /**
     * Pobiera statystyki talii
     *
     * @param deckId ID talii
     * @param userId ID użytkownika próbującego pobrać statystyki talii
     * @return DeckStatisticsDto z danymi statystycznymi talii
     */
    @Override
    public DeckStatisticsDto getDeckStatistics(String deckId, String userId) {
        Deck deck = getDeckIfUserHasPermissions(deckId, userId);

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
        log.info("Pobrano statystyki talii o ID '{}': totalFlashcards={}, progressPercentage={}, totalSessions={}), completedSessions={}",
                deckId, totalFlashcards, progressPercentage, totalSessions, completedSessions);
        return stats;

    }
    /**
     * Pobiera talię dla widoku "Moje kursy" studenta z paginacją
     *
     * @param userId ID użytkownika
     * @param page Numer strony (0-indexed)
     * @param size Rozmiar strony
     * @return Strona z listą talii w formacie StudentMyCourseListItemDto
     */
    @Override
    public Page<StudentMyCourseListItemDto> getStudentMyCourseDecks(String userId, int page, int size) {
        if (userId == null || userId.isBlank()) {
            log.error("UserId jest pusty");
            throw new IllegalArgumentException("UserId nie może być pusty");
        }
        log.debug("Pobieranie talii dla użytkownika: {}", userId);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Order.asc("completionPercent"),
                        Sort.Order.desc("lastAccessed")
                )
        );
        return deckRepository.findByUserId(userId, pageable)
                .map(this::toDto);
    }

    private StudentMyCourseListItemDto toDto(Deck deck) {

        return new StudentMyCourseListItemDto(
                deck.getId(),
                deck.getName(),
                deck.getDescription(),
                deck.getTotalSessions(),
                deck.getSessionCompleted(),
                deck.getCompletionPercent(),
                deck.getLastAccessed(),
                deck.getDifficulty(),
                deck.getOwner()
        );
    }

    /**
     * Sprawdza czy nazwa talii jest już zajęta dla danego użytkownika
     *
     * @param userId ID użytkownika
     * @param deckName Nazwa talii do sprawdzenia
     * @return true jeśli nazwa jest zajęta, false w przeciwnym razie
     */
    @Override
    public boolean isDeckNameTaken(String userId, String deckName) {
        if (userId == null || userId.isBlank() || deckName == null || deckName.isBlank()) {
            log.error("userId lub deckName nie może być pusty");
            throw new IllegalArgumentException("userId lub deckName nie może być pusty");
        }

        boolean isTaken = deckRepository.existsByNameAndUserId(deckName, userId);

        if (isTaken) {
            log.info("Nazwa talii '{}' dla użytkownika '{}' jest ZAJĘTA", deckName, userId);
        } else {
            log.info("Nazwa talii '{}' dla użytkownika '{}' jest DOSTĘPNA", deckName, userId);
        }

        return isTaken;
    }

    /**
     * Assercja czy nazwa talii jest wolna dla danego użytkownika
     *
     * @param userId ID użytkownika
     * @param deckName Nazwa talii do sprawdzenia
     * @throws DeckWithThisNameForThisUserAlreadyExistsException gdy nazwa jest już zajęta
     */
    private void assertDeckNameIsFree(String userId, String deckName) {
        if (isDeckNameTaken(userId, deckName)) {
            throw new DeckWithThisNameForThisUserAlreadyExistsException(deckName);
        }
    }


    /**
     * Pobiera talię jeśli użytkownik ma do niej uprawnienia
     *
     * @param deckId ID talii
     * @param userId ID użytkownika
     * @return Deck jeśli użytkownik ma uprawnienia, wyjątek w przeciwnym razie
     * @throws IllegalArgumentException gdy userId lub deckId są puste
     * @throws DeckNotFoundException gdy talia o podanym ID nie istnieje
     * @throws UserPermissionsMissing gdy użytkownik nie ma uprawnień do talii
     */
    private Deck getDeckIfUserHasPermissions(String deckId, String userId) {
        if (userId == null || userId.isBlank() || deckId == null || deckId.isBlank()) {
            log.error("UserId lub DeckId nie może być pusty");
            throw new IllegalArgumentException("UserId lub DeckId nie może być pusty");
        }
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new DeckNotFoundException(
                        "Talia o ID '%s' nie istnieje".formatted(deckId)
                ));

        if (!deck.getUserId().equals(userId)) {
            log.warn("Użytkownik '{}' nie ma uprawnień do talii o ID '{}'", userId, deckId);
            throw new UserPermissionsMissing("Użytkownik nie ma uprawnień do tej talii");
        }
        return deck;
    }

    private DeckDto mapToDeckDto(Deck deck) {
        return new DeckDto(
                deck.getId(),
                deck.getName(),
                deck.isPublic(),
                deck.getUserId(),
                deck.getOwner().name(),
                deck.getWordCount()
        );
    }
}

