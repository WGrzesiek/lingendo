package com.learnwords.deckservice.integration;

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
import com.learnwords.deckservice.service.DeckService;
import com.learnwords.deckservice.service.event.GenericEventProducer;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@SpringBootTest
@Testcontainers
@Epic("Decks")
@Feature("DeckServiceImpl - integracja")
@DisplayName("DeckServiceImpl - testy integracyjne (PostgreSQL/Testcontainers)")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DeckServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("deck_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private DeckService deckService;

    @Autowired
    private DeckRepository deckRepository;

    @MockitoBean
    private GenericEventProducer eventProducer;

    @MockitoBean
    private DeckEnrollmentService deckEnrollmentService;

    private static final String USER_ID = "integration-user-1";
    private static final String OTHER_USER_ID = "integration-user-2";
    private static final String DECK_NAME = "Integration Deck";

    private CreateDeckDto createDeckDto;

    @BeforeEach
    void setUp() {
        createDeckDto = CreateDeckDto.builder()
                .deckName(DECK_NAME)
                .description("Opis integracyjny")
                .visibility(DeckVisibility.PRIVATE)
                .owner(DeckOwner.I)
                .learnAlgorithm(LearnAlgorithm.GRZESIEK_ALGORITHM)
                .languageFrom(Language.ENGLISH)
                .languageTo(Language.POLISH)
                .category(DeckCategory.IT)
                .difficulty(DeckDifficulty.EASY)
                .howManyFlashcardsForOneSession(20L)
                .build();
        deckRepository.deleteAll();
    }

    @Test
    @Transactional
    @Story("Tworzenie talii")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Tworzy talię i zapisuje w bazie")
    @Description("Tworzy nową talię, weryfikuje zapis w bazie i emisję zdarzenia DeckCreatedEvent")
    void createDeck_shouldPersistAndEmitEvent() {
        deckService.createDeck(USER_ID, createDeckDto);

        List<Deck> decks = deckRepository.findAll();
        assertThat(decks).hasSize(1);
        Deck saved = decks.get(0);
        assertThat(saved.getName()).isEqualTo(DECK_NAME);
        assertThat(saved.getOwnerId()).isEqualTo(USER_ID);
        assertThat(saved.getVisibility()).isEqualTo(DeckVisibility.PRIVATE);
        assertThat(saved.getOwner()).isEqualTo(DeckOwner.I);
        assertThat(saved.getLanguageFrom()).isEqualTo(Language.ENGLISH);
        assertThat(saved.getLanguageTo()).isEqualTo(Language.POLISH);
        assertThat(saved.getCategory()).isEqualTo(DeckCategory.IT);
        assertThat(saved.getDifficulty()).isEqualTo(DeckDifficulty.EASY);
        assertThat(saved.getCreatedAt()).isNotNull();

        verify(eventProducer).send(eq(KafkaTopic.DECK_CREATED), any(DeckCreatedEvent.class));
    }

    @Test
    @Transactional
    @Story("Tworzenie talii")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Blokuje duplikat nazwy dla tego samego użytkownika")
    @Description("Przy próbie utworzenia drugiej talii o tej samej nazwie dla tego samego właściciela rzuca wyjątek")
    void createDeck_shouldRejectDuplicateNameForSameUser() {
        deckService.createDeck(USER_ID, createDeckDto);

        assertThatThrownBy(() -> deckService.createDeck(USER_ID, createDeckDto))
                .isInstanceOf(DeckWithThisNameForThisUserAlreadyExistsException.class);

        assertThat(deckRepository.findAll()).hasSize(1);
        verify(eventProducer).send(eq(KafkaTopic.DECK_CREATED), any(DeckCreatedEvent.class));
    }

    @Test
    @Transactional
    @Story("Tworzenie talii")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Pozwala na tę samą nazwę dla innego użytkownika")
    @Description("Dwóch różnych użytkowników może utworzyć talie o tej samej nazwie")
    void createDeck_shouldAllowSameNameForDifferentUsers() {
        deckService.createDeck(USER_ID, createDeckDto);
        deckService.createDeck(OTHER_USER_ID, createDeckDto);

        List<Deck> decks = deckRepository.findAll();
        assertThat(decks).hasSize(2);
        long user1Count = decks.stream().filter(d -> d.getOwnerId().equals(USER_ID)).count();
        long user2Count = decks.stream().filter(d -> d.getOwnerId().equals(OTHER_USER_ID)).count();
        assertThat(user1Count).isEqualTo(1);
        assertThat(user2Count).isEqualTo(1);
    }

    @Test
    @Transactional
    @Story("Pobieranie talii")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Zwraca talię dla właściciela")
    @Description("Pobiera talię po ID i mapuje do DeckDto gdy żądający jest właścicielem")
    void getDeckById_shouldReturnDeckForOwner() {
        deckService.createDeck(USER_ID, createDeckDto);
        Deck saved = deckRepository.findAll().get(0);

        DeckDto dto = deckService.getDeckById(saved.getId(), USER_ID);

        assertThat(dto.id()).isEqualTo(saved.getId());
        assertThat(dto.name()).isEqualTo(DECK_NAME);
        assertThat(dto.ownerId()).isEqualTo(USER_ID);
    }

    @Test
    @Transactional
    @Story("Pobieranie talii")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Odrzuca pobranie talii przez innego użytkownika")
    @Description("Rzuca UserPermissionsMissing przy próbie pobrania talii przez nie-właściciela")
    void getDeckById_shouldRejectForOtherUser() {
        deckService.createDeck(USER_ID, createDeckDto);
        Deck saved = deckRepository.findAll().get(0);

        assertThatThrownBy(() -> deckService.getDeckById(saved.getId(), OTHER_USER_ID))
                .isInstanceOf(UserPermissionsMissing.class);
    }

    @Test
    @Transactional
    @Story("Edycja talii")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Zmienia nazwę talii i zapisuje w bazie")
    @Description("Po zmianie nazwy przez właściciela wartość jest zaktualizowana w bazie")
    void renameDeck_shouldUpdateName() {
        deckService.createDeck(USER_ID, createDeckDto);
        Deck saved = deckRepository.findAll().get(0);

        String newName = "Nowa nazwa";
        deckService.renameDeck(saved.getId(), newName, USER_ID);

        Deck updated = deckRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo(newName);
    }

    @Test
    @Transactional
    @Story("Edycja talii")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Zmienia widoczność talii")
    @Description("Aktualizuje visibility na PUBLIC i utrwala zmianę w bazie")
    void changeDeckVisibility_shouldPersistChange() {
        deckService.createDeck(USER_ID, createDeckDto);
        Deck saved = deckRepository.findAll().get(0);

        deckService.changeDeckVisibility(saved.getId(), USER_ID, DeckVisibility.PUBLIC);

        Deck updated = deckRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getVisibility()).isEqualTo(DeckVisibility.PUBLIC);
    }

    @Test
    @Transactional
    @Story("Edycja talii")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Zmienia właściciela talii")
    @Description("Aktualizuje pole owner i utrwala zmianę w bazie")
    void changeDeckOwner_shouldPersistChange() {
        deckService.createDeck(USER_ID, createDeckDto);
        Deck saved = deckRepository.findAll().get(0);

        deckService.changeDeckOwner(saved.getId(), USER_ID, DeckOwner.TEACHER);

        Deck updated = deckRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getOwner()).isEqualTo(DeckOwner.TEACHER);
    }

    @Test
    @Transactional
    @Story("Szczegóły talii")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Aktualizuje szczegóły talii")
    @Description("Edycja DeckDetailsDto zmienia pola encji w bazie")
    void editDeckDetails_shouldPersistChanges() {
        deckService.createDeck(USER_ID, createDeckDto);
        Deck saved = deckRepository.findAll().get(0);

        DeckDetailsDto update = DeckDetailsDto.builder()
                .name("Zmieniona")
                .description("Nowy opis")
                .visibility(DeckVisibility.PUBLIC)
                .owner(DeckOwner.COMMUNITY)
                .learnAlgorithm(LearnAlgorithm.LEINER_ALGORITHM)
                .howManyFlashcardsForOneSession(30L)
                .languageFrom(Language.POLISH)
                .languageTo(Language.ENGLISH)
                .category(DeckCategory.SCIENCE)
                .difficulty(DeckDifficulty.HARD)
                .build();

        deckService.editDeckDetails(saved.getId(), update, USER_ID);

        Deck updated = deckRepository.findById(saved.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Zmieniona");
        assertThat(updated.getDescription()).isEqualTo("Nowy opis");
        assertThat(updated.getVisibility()).isEqualTo(DeckVisibility.PUBLIC);
        assertThat(updated.getOwner()).isEqualTo(DeckOwner.COMMUNITY);
        assertThat(updated.getLearnAlgorithm()).isEqualTo(LearnAlgorithm.LEINER_ALGORITHM);
        assertThat(updated.getHowManyFlashcardsForOneSession()).isEqualTo(30L);
        assertThat(updated.getLanguageFrom()).isEqualTo(Language.POLISH);
        assertThat(updated.getLanguageTo()).isEqualTo(Language.ENGLISH);
        assertThat(updated.getCategory()).isEqualTo(DeckCategory.SCIENCE);
        assertThat(updated.getDifficulty()).isEqualTo(DeckDifficulty.HARD);
    }

    @Test
    @Transactional
    @Story("Filtrowanie talii")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Filtruje talie po widoczności i właścicielu")
    @Description("Zwraca tylko talie spełniające kryteria visibility i owner dla użytkownika")
    void getDecksByFilter_shouldReturnMatchingDecks() {
        deckService.createDeck(USER_ID, createDeckDto);

        CreateDeckDto publicDeckDto = CreateDeckDto.builder()
                .deckName("Publiczna talia")
                .description(createDeckDto.getDescription())
                .visibility(DeckVisibility.PUBLIC)
                .owner(DeckOwner.COMMUNITY)
                .learnAlgorithm(createDeckDto.getLearnAlgorithm())
                .languageFrom(createDeckDto.getLanguageFrom())
                .languageTo(createDeckDto.getLanguageTo())
                .category(createDeckDto.getCategory())
                .difficulty(createDeckDto.getDifficulty())
                .howManyFlashcardsForOneSession(createDeckDto.getHowManyFlashcardsForOneSession())
                .build();
        deckService.createDeck(USER_ID, publicDeckDto);

        List<DeckDto> result = deckService.getDecksByFilter(USER_ID, DeckVisibility.PUBLIC, DeckOwner.COMMUNITY);

        assertThat(result).hasSize(1);
        DeckDto dto = result.get(0);
        assertThat(dto.name()).isEqualTo("Publiczna talia");
        assertThat(dto.visibility()).isEqualTo(DeckVisibility.PUBLIC);
        assertThat(dto.ownerType()).isEqualTo(DeckOwner.COMMUNITY.name());
    }

    @Test
    @Transactional
    @Story("Statystyki talii")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Zwraca liczbę słówek w talii")
    @Description("Pobiera wordCount zapisane w bazie po wcześniejszej aktualizacji encji")
    void getTotalFlashcardsCount_shouldReturnPersistedWordCount() {
        deckService.createDeck(USER_ID, createDeckDto);
        Deck saved = deckRepository.findAll().get(0);
        saved.setWordCount(7);
        saved.setUpdatedAt(Instant.now());
        deckRepository.save(saved);

        long count = deckService.getTotalFlashcardsCount(saved.getId(), USER_ID);

        assertThat(count).isEqualTo(7);
    }

    @Test
    @Transactional
    @Story("Walidacja nazwy")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Sprawdza zajętość nazwy talii")
    @Description("Zwraca true dla istniejącej nazwy u właściciela i false dla innego użytkownika")
    void isDeckNameTaken_shouldReflectRepositoryState() {
        deckService.createDeck(USER_ID, createDeckDto);

        boolean takenForOwner = deckService.isDeckNameTaken(USER_ID, DECK_NAME);
        boolean takenForOther = deckService.isDeckNameTaken(OTHER_USER_ID, DECK_NAME);

        assertThat(takenForOwner).isTrue();
        assertThat(takenForOther).isFalse();
    }

    @Test
    @Transactional
    @Story("Usuwanie talii")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Usuwa talię i czyści bazę")
    @Description("Po usunięciu talii przez właściciela rekord znika z bazy danych")
    void deleteDeck_shouldRemoveFromDatabase() {
        deckService.createDeck(USER_ID, createDeckDto);
        Deck saved = deckRepository.findAll().get(0);

        deckService.deleteDeck(saved.getId(), USER_ID);

        Optional<Deck> deleted = deckRepository.findById(saved.getId());
        assertThat(deleted).isEmpty();
    }

    @Test
    @Transactional
    @Story("Usuwanie talii")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Blokuje usunięcie przez obcego użytkownika")
    @Description("Rzuca UserPermissionsMissing i nie usuwa rekordu gdy użytkownik nie jest właścicielem")
    void deleteDeck_shouldRejectForOtherUser() {
        deckService.createDeck(USER_ID, createDeckDto);
        Deck saved = deckRepository.findAll().get(0);

        assertThatThrownBy(() -> deckService.deleteDeck(saved.getId(), OTHER_USER_ID))
                .isInstanceOf(UserPermissionsMissing.class);

        assertThat(deckRepository.findAll()).hasSize(1);
        verifyNoInteractions(deckEnrollmentService);
    }
}
