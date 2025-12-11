package com.learnwords.deckservice.service.impl;

import com.learnwords.common.KafkaTopic;
import com.learnwords.common.events.DeckCreatedEvent;
import com.learnwords.deckservice.dto.deck.CreateDeckDto;
import com.learnwords.deckservice.dto.deck.DeckDetailsDto;
import com.learnwords.deckservice.dto.deck.DeckDto;
import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.enums.*;
import com.learnwords.deckservice.exception.exceptions.DeckWithThisNameForThisUserAlreadyExistsException;
import com.learnwords.deckservice.exception.exceptions.UserPermissionsMissing;
import com.learnwords.deckservice.repository.DeckRepository;
import com.learnwords.deckservice.service.DeckEnrollmentService;
import com.learnwords.deckservice.service.event.GenericEventProducer;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Epic("Decks")
@Feature("DeckServiceImpl")
@DisplayName("DeckServiceImpl - testy jednostkowe")
class DeckServiceImplTest {

    private static final String USER_ID = "user-123";
    private static final String OTHER_USER_ID = "user-456";
    private static final String DECK_ID = "deck-1";
    private static final String DECK_NAME = "English Basics";

    @Mock
    private DeckRepository deckRepository;

    @Mock
    private DeckEnrollmentService deckEnrollmentService;

    @Mock
    private GenericEventProducer eventProducer;

    @InjectMocks
    private DeckServiceImpl deckService;

    private Deck deck;
    private CreateDeckDto createDeckDto;

    @BeforeEach
    void setUp() {
        deck = Deck.builder()
                .id(DECK_ID)
                .name(DECK_NAME)
                .description("Test deck")
                .ownerId(USER_ID)
                .visibility(DeckVisibility.PRIVATE)
                .owner(DeckOwner.I)
                .learnAlgorithm(LearnAlgorithm.GRZESIEK_ALGORITHM)
                .languageFrom(Language.ENGLISH)
                .languageTo(Language.POLISH)
                .category(DeckCategory.IT)
                .difficulty(DeckDifficulty.MEDIUM)
                .wordCount(50)
                .howManyFlashcardsForOneSession(15L)
                .createdAt(Instant.now())
                .build();

        createDeckDto = CreateDeckDto.builder()
                .deckName(DECK_NAME)
                .description("Test deck")
                .visibility(DeckVisibility.PRIVATE)
                .owner(DeckOwner.I)
                .learnAlgorithm(LearnAlgorithm.GRZESIEK_ALGORITHM)
                .languageFrom(Language.ENGLISH)
                .languageTo(Language.POLISH)
                .category(DeckCategory.IT)
                .difficulty(DeckDifficulty.EASY)
                .howManyFlashcardsForOneSession(20L)
                .build();
    }

