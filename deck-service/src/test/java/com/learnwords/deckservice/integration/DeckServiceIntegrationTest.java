//package com.learnwords.deckservice.integration;
//
//import com.learnwords.deckservice.dto.*;
//import com.learnwords.deckservice.dto.Deck.CreateDeckDto;
//import com.learnwords.deckservice.dto.Deck.DeckDetailsDto;
//import com.learnwords.deckservice.dto.Deck.DeckDto;
//import com.learnwords.deckservice.entity.Deck;
//import com.learnwords.deckservice.enums.DeckOwner;
//import com.learnwords.deckservice.enums.Language;
//import com.learnwords.deckservice.enums.LearnAlgorithm;
//import com.learnwords.deckservice.exception.exceptions.DeckNotFoundException;
//import com.learnwords.deckservice.exception.exceptions.DeckWithThisNameForThisUserAlreadyExistsException;
//import com.learnwords.deckservice.exception.exceptions.UserPermissionsMissing;
//import com.learnwords.deckservice.repository.DeckRepository;
//import com.learnwords.deckservice.repository.FlashcardRepository;
//import com.learnwords.deckservice.repository.SessionRepository;
//import com.learnwords.deckservice.service.DeckService;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.springframework.transaction.annotation.Transactional;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.*;
//
// /**
// * Testy integracyjne dla {@link DeckService}.
// *
// * <p>Testy uruchamiają pełny kontekst Spring Boot z prawdziwą bazą danych PostgreSQL
// * w kontenerze Docker (Testcontainers). Sprawdzają:
// * <ul>
// *   <li>Pełną ścieżkę: Service → Repository → Database</li>
// *   <li>Transakcje i rollbacki</li>
// *   <li>Mapowanie JPA/Hibernate</li>
// *   <li>Constrainty bazodanowe (unique, foreign keys)</li>
// *   <li>Kaskadowe operacje</li>
// *   <li>Izolację między testami</li>
// * </ul>
// *
// * <p>Wykorzystuje:
// * <ul>
// *   <li>{@link SpringBootTest} - pełny kontekst aplikacji</li>
// *   <li>{@link Testcontainers} - PostgreSQL w Dockerze</li>
// *   <li>{@link Transactional} - automatyczny rollback po testach</li>
// * </ul>
// *
// * @author Grzegorz Wawrzeń
// * @version 1.0
// * @since 2025-11-13
// */
//@SpringBootTest
//@Testcontainers
//@DisplayName("DeckService - Testy integracyjne")
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//class DeckServiceIntegrationTest {
//
//    @Container
//    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
//            .withDatabaseName("deck_test")
//            .withUsername("test")
//            .withPassword("test");
//
//    @DynamicPropertySource
//    static void configureProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", postgres::getJdbcUrl);
//        registry.add("spring.datasource.username", postgres::getUsername);
//        registry.add("spring.datasource.password", postgres::getPassword);
//        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
//    }
//
//    @Autowired
//    private DeckService deckService;
//
//    @Autowired
//    private DeckRepository deckRepository;
//
//    @Autowired
//    private FlashcardRepository flashcardRepository;
//
//    @Autowired
//    private SessionRepository sessionRepository;
//
//    private static final String USER_ID = "integration-user-123";
//    private static final String OTHER_USER_ID = "other-user-456";
//    private static final String DECK_NAME = "Integration Test Deck";
//
//    private CreateDeckDto createDeckDto;
//
//    @BeforeEach
//    void setUp() {
//        createDeckDto = CreateDeckDto.builder()
//                .deckName(DECK_NAME)
//                .description("Integration test deck description")
//                .isPublic(false)
//                .learnAlgorithm(LearnAlgorithm.GRZESIEK_ALGORITHM)
//                .languageFrom(Language.ENGLISH)
//                .languageTo(Language.POLISH)
//                .howManyFlashcardsForOneSession(20L)
//                .build();
//    }
//
//    @AfterEach
//    void cleanUp() {
//        // Czyszczenie bazy po każdym teście
//        deckRepository.deleteAll();
//    }
//
//    @Nested
//    @DisplayName("Tworzenie talii")
//    class CreateDeckTests {
//
//        @Test
//        @Transactional
//        @DisplayName("Powinien utworzyć talię i zapisać w bazie danych")
//        void shouldCreateDeckAndPersistToDatabase() {
//            // when
//            deckService.createDeck(USER_ID, createDeckDto);
//
//            // then
//            List<Deck> decks = deckRepository.findByUserId(USER_ID);
//            assertThat(decks).hasSize(1);
//
//            Deck savedDeck = decks.get(0);
//            assertThat(savedDeck.getName()).isEqualTo(DECK_NAME);
//            assertThat(savedDeck.getUserId()).isEqualTo(USER_ID);
//            assertThat(savedDeck.getDescription()).isEqualTo("Integration test deck description");
//            assertThat(savedDeck.getLearnAlgorithm()).isEqualTo(LearnAlgorithm.GRZESIEK_ALGORITHM);
//            assertThat(savedDeck.getWordCount()).isZero();
//            assertThat(savedDeck.isPublic()).isFalse();
//            assertThat(savedDeck.getId()).isNotNull();
//            assertThat(savedDeck.getCreatedAt()).isNotNull();
//        }
//
//        @Test
//        @Transactional
//        @DisplayName("Powinien rzucić wyjątek przy duplikacji nazwy dla tego samego użytkownika")
//        void shouldThrowExceptionWhenDuplicateDeckNameForSameUser() {
//            // given
//            deckService.createDeck(USER_ID, createDeckDto);
//
//            // when & then
//            assertThatThrownBy(() -> deckService.createDeck(USER_ID, createDeckDto))
//                    .isInstanceOf(DeckWithThisNameForThisUserAlreadyExistsException.class);
//
//            List<Deck> decks = deckRepository.findByUserId(USER_ID);
//            assertThat(decks).hasSize(1);
//        }
//
//        @Test
//        @Transactional
//        @DisplayName("Powinien pozwolić na utworzenie talii o tej samej nazwie dla innego użytkownika")
//        void shouldAllowSameDeckNameForDifferentUsers() {
//            // when
//            deckService.createDeck(USER_ID, createDeckDto);
//            deckService.createDeck(OTHER_USER_ID, createDeckDto);
//
//            // then
//            List<Deck> user1Decks = deckRepository.findByUserId(USER_ID);
//            List<Deck> user2Decks = deckRepository.findByUserId(OTHER_USER_ID);
//
//            assertThat(user1Decks).hasSize(1);
//            assertThat(user2Decks).hasSize(1);
//            assertThat(user1Decks.get(0).getName()).isEqualTo(user2Decks.get(0).getName());
//            assertThat(user1Decks.get(0).getId()).isNotEqualTo(user2Decks.get(0).getId());
//        }
//    }
//
//    @Nested
//    @DisplayName("Odczyt talii")
//    class GetDeckTests {
//
//        @Test
//        @Transactional
//        @DisplayName("Powinien pobrać talię po ID")
//        void shouldGetDeckById() {
//            // given
//            deckService.createDeck(USER_ID, createDeckDto);
//            Deck savedDeck = deckRepository.findByUserId(USER_ID).get(0);
//
//            // when
//            DeckDto result = deckService.getDeckById(savedDeck.getId(), USER_ID);
//
//            // then
//            assertThat(result).isNotNull();
//            assertThat(result.id()).isEqualTo(savedDeck.getId());
//            assertThat(result.name()).isEqualTo(DECK_NAME);
//            assertThat(result.userId()).isEqualTo(USER_ID);
//        }
//
//        @Test
//        @Transactional
//        @DisplayName("Powinien rzucić wyjątek gdy talia nie istnieje")
//        void shouldThrowExceptionWhenDeckNotFound() {
//            // when & then
//            assertThatThrownBy(() -> deckService.getDeckById("non-existent-id", USER_ID))
//                    .isInstanceOf(DeckNotFoundException.class);
//        }
//
//        @Test
//        @Transactional
//        @DisplayName("Powinien rzucić wyjątek gdy użytkownik nie ma uprawnień")
//        void shouldThrowExceptionWhenUserHasNoPermissions() {
//            // given
//            deckService.createDeck(USER_ID, createDeckDto);
//            Deck savedDeck = deckRepository.findByUserId(USER_ID).get(0);
//
//            // when & then
//            assertThatThrownBy(() -> deckService.getDeckById(savedDeck.getId(), OTHER_USER_ID))
//                    .isInstanceOf(UserPermissionsMissing.class);
//        }
//
//        @Test
//        @Transactional
//        @DisplayName("Powinien filtrować talie według widoczności")
//        void shouldFilterDecksByVisibility() {
//            // given
//            CreateDeckDto publicDeck = CreateDeckDto.builder()
//                    .deckName("Public Deck")
//                    .description("Public")
//                    .isPublic(true)
//                    .learnAlgorithm(LearnAlgorithm.GRZESIEK_ALGORITHM)
//                    .languageFrom(Language.ENGLISH)
//                    .languageTo(Language.POLISH)
//                    .howManyFlashcardsForOneSession(20L)
//                    .build();
//
//            deckService.createDeck(USER_ID, createDeckDto); // prywatna
//            deckService.createDeck(USER_ID, publicDeck);    // publiczna
//
//            // when
//            List<DeckDto> publicDecks = deckService.getDecksByFilter(USER_ID, true, null);
//            List<DeckDto> privateDecks = deckService.getDecksByFilter(USER_ID, false, null);
//
//            // then
//            assertThat(publicDecks).hasSize(1);
//            assertThat(publicDecks.get(0).isPublic()).isTrue();
//            assertThat(privateDecks).hasSize(1);
//            assertThat(privateDecks.get(0).isPublic()).isFalse();
//        }
//
//        @Test
//        @Transactional
//        @DisplayName("Powinien pobrać publiczne talie")
//        void shouldGetPublicDecks() {
//            // given
//            CreateDeckDto publicDeck1 = CreateDeckDto.builder()
//                    .deckName("Public Deck 1")
//                    .description("Public")
//                    .isPublic(true)
//                    .learnAlgorithm(LearnAlgorithm.GRZESIEK_ALGORITHM)
//                    .languageFrom(Language.ENGLISH)
//                    .languageTo(Language.POLISH)
//                    .howManyFlashcardsForOneSession(20L)
//                    .build();
//
//            CreateDeckDto publicDeck2 = CreateDeckDto.builder()
//                    .deckName("Public Deck 2")
//                    .description("Public")
//                    .isPublic(true)
//                    .learnAlgorithm(LearnAlgorithm.GRZESIEK_ALGORITHM)
//                    .languageFrom(Language.ENGLISH)
//                    .languageTo(Language.POLISH)
//                    .howManyFlashcardsForOneSession(20L)
//                    .build();
//
//            deckService.createDeck(USER_ID, createDeckDto);     // prywatna
//            deckService.createDeck(USER_ID, publicDeck1);
//            deckService.createDeck(OTHER_USER_ID, publicDeck2);
//
//            // when
//            List<DeckDto> publicDecks = deckService.getPublicDecks();
//
//            // then
//            assertThat(publicDecks).hasSize(2);
//            assertThat(publicDecks).allMatch(DeckDto::isPublic);
//        }
//    }
//
//    @Nested
//    @DisplayName("Aktualizacja talii")
//    class UpdateDeckTests {
//
//        @Test
//        @Transactional
//        @DisplayName("Powinien zmienić nazwę talii")
//        void shouldRenameDeck() {
//            // given
//            deckService.createDeck(USER_ID, createDeckDto);
//            Deck savedDeck = deckRepository.findByUserId(USER_ID).get(0);
//            String newName = "Updated Deck Name";
//
//            // when
//            String result = deckService.renameDeck(savedDeck.getId(), newName, USER_ID);
//
//            // then
//            assertThat(result).isEqualTo(newName);
//
//            Deck updatedDeck = deckRepository.findById(savedDeck.getId()).orElseThrow();
//            assertThat(updatedDeck.getName()).isEqualTo(newName);
//        }
//
//        @Test
//        @Transactional
//        @DisplayName("Powinien zmienić widoczność talii")
//        void shouldChangeDeckVisibility() {
//            // given
//            deckService.createDeck(USER_ID, createDeckDto);
//            Deck savedDeck = deckRepository.findByUserId(USER_ID).get(0);
//
//            // when
//            boolean result = deckService.changeDeckVisibility(savedDeck.getId(), USER_ID, true);
//
//            // then
//            assertThat(result).isTrue();
//
//            Deck updatedDeck = deckRepository.findById(savedDeck.getId()).orElseThrow();
//            assertThat(updatedDeck.isPublic()).isTrue();
//        }
//
//        @Test
//        @Transactional
//        @DisplayName("Powinien zmienić właściciela talii")
//        void shouldChangeDeckOwner() {
//            // given
//            deckService.createDeck(USER_ID, createDeckDto);
//            Deck savedDeck = deckRepository.findByUserId(USER_ID).get(0);
//
//            // when
//            DeckOwner result = deckService.changeDeckOwner(savedDeck.getId(), USER_ID, DeckOwner.TEACHER);
//
//            // then
//            assertThat(result).isEqualTo(DeckOwner.TEACHER);
//
//            Deck updatedDeck = deckRepository.findById(savedDeck.getId()).orElseThrow();
//            assertThat(updatedDeck.getOwner()).isEqualTo(DeckOwner.TEACHER);
//        }
//
//        @Test
//        @Transactional
//        @DisplayName("Powinien zaktualizować algorytm nauki")
//        void shouldUpdateLearnAlgorithm() {
//            // given
//            deckService.createDeck(USER_ID, createDeckDto);
//            Deck savedDeck = deckRepository.findByUserId(USER_ID).get(0);
//
//            // when
//            String result = deckService.updateLearnAlgorithm(savedDeck.getId(), LearnAlgorithm.GRZESIEK_ALGORITHM, USER_ID);
//
//            // then
//            assertThat(result).isEqualTo("GRZESIEK");
//
//            Deck updatedDeck = deckRepository.findById(savedDeck.getId()).orElseThrow();
//            assertThat(updatedDeck.getLearnAlgorithm()).isEqualTo(LearnAlgorithm.GRZESIEK_ALGORITHM);
//        }
//
//        @Test
//        @Transactional
//        @DisplayName("Powinien zaktualizować liczbę fiszek na sesję")
//        void shouldUpdateFlashcardsPerSession() {
//            // given
//            deckService.createDeck(USER_ID, createDeckDto);
//            Deck savedDeck = deckRepository.findByUserId(USER_ID).get(0);
//
//            // when
//            Long result = deckService.updateFlashcardsPerSession(savedDeck.getId(), 50L, USER_ID);
//
//            // then
//            assertThat(result).isEqualTo(50L);
//
//            Deck updatedDeck = deckRepository.findById(savedDeck.getId()).orElseThrow();
//            assertThat(updatedDeck.getHowManyFlashcardsForOneSession()).isEqualTo(50L);
//        }
//
//        @Test
//        @Transactional
//        @DisplayName("Powinien zaktualizować szczegóły talii")
//        void shouldUpdateDeckDetails() {
//            // given
//            deckService.createDeck(USER_ID, createDeckDto);
//            Deck savedDeck = deckRepository.findByUserId(USER_ID).get(0);
//
//            DeckDetailsDto updateDto = DeckDetailsDto.builder()
//                    .name("Updated Name")
//                    .description("Updated Description")
//                    .isPublic(true)
//                    .owner(DeckOwner.TEACHER)
//                    .learnAlgorithm(LearnAlgorithm.GRZESIEK_ALGORITHM)
//                    .howManyFlashcardsForOneSession(30L)
//                    .languageFrom(Language.POLISH)
//                    .languageTo(Language.ENGLISH)
//                    .build();
//
//            // when
//            DeckDetailsDto result = deckService.editDeckDetails(savedDeck.getId(), updateDto, USER_ID);
//
//            // then
//            assertThat(result.getName()).isEqualTo("Updated Name");
//            assertThat(result.getDescription()).isEqualTo("Updated Description");
//            assertThat(result.getIsPublic()).isTrue();
//
//            Deck updatedDeck = deckRepository.findById(savedDeck.getId()).orElseThrow();
//            assertThat(updatedDeck.getName()).isEqualTo("Updated Name");
//            assertThat(updatedDeck.getDescription()).isEqualTo("Updated Description");
//            assertThat(updatedDeck.isPublic()).isTrue();
//            assertThat(updatedDeck.getOwner()).isEqualTo(DeckOwner.TEACHER);
//        }
//    }
//
//    @Nested
//    @DisplayName("Usuwanie talii")
//    class DeleteDeckTests {
//
//        @Test
//        @Transactional
//        @DisplayName("Powinien usunąć talię z bazy danych")
//        void shouldDeleteDeckFromDatabase() {
//            // given
//            deckService.createDeck(USER_ID, createDeckDto);
//            Deck savedDeck = deckRepository.findByUserId(USER_ID).get(0);
//            String deckId = savedDeck.getId();
//
//            // when
//            deckService.deleteDeck(deckId, USER_ID);
//
//            // then
//            assertThat(deckRepository.findById(deckId)).isEmpty();
//            assertThat(deckRepository.findByUserId(USER_ID)).isEmpty();
//        }
//
//        @Test
//        @Transactional
//        @DisplayName("Powinien rzucić wyjątek przy próbie usunięcia nieistniejącej talii")
//        void shouldThrowExceptionWhenDeletingNonExistentDeck() {
//            // when & then
//            assertThatThrownBy(() -> deckService.deleteDeck("non-existent-id", USER_ID))
//                    .isInstanceOf(DeckNotFoundException.class);
//        }
//    }
//
//    @Nested
//    @DisplayName("Statystyki i liczniki")
//    class StatisticsTests {
//
//        @Test
//        @Transactional
//        @DisplayName("Powinien zwrócić liczbę talii użytkownika")
//        void shouldReturnUserDeckCount() {
//            // given
//            CreateDeckDto deck2 = CreateDeckDto.builder()
//                    .deckName("Deck 2")
//                    .description("Second deck")
//                    .isPublic(true)
//                    .learnAlgorithm(LearnAlgorithm.GRZESIEK_ALGORITHM)
//                    .languageFrom(Language.ENGLISH)
//                    .languageTo(Language.POLISH)
//                    .howManyFlashcardsForOneSession(20L)
//                    .build();
//
//            CreateDeckDto deck3 = CreateDeckDto.builder()
//                    .deckName("Deck 3")
//                    .description("Third deck")
//                    .isPublic(false)
//                    .learnAlgorithm(LearnAlgorithm.GRZESIEK_ALGORITHM)
//                    .languageFrom(Language.ENGLISH)
//                    .languageTo(Language.POLISH)
//                    .howManyFlashcardsForOneSession(20L)
//                    .build();
//
//            deckService.createDeck(USER_ID, createDeckDto); // prywatna
//            deckService.createDeck(USER_ID, deck2);         // publiczna
//            deckService.createDeck(USER_ID, deck3);         // prywatna
//
//            // when
//            UserDeckCountDto result = deckService.getUserDeckCount(USER_ID);
//
//            // then
//            assertThat(result.userId()).isEqualTo(USER_ID);
//            assertThat(result.totalDecks()).isEqualTo(3);
//            assertThat(result.publicDecks()).isEqualTo(1);
//            assertThat(result.privateDecks()).isEqualTo(2);
//        }
//
//        @Test
//        @Transactional
//        @DisplayName("Powinien zwrócić 0 dla użytkownika bez talii")
//        void shouldReturnZeroForUserWithoutDecks() {
//            // when
//            UserDeckCountDto result = deckService.getUserDeckCount("user-without-decks");
//
//            // then
//            assertThat(result.totalDecks()).isZero();
//            assertThat(result.publicDecks()).isZero();
//            assertThat(result.privateDecks()).isZero();
//        }
//
//        @Test
//        @Transactional
//        @DisplayName("Powinien sprawdzić czy nazwa talii jest zajęta")
//        void shouldCheckIfDeckNameIsTaken() {
//            // given
//            deckService.createDeck(USER_ID, createDeckDto);
//
//            // when
//            boolean isTaken = deckService.isDeckNameTaken(USER_ID, DECK_NAME);
//            boolean isAvailable = deckService.isDeckNameTaken(USER_ID, "Available Name");
//
//            // then
//            assertThat(isTaken).isTrue();
//            assertThat(isAvailable).isFalse();
//        }
//    }
//
//    @Nested
//    @DisplayName("Transakcje i rollback")
//    class TransactionTests {
//
//        @Test
//        @DisplayName("Powinien wykonać rollback przy błędzie")
//        void shouldRollbackOnError() {
//            // given
//            long initialCount = deckRepository.count();
//
//            // when & then
//            assertThatThrownBy(() -> {
//                deckService.createDeck(USER_ID, createDeckDto);
//                deckService.createDeck(USER_ID, createDeckDto); // duplikat - rzuci wyjątek
//            }).isInstanceOf(DeckWithThisNameForThisUserAlreadyExistsException.class);
//
//            // Sprawdzenie czy rollback się wykonał
//            long finalCount = deckRepository.count();
//            assertThat(finalCount).isEqualTo(initialCount + 1); // tylko pierwsza się zapisała
//        }
//    }
//}
