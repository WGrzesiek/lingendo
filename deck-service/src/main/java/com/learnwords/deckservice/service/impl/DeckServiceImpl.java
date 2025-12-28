package com.learnwords.deckservice.service.impl;

import com.learnwords.auth.v1.GetUserNameByIdResponse;
import com.learnwords.common.KafkaTopic;
import com.learnwords.common.events.DeckCreatedEvent;
import com.learnwords.deckservice.dto.deck.CreateDeckDto;
import com.learnwords.deckservice.dto.deck.DeckDetailsDto;
import com.learnwords.deckservice.dto.deck.DeckDto;
import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.enums.*;
import com.learnwords.deckservice.exception.exceptions.DeckWithThisNameForThisUserAlreadyExistsException;
import com.learnwords.deckservice.repository.DeckRepository;
import com.learnwords.deckservice.service.DeckEnrollmentService;
import com.learnwords.deckservice.service.DeckService;
import com.learnwords.deckservice.service.event.GenericEventProducer;
import com.learnwords.deckservice.service.grpcClient.UserGrcpClient;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.learnwords.deckservice.service.utils.DeckUtils.getDeckIfUserHasPermissions;

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
 * @version 2.0
 * @since 2025-11-24
 * @see DeckService
 * @see Deck
 * @see DeckDto
 * @see DeckDetailsDto
 */
@Slf4j
@Service
public class DeckServiceImpl implements DeckService {
    private final DeckRepository deckRepository;
    private final GenericEventProducer eventProducer;
    private final UserGrcpClient userGrcpClient;

    public DeckServiceImpl(
            DeckRepository deckRepository,
            GenericEventProducer eventProducer, UserGrcpClient userGrcpClient) {
        this.deckRepository = deckRepository;
        this.eventProducer = eventProducer;
        this.userGrcpClient = userGrcpClient;
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
                .ownerId(userId)
                .owner(createDeckDto.getOwner())
                .howManyFlashcardsForOneSession(createDeckDto.getHowManyFlashcardsForOneSession())
                .visibility(createDeckDto.getVisibility())
                .learnAlgorithm(createDeckDto.getLearnAlgorithm())
                .languageFrom(createDeckDto.getLanguageFrom())
                .languageTo(createDeckDto.getLanguageTo())
                .category(createDeckDto.getCategory())
                .difficulty(createDeckDto.getDifficulty())
                .reviewSchedule(createDeckDto.getReviewSchedule())
                .build();
        deckRepository.save(deck);
        DeckCreatedEvent deckCreatedEvent = DeckCreatedEvent.builder()
                .eventTime(deck.getCreatedAt())
                .deckId(deck.getId())
                .userId(userId)
                .deckName(deck.getName())
                .deckCategory(deck.getCategory().name())
                .languageFrom(deck.getLanguageFrom().name())
                .languageTo(deck.getLanguageTo().name())
                .receivedAt(Instant.now())
                .build();
        eventProducer.send(KafkaTopic.DECK_CREATED, deckCreatedEvent);
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
        getDeckIfUserHasPermissions(deckRepository, deckId, userId);
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
        Deck deck = getDeckIfUserHasPermissions(deckRepository, deckId, userId);
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
     * @param visibility true jeśli talia ma być publiczna, false jeśli prywatna
     * @return true jeśli operacja się powiodła
     */
    @Override
    @Transactional
    public void changeDeckVisibility(String deckId, String userId, DeckVisibility visibility) {
        Deck deck = getDeckIfUserHasPermissions(deckRepository, deckId, userId);
        deck.setVisibility(visibility);
        deckRepository.save(deck);
        log.info("Widoczność talii o ID '{}' została pomyślnie zmieniona na '{}'", deckId, visibility);
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
        Deck deck = getDeckIfUserHasPermissions(deckRepository, deckId, userId);
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
        Deck deck = getDeckIfUserHasPermissions(deckRepository, deckId, userId);

        log.info("Pomyślnie pobrano talię o ID '{}'", deckId);
        return mapToDeckDto(deck, true);
    }

    @Override
    public Deck getDeckById(String deckId) {
        Deck deck = deckRepository.findById(deckId).orElseThrow(
                () -> {
                    log.error("Talia o ID '{}' nie istnieje", deckId);
                    return new IllegalArgumentException("Talia o podanym ID nie istnieje");
                }
        );
        log.info("Pomyślnie pobrano talię o ID '{}'", deckId);
        return deck;
    }


    /**
     * Filtruje talie należące do użytkownika na podstawie podanych parametrów.
     * Zwraca tylko talie, których właścicielem jest userId.
     * Parametr userId jest wymagany, pozostałe są opcjonalne.
     * 
     * <p>Uwaga: Ta metoda NIE zwraca talii udostępnionych użytkownikowi.
     * Do pobierania udostępnionych talii użyj {@link com.learnwords.deckservice.service.DeckShareService#getSharedWithMe(String, int, int)}.
     * 
     * @param userId ID użytkownika (wymagany)
     * @param visibility Lista widoczności do filtrowania (opcjonalne)
     * @param owner Typ właściciela talii (opcjonalne)
     * @param page Numer strony
     * @param size Rozmiar strony
     * @return Strona DeckDto spełniających kryteria
     */
    @Override
    public Page<DeckDto> getDecksByFilter(String userId, List<DeckVisibility> visibility, DeckOwner owner, int page, int size) {
        log.info("Filtrowanie talii użytkownika: userId={}, visibility={}, owner={}",
                userId, visibility, owner);
        if(userId == null || userId.isBlank()) {
            log.error("UserId jest pusty");
            throw new IllegalArgumentException("UserId nie może być pusty");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Deck> decks = deckRepository.findOwnedDecksWithFilters(userId, visibility, owner, pageable);
        log.info("Znaleziono {} talii należących do użytkownika", decks.getContent().size());
        return decks.map(deck -> mapToDeckDto(deck, false));
    }

    /**
     * Pobiera szczegółowe informacje o talii
     * @param deckId ID talii
     * @param userId ID użytkownika próbującego pobrać szczegóły talii
     * @return DeckDetailsDto z pełnymi informacjami
     */
    @Override
    public DeckDetailsDto getDeckDetailsById(String deckId, String userId) {
        Deck deck = getDeckIfUserHasPermissions(deckRepository, deckId, userId);
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
        Deck deck = getDeckIfUserHasPermissions(deckRepository, deckId, userId);
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
     * @return Liczba fiszek w talii
     */
    @Override
    public long getTotalFlashcardsCount(String deckId, String userId) {
        Deck deck = getDeckIfUserHasPermissions(deckRepository, deckId, userId);
        log.info("Pobieranie łącznej liczby fiszek dla talii o ID: {}", deckId);
        return deck.getWordCount();
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

        boolean isTaken = deckRepository.existsByNameAndOwnerId(deckName, userId);

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

    private DeckDto mapToDeckDto(Deck deck, Boolean includeUsername) {
        GetUserNameByIdResponse userResponse = userGrcpClient.getUserNameById(deck.getOwnerId());
        return DeckDto.builder()
                .id(deck.getId())
                .name(deck.getName())
                .deckDescription(deck.getDescription())
                .deckDifficulty(deck.getDifficulty())
                .deckOwner(deck.getOwner())
                .deckCategory(deck.getCategory())
                .ownerId(deck.getOwnerId())
                .wordCount(deck.getWordCount())
                .visibility(deck.getVisibility())
                .createdAt(deck.getCreatedAt())
                .updatedAt(deck.getUpdatedAt())
                .username(includeUsername ? userResponse.getUsername() : null)
                .build();
    }
}