    @Test
    @Story("Tworzenie talii")
    @DisplayName("Tworzy talię gdy nazwa jest wolna i wysyła DeckCreatedEvent")
    @Description("Tworzy talię gdy nazwa jest wolna i wysyła DeckCreatedEvent z poprawnym ładunkiem")
    @Severity(SeverityLevel.CRITICAL)
    void createDeck_shouldPersistAndEmitEvent() {
        when(deckRepository.existsByNameAndOwnerId(DECK_NAME, USER_ID)).thenReturn(false);
        when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));

        deckService.createDeck(USER_ID, createDeckDto);

        ArgumentCaptor<Deck> deckCaptor = ArgumentCaptor.forClass(Deck.class);
        verify(deckRepository).save(deckCaptor.capture());
        Deck savedDeck = deckCaptor.getValue();

        assertThat(savedDeck.getId()).isNotBlank();
        assertThat(savedDeck.getName()).isEqualTo(DECK_NAME);
        assertThat(savedDeck.getOwnerId()).isEqualTo(USER_ID);
        assertThat(savedDeck.getVisibility()).isEqualTo(DeckVisibility.PRIVATE);
        assertThat(savedDeck.getLearnAlgorithm()).isEqualTo(createDeckDto.getLearnAlgorithm());
        assertThat(savedDeck.getCategory()).isEqualTo(createDeckDto.getCategory());
        assertThat(savedDeck.getLanguageFrom()).isEqualTo(Language.ENGLISH);
        assertThat(savedDeck.getLanguageTo()).isEqualTo(Language.POLISH);

        ArgumentCaptor<DeckCreatedEvent> eventCaptor = ArgumentCaptor.forClass(DeckCreatedEvent.class);
        verify(eventProducer).send(eq(KafkaTopic.DECK_CREATED), eventCaptor.capture());
        DeckCreatedEvent event = eventCaptor.getValue();

        assertThat(event.deckId()).isEqualTo(savedDeck.getId());
        assertThat(event.deckName()).isEqualTo(savedDeck.getName());
        assertThat(event.userId()).isEqualTo(USER_ID);
        assertThat(event.deckCategory()).isEqualTo(savedDeck.getCategory().name());
        assertThat(event.languageFrom()).isEqualTo(savedDeck.getLanguageFrom().name());
        assertThat(event.languageTo()).isEqualTo(savedDeck.getLanguageTo().name());
        assertThat(event.eventTime()).isNotNull();
        assertThat(event.receivedAt()).isNotNull();
    }

    @Test
    @Story("Tworzenie talii")
    @DisplayName("Odrzuca tworzenie gdy właściciel ma talię o tej samej nazwie")
    @Description("Rzuca wyjątek dla duplikatu nazwy talii u tego samego użytkownika")
    @Severity(SeverityLevel.CRITICAL)
    void createDeck_shouldFailWhenNameTaken() {
        when(deckRepository.existsByNameAndOwnerId(DECK_NAME, USER_ID)).thenReturn(true);

        assertThatThrownBy(() -> deckService.createDeck(USER_ID, createDeckDto))
                .isInstanceOf(DeckWithThisNameForThisUserAlreadyExistsException.class);

        verify(deckRepository, never()).save(any(Deck.class));
        verify(eventProducer, never()).send(anyString(), any());
    }

    @Test
    @Story("Usuwanie talii")
    @DisplayName("Usuwa talię gdy użytkownik jest właścicielem")
    @Description("Usuwa talię po pozytywnej weryfikacji uprawnień właściciela")
    @Severity(SeverityLevel.CRITICAL)
    void deleteDeck_shouldRemoveDeck() {
        when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(deck));

        deckService.deleteDeck(DECK_ID, USER_ID);

        verify(deckRepository).deleteById(DECK_ID);
    }

    @Test
    @Story("Usuwanie talii")
    @DisplayName("Blokuje usunięcie gdy użytkownik nie jest właścicielem")
    @Description("Rzuca wyjątek uprawnień przy próbie usunięcia przez innego użytkownika")
    @Severity(SeverityLevel.BLOCKER)
    void deleteDeck_shouldRejectWhenUserNotOwner() {
        deck.setOwnerId(OTHER_USER_ID);
        when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(deck));

        assertThatThrownBy(() -> deckService.deleteDeck(DECK_ID, USER_ID))
                .isInstanceOf(UserPermissionsMissing.class);

        verify(deckRepository, never()).deleteById(anyString());
    }

    @Test
    @Story("Edycja talii")
    @DisplayName("Zmienia nazwę gdy nowa jest dostępna dla właściciela")
    @Description("Aktualizuje nazwę talii po zweryfikowaniu unikalności dla właściciela")
    @Severity(SeverityLevel.CRITICAL)
    void renameDeck_shouldUpdateName() {
        String newName = "Advanced English";
        when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(deck));
        when(deckRepository.existsByNameAndOwnerId(newName, USER_ID)).thenReturn(false);
        when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String result = deckService.renameDeck(DECK_ID, newName, USER_ID);

        assertThat(result).isEqualTo(newName);
        assertThat(deck.getName()).isEqualTo(newName);
        verify(deckRepository).save(deck);
    }

    @Test
    @Story("Edycja talii")
    @DisplayName("Blokuje zmianę nazwy gdy nowa jest zajęta")
    @Description("Rzuca wyjątek przy próbie nadania zajętej nazwy")
    @Severity(SeverityLevel.CRITICAL)
    void renameDeck_shouldFailWhenNameAlreadyTaken() {
        String newName = "Advanced English";
        when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(deck));
        when(deckRepository.existsByNameAndOwnerId(newName, USER_ID)).thenReturn(true);

        assertThatThrownBy(() -> deckService.renameDeck(DECK_ID, newName, USER_ID))
                .isInstanceOf(DeckWithThisNameForThisUserAlreadyExistsException.class);

        verify(deckRepository, never()).save(any(Deck.class));
    }

    @Test
    @Story("Edycja talii")
    @DisplayName("Aktualizuje widoczność talii po weryfikacji uprawnień")
    @Description("Zmienia widoczność talii i zapisuje zmianę w repozytorium")
    @Severity(SeverityLevel.NORMAL)
    void changeDeckVisibility_shouldPersistNewVisibility() {
        when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(deck));
        when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));

        deckService.changeDeckVisibility(DECK_ID, USER_ID, DeckVisibility.PUBLIC);

        assertThat(deck.getVisibility()).isEqualTo(DeckVisibility.PUBLIC);
        verify(deckRepository).save(deck);
    }

    @Test
    @Story("Edycja talii")
    @DisplayName("Aktualizuje właściciela talii po weryfikacji uprawnień")
    @Description("Zmienia właściciela talii i zapisuje w repozytorium")
    @Severity(SeverityLevel.NORMAL)
    void changeDeckOwner_shouldPersistNewOwner() {
        when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(deck));
        when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DeckOwner owner = deckService.changeDeckOwner(DECK_ID, USER_ID, DeckOwner.COMMUNITY);

        assertThat(owner).isEqualTo(DeckOwner.COMMUNITY);
        assertThat(deck.getOwner()).isEqualTo(DeckOwner.COMMUNITY);
        verify(deckRepository).save(deck);
    }

    @Test
    @Story("Pobieranie talii")
    @DisplayName("Zwraca talię w DTO gdy żądający jest właścicielem")
    @Description("Pobiera talię i mapuje do DeckDto dla właściciela")
    @Severity(SeverityLevel.NORMAL)
    void getDeckById_shouldReturnDtoWhenUserHasAccess() {
        when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(deck));

        DeckDto result = deckService.getDeckById(DECK_ID, USER_ID);

        assertThat(result.id()).isEqualTo(DECK_ID);
        assertThat(result.name()).isEqualTo(DECK_NAME);
        assertThat(result.ownerId()).isEqualTo(USER_ID);
        assertThat(result.ownerType()).isEqualTo(deck.getOwner().name());
        assertThat(result.visibility()).isEqualTo(deck.getVisibility());
    }

    @Test
    @Story("Pobieranie talii")
    @DisplayName("Odrzuca pobranie talii gdy żądający nie jest właścicielem")
    @Description("Rzuca wyjątek uprawnień przy próbie pobrania przez innego użytkownika")
    @Severity(SeverityLevel.CRITICAL)
    void getDeckById_shouldRejectWhenUserHasNoAccess() {
        deck.setOwnerId(OTHER_USER_ID);
        when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(deck));

        assertThatThrownBy(() -> deckService.getDeckById(DECK_ID, USER_ID))
                .isInstanceOf(UserPermissionsMissing.class);
    }

    @Test
    @Story("Pobieranie talii")
    @DisplayName("Pobiera talię po ID bez weryfikacji uprawnień (wewnętrznie)")
    @Description("Zwraca encję Deck dla podanego ID bez sprawdzania właściciela")
    @Severity(SeverityLevel.NORMAL)
    void getDeckByIdWithoutUser_shouldReturnDeck() {
        when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(deck));

        Deck result = deckService.getDeckById(DECK_ID);

        assertThat(result).isSameAs(deck);
    }

    @Test
    @Story("Pobieranie talii")
    @DisplayName("Rzuca wyjątek gdy talia nie istnieje (pobranie bez weryfikacji)")
    @Description("Rzuca IllegalArgumentException dla nieistniejącego ID w metodzie bez sprawdzania uprawnień")
    @Severity(SeverityLevel.NORMAL)
    void getDeckByIdWithoutUser_shouldThrowWhenMissing() {
        when(deckRepository.findById(DECK_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deckService.getDeckById(DECK_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Talia o podanym ID nie istnieje");
    }

    @Test
    @Story("Filtrowanie talii")
    @DisplayName("Filtruje talie po widoczności i właścicielu oraz mapuje do DTO")
    @Description("Zwraca listę DeckDto po zastosowaniu filtrów visibility i owner")
    @Severity(SeverityLevel.NORMAL)
    void getDecksByFilter_shouldMapResults() {
        Deck secondDeck = Deck.builder()
                .id("deck-2")
                .name("Public Deck")
                .ownerId(USER_ID)
                .visibility(DeckVisibility.PUBLIC)
                .owner(DeckOwner.COMMUNITY)
                .learnAlgorithm(LearnAlgorithm.LEINER_ALGORITHM)
                .languageFrom(Language.POLISH)
                .languageTo(Language.ENGLISH)
                .category(DeckCategory.SCIENCE)
                .difficulty(DeckDifficulty.HARD)
                .wordCount(8)
                .createdAt(Instant.now())
                .build();

        when(deckRepository.findByFilters(USER_ID, DeckVisibility.PUBLIC, DeckOwner.COMMUNITY))
                .thenReturn(List.of(secondDeck));

        List<DeckDto> result = deckService.getDecksByFilter(USER_ID, DeckVisibility.PUBLIC, DeckOwner.COMMUNITY);

        assertThat(result).hasSize(1);
        DeckDto dto = result.get(0);
        assertThat(dto.id()).isEqualTo(secondDeck.getId());
        assertThat(dto.ownerType()).isEqualTo(secondDeck.getOwner().name());
        assertThat(dto.visibility()).isEqualTo(DeckVisibility.PUBLIC);
        verify(deckRepository).findByFilters(USER_ID, DeckVisibility.PUBLIC, DeckOwner.COMMUNITY);
    }

    @Test
    @Story("Filtrowanie talii")
    @DisplayName("Waliduje obecność userId przy filtrowaniu")
    @Description("Rzuca IllegalArgumentException gdy userId jest pusty podczas filtrowania")
    @Severity(SeverityLevel.CRITICAL)
    void getDecksByFilter_shouldValidateUserId() {
        assertThatThrownBy(() -> deckService.getDecksByFilter(" ", null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("UserId nie może być pusty");
    }

    @Test
    @Story("Szczegóły talii")
    @DisplayName("Zwraca szczegóły talii gdy użytkownik ma uprawnienia")
    @Description("Pobiera DeckDetailsDto dla właściciela talii")
    @Severity(SeverityLevel.NORMAL)
    void getDeckDetailsById_shouldReturnDetails() {
        when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(deck));

        DeckDetailsDto details = deckService.getDeckDetailsById(DECK_ID, USER_ID);

        assertThat(details.getId()).isEqualTo(DECK_ID);
        assertThat(details.getOwnerId()).isEqualTo(USER_ID);
        assertThat(details.getName()).isEqualTo(DECK_NAME);
        assertThat(details.getVisibility()).isEqualTo(deck.getVisibility());
        assertThat(details.getLanguageFrom()).isEqualTo(deck.getLanguageFrom());
    }

    @Test
    @Story("Szczegóły talii")
    @DisplayName("Aktualizuje szczegóły talii i zapisuje zmiany")
    @Description("Aktualizuje pola talii na podstawie DeckDetailsDto po weryfikacji uprawnień")
    @Severity(SeverityLevel.CRITICAL)
    void editDeckDetails_shouldUpdateEntityAndPersist() {
        DeckDetailsDto update = DeckDetailsDto.builder()
                .name("Updated")
                .description("Updated description")
                .visibility(DeckVisibility.PUBLIC)
                .owner(DeckOwner.TEACHER)
                .learnAlgorithm(LearnAlgorithm.LEINER_ALGORITHM)
                .howManyFlashcardsForOneSession(30L)
                .languageFrom(Language.POLISH)
                .languageTo(Language.ENGLISH)
                .category(DeckCategory.SCIENCE)
                .difficulty(DeckDifficulty.HARD)
                .build();

        when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(deck));
        when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DeckDetailsDto result = deckService.editDeckDetails(DECK_ID, update, USER_ID);

        assertThat(deck.getName()).isEqualTo("Updated");
        assertThat(deck.getDescription()).isEqualTo("Updated description");
        assertThat(deck.getVisibility()).isEqualTo(DeckVisibility.PUBLIC);
        assertThat(deck.getOwner()).isEqualTo(DeckOwner.TEACHER);
        assertThat(deck.getLearnAlgorithm()).isEqualTo(LearnAlgorithm.LEINER_ALGORITHM);
        assertThat(deck.getHowManyFlashcardsForOneSession()).isEqualTo(30L);
        assertThat(deck.getLanguageFrom()).isEqualTo(Language.POLISH);
        assertThat(deck.getLanguageTo()).isEqualTo(Language.ENGLISH);
        assertThat(deck.getCategory()).isEqualTo(DeckCategory.SCIENCE);
        assertThat(deck.getDifficulty()).isEqualTo(DeckDifficulty.HARD);

        assertThat(result.getName()).isEqualTo("Updated");
        verify(deckRepository).save(deck);
    }

    @Test
    @Story("Statystyki talii")
    @DisplayName("Zwraca liczbę słówek w talii po weryfikacji uprawnień")
    @Description("Zwraca wordCount z talii gdy użytkownik ma dostęp")
    @Severity(SeverityLevel.MINOR)
    void getTotalFlashcardsCount_shouldReturnWordCount() {
        deck.setWordCount(42);
        when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(deck));

        long result = deckService.getTotalFlashcardsCount(DECK_ID, USER_ID);

        assertThat(result).isEqualTo(42);
    }

    @Test
    @Story("Walidacja nazwy talii")
    @DisplayName("Sprawdza zajętość nazwy poprzez repozytorium")
    @Description("Deleguje do repozytorium sprawdzenie istnienia talii o danej nazwie dla właściciela")
    @Severity(SeverityLevel.MINOR)
    void isDeckNameTaken_shouldReturnRepositoryResult() {
        when(deckRepository.existsByNameAndOwnerId(DECK_NAME, USER_ID)).thenReturn(true);

        boolean taken = deckService.isDeckNameTaken(USER_ID, DECK_NAME);

        assertThat(taken).isTrue();
        verify(deckRepository).existsByNameAndOwnerId(DECK_NAME, USER_ID);
    }

    @Test
    @Story("Walidacja nazwy talii")
    @DisplayName("Waliduje argumenty przed sprawdzeniem dostępności nazwy")
    @Description("Rzuca IllegalArgumentException gdy userId lub deckName są puste")
    @Severity(SeverityLevel.CRITICAL)
    void isDeckNameTaken_shouldValidateArguments() {
        assertThatThrownBy(() -> deckService.isDeckNameTaken(" ", DECK_NAME))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("userId lub deckName nie może być pusty");

        assertThatThrownBy(() -> deckService.isDeckNameTaken(USER_ID, " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("userId lub deckName nie może być pusty");
    }
}
