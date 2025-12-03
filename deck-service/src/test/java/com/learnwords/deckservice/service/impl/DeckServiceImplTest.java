//package com.learnwords.deckservice.service.impl;
//
//import com.learnwords.deckservice.dto.*;
//import com.learnwords.deckservice.dto.Deck.CreateDeckDto;
//import com.learnwords.deckservice.dto.Deck.DeckDetailsDto;
//import com.learnwords.deckservice.dto.Deck.DeckDto;
//import com.learnwords.deckservice.entity.Deck;
//import com.learnwords.deckservice.enums.DeckOwner;
//import com.learnwords.deckservice.enums.Language;
//import com.learnwords.deckservice.enums.LearnAlgorithm;
//import com.learnwords.deckservice.enums.SessionStatus;
//import com.learnwords.deckservice.exception.exceptions.DeckNotFoundException;
//import com.learnwords.deckservice.exception.exceptions.DeckWithThisNameForThisUserAlreadyExistsException;
//import com.learnwords.deckservice.exception.exceptions.UserPermissionsMissing;
//import com.learnwords.deckservice.repository.DeckRepository;
//import com.learnwords.deckservice.repository.FlashcardRepository;
//import com.learnwords.deckservice.repository.SessionRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
///**
// * Testy jednostkowe dla {@link DeckServiceImpl}.
// *
// * <p>Klasa testowa obejmuje wszystkie metody serwisu zarządzającego taliami:
// * <ul>
// *   <li>Operacje CRUD (tworzenie, odczyt, aktualizacja, usuwanie)</li>
// *   <li>Filtrowanie i wyszukiwanie talii</li>
// *   <li>Zarządzanie ustawieniami talii (algorytm, widoczność, właściciel)</li>
// *   <li>Pobieranie statystyk i liczników</li>
// *   <li>Walidację uprawnień użytkownika</li>
// *   <li>Obsługę błędów i wyjątków</li>
// * </ul>
// *
// * <p>Testy wykorzystują Mockito do mockowania zależności (repozytoria)
// * i AssertJ do weryfikacji wyników.
// *
// * @author Grzegorz Wawrzeń
// * @version 1.0
// * @since 2025-11-13
// */
//@ExtendWith(MockitoExtension.class)
//@DisplayName("DeckServiceImpl - Testy jednostkowe")
//class DeckServiceImplTest {
//
//    @Mock
//    private DeckRepository deckRepository;
//
//    @Mock
//    private FlashcardRepository flashcardRepository;
//
//    @Mock
//    private SessionRepository sessionRepository;
//
//    @InjectMocks
//    private DeckServiceImpl deckService;
//
//    private static final String USER_ID = "user-123";
//    private static final String DECK_ID = "deck-456";
//    private static final String DECK_NAME = "English Vocabulary";
//    private static final String NEW_DECK_NAME = "Advanced English";
//
//    private Deck testDeck;
//    private CreateDeckDto createDeckDto;
//
//    @BeforeEach
//    void setUp() {
//        testDeck = Deck.builder()
//                .id(DECK_ID)
//                .name(DECK_NAME)
//                .description("Test deck description")
//                .userId(USER_ID)
//                .isPublic(false)
//                .owner(DeckOwner.I)
//                .wordCount(10)
//                .learnAlgorithm(LearnAlgorithm.GRZESIEK_ALGORITHM)
//                .languageFrom(Language.ENGLISH)
//                .languageTo(Language.POLISH)
//                .howManyFlashcardsForOneSession(20L)
//                .build();
//
//        createDeckDto = CreateDeckDto.builder()
//                .deckName(DECK_NAME)
//                .description("Test deck description")
//                .isPublic(false)
//                .learnAlgorithm(LearnAlgorithm.GRZESIEK_ALGORITHM)
//                .languageFrom(Language.ENGLISH)
//                .languageTo(Language.POLISH)
//                .howManyFlashcardsForOneSession(20L)
//                .build();
//    }
//
//    @Nested
//    @DisplayName("createDeck() - Tworzenie talii")
//    class CreateDeckTests {
//
//        @Test
//        @DisplayName("Powinien pomyślnie utworzyć nową talię")
//        void shouldCreateDeckSuccessfully() {
//            // given
//            when(deckRepository.existsByNameAndUserId(DECK_NAME, USER_ID)).thenReturn(false);
//
//            // when
//            deckService.createDeck(USER_ID, createDeckDto);
//
//            // then
//            ArgumentCaptor<Deck> deckCaptor = ArgumentCaptor.forClass(Deck.class);
//            verify(deckRepository).save(deckCaptor.capture());
//
//            Deck savedDeck = deckCaptor.getValue();
//            assertThat(savedDeck.getName()).isEqualTo(DECK_NAME);
//            assertThat(savedDeck.getUserId()).isEqualTo(USER_ID);
//            assertThat(savedDeck.getDescription()).isEqualTo("Test deck description");
//            assertThat(savedDeck.getLearnAlgorithm()).isEqualTo(LearnAlgorithm.GRZESIEK_ALGORITHM);
//            assertThat(savedDeck.getWordCount()).isZero();
//        }
//
//        @Test
//        @DisplayName("Powinien rzucić wyjątek gdy nazwa talii jest już zajęta")
//        void shouldThrowExceptionWhenDeckNameAlreadyExists() {
//            // given
//            when(deckRepository.existsByNameAndUserId(DECK_NAME, USER_ID)).thenReturn(true);
//
//            // when & then
//            assertThatThrownBy(() -> deckService.createDeck(USER_ID, createDeckDto))
//                    .isInstanceOf(DeckWithThisNameForThisUserAlreadyExistsException.class)
//                    .hasMessageContaining("Talia o tej nazwie już istnieje dla tego użytkownika");
//
//            verify(deckRepository, never()).save(any(Deck.class));
//        }
//    }
//
//    @Nested
//    @DisplayName("deleteDeck() - Usuwanie talii")
//    class DeleteDeckTests {
//
//        @Test
//        @DisplayName("Powinien pomyślnie usunąć talię")
//        void shouldDeleteDeckSuccessfully() {
//            // given
//            when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(testDeck));
//
//            // when
//            deckService.deleteDeck(DECK_ID, USER_ID);
//
//            // then
//            verify(deckRepository).deleteById(DECK_ID);
//        }
//
//        @Test
//        @DisplayName("Powinien rzucić wyjątek gdy talia nie istnieje")
//        void shouldThrowExceptionWhenDeckNotFound() {
//            // given
//            when(deckRepository.findById(DECK_ID)).thenReturn(Optional.empty());
//
//            // when & then
//            assertThatThrownBy(() -> deckService.deleteDeck(DECK_ID, USER_ID))
//                    .isInstanceOf(DeckNotFoundException.class)
//                    .hasMessageContaining("Talia o ID");
//
//            verify(deckRepository, never()).deleteById(anyString());
//        }
//
//        @Test
//        @DisplayName("Powinien rzucić wyjątek gdy użytkownik nie ma uprawnień")
//        void shouldThrowExceptionWhenUserHasNoPermissions() {
//            // given
//            when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(testDeck));
//
//            // when & then
//            assertThatThrownBy(() -> deckService.deleteDeck(DECK_ID, "other-user"))
//                    .isInstanceOf(UserPermissionsMissing.class)
//                    .hasMessageContaining("Użytkownik nie ma uprawnień do tej talii");
//
//            verify(deckRepository, never()).deleteById(anyString());
//        }
//    }
//
//    @Nested
//    @DisplayName("renameDeck() - Zmiana nazwy talii")
//    class RenameDeckTests {
//
//        @Test
//        @DisplayName("Powinien pomyślnie zmienić nazwę talii")
//        void shouldRenameDeckSuccessfully() {
//            // given
//            when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(testDeck));
//            when(deckRepository.existsByNameAndUserId(NEW_DECK_NAME, USER_ID)).thenReturn(false);
//            when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//            // when
//            String result = deckService.renameDeck(DECK_ID, NEW_DECK_NAME, USER_ID);
//
//            // then
//            assertThat(result).isEqualTo(NEW_DECK_NAME);
//            assertThat(testDeck.getName()).isEqualTo(NEW_DECK_NAME);
//            verify(deckRepository).save(testDeck);
//        }
//
//        @Test
//        @DisplayName("Powinien rzucić wyjątek gdy nowa nazwa jest już zajęta")
//        void shouldThrowExceptionWhenNewNameAlreadyExists() {
//            // given
//            when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(testDeck));
//            when(deckRepository.existsByNameAndUserId(NEW_DECK_NAME, USER_ID)).thenReturn(true);
//
//            // when & then
//            assertThatThrownBy(() -> deckService.renameDeck(DECK_ID, NEW_DECK_NAME, USER_ID))
//                    .isInstanceOf(DeckWithThisNameForThisUserAlreadyExistsException.class);
//
//            verify(deckRepository, never()).save(any(Deck.class));
//        }
//    }
//
//    @Nested
//    @DisplayName("changeDeckVisibility() - Zmiana widoczności")
//    class ChangeDeckVisibilityTests {
//
//        @Test
//        @DisplayName("Powinien zmienić talię na publiczną")
//        void shouldChangeDeckToPublic() {
//            // given
//            when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(testDeck));
//            when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//            // when
//            boolean result = deckService.changeDeckVisibility(DECK_ID, USER_ID, true);
//
//            // then
//            assertThat(result).isTrue();
//            assertThat(testDeck.isPublic()).isTrue();
//            verify(deckRepository).save(testDeck);
//        }
//
//        @Test
//        @DisplayName("Powinien zmienić talię na prywatną")
//        void shouldChangeDeckToPrivate() {
//            // given
//            testDeck.setPublic(true);
//            when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(testDeck));
//            when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//            // when
//            boolean result = deckService.changeDeckVisibility(DECK_ID, USER_ID, false);
//
//            // then
//            assertThat(result).isTrue();
//            assertThat(testDeck.isPublic()).isFalse();
//            verify(deckRepository).save(testDeck);
//        }
//    }
//
//    @Nested
//    @DisplayName("changeDeckOwner() - Zmiana właściciela")
//    class ChangeDeckOwnerTests {
//
//        @Test
//        @DisplayName("Powinien zmienić właściciela na TEACHER")
//        void shouldChangeOwnerToAdmin() {
//            // given
//            when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(testDeck));
//            when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//            // when
//            DeckOwner result = deckService.changeDeckOwner(DECK_ID, USER_ID, DeckOwner.TEACHER);
//
//            // then
//            assertThat(result).isEqualTo(DeckOwner.TEACHER);
//            assertThat(testDeck.getOwner()).isEqualTo(DeckOwner.TEACHER);
//            verify(deckRepository).save(testDeck);
//        }
//
//        @Test
//        @DisplayName("Powinien zmienić właściciela na COMMUNITY")
//        void shouldChangeOwnerToSystem() {
//            // given
//            when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(testDeck));
//            when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//            // when
//            DeckOwner result = deckService.changeDeckOwner(DECK_ID, USER_ID, DeckOwner.COMMUNITY);
//
//            // then
//            assertThat(result).isEqualTo(DeckOwner.COMMUNITY);
//            assertThat(testDeck.getOwner()).isEqualTo(DeckOwner.COMMUNITY);
//            verify(deckRepository).save(testDeck);
//        }
//    }
//
//    @Nested
//    @DisplayName("getDeckById() - Pobieranie talii")
//    class GetDeckByIdTests {
//
//        @Test
//        @DisplayName("Powinien pomyślnie pobrać talię")
//        void shouldGetDeckSuccessfully() {
//            // given
//            when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(testDeck));
//
//            // when
//            DeckDto result = deckService.getDeckById(DECK_ID, USER_ID);
//
//            // then
//            assertThat(result).isNotNull();
//            assertThat(result.id()).isEqualTo(DECK_ID);
//            assertThat(result.name()).isEqualTo(DECK_NAME);
//            assertThat(result.userId()).isEqualTo(USER_ID);
//            assertThat(result.ownerType()).isEqualTo(DeckOwner.I.name());
//            assertThat(result.wordCount()).isEqualTo(10);
//        }
//
//        @Test
//        @DisplayName("Powinien rzucić wyjątek gdy deckId jest pusty")
//        void shouldThrowExceptionWhenDeckIdIsEmpty() {
//            // when & then
//            assertThatThrownBy(() -> deckService.getDeckById("", USER_ID))
//                    .isInstanceOf(IllegalArgumentException.class)
//                    .hasMessageContaining("UserId lub DeckId nie może być pusty");
//        }
//
//        @Test
//        @DisplayName("Powinien rzucić wyjątek gdy userId jest pusty")
//        void shouldThrowExceptionWhenUserIdIsEmpty() {
//            // when & then
//            assertThatThrownBy(() -> deckService.getDeckById(DECK_ID, ""))
//                    .isInstanceOf(IllegalArgumentException.class)
//                    .hasMessageContaining("UserId lub DeckId nie może być pusty");
//        }
//    }
//
//    @Nested
//    @DisplayName("getDecksByFilter() - Filtrowanie talii")
//    class GetDecksByFilterTests {
//
//        @Test
//        @DisplayName("Powinien pobrać wszystkie talie użytkownika")
//        void shouldGetAllUserDecks() {
//            // given
//            List<Deck> decks = List.of(testDeck);
//            when(deckRepository.findByFilters(USER_ID, null, null)).thenReturn(decks);
//
//            // when
//            List<DeckDto> result = deckService.getDecksByFilter(USER_ID, null, null);
//
//            // then
//            assertThat(result).hasSize(1);
//            assertThat(result.get(0).id()).isEqualTo(DECK_ID);
//            assertThat(result.get(0).name()).isEqualTo(DECK_NAME);
//        }
//
//        @Test
//        @DisplayName("Powinien pobrać publiczne talie użytkownika")
//        void shouldGetPublicUserDecks() {
//            // given
//            testDeck.setPublic(true);
//            List<Deck> decks = List.of(testDeck);
//            when(deckRepository.findByFilters(USER_ID, true, null)).thenReturn(decks);
//
//            // when
//            List<DeckDto> result = deckService.getDecksByFilter(USER_ID, true, null);
//
//            // then
//            assertThat(result).hasSize(1);
//            assertThat(result.get(0).isPublic()).isTrue();
//        }
//
//        @Test
//        @DisplayName("Powinien pobrać talie według właściciela")
//        void shouldGetDecksByOwner() {
//            // given
//            List<Deck> decks = List.of(testDeck);
//            when(deckRepository.findByFilters(USER_ID, null, DeckOwner.I)).thenReturn(decks);
//
//            // when
//            List<DeckDto> result = deckService.getDecksByFilter(USER_ID, null, DeckOwner.I);
//
//            // then
//            assertThat(result).hasSize(1);
//            assertThat(result.get(0).ownerType()).isEqualTo(DeckOwner.I.name());
//        }
//
//        @Test
//        @DisplayName("Powinien rzucić wyjątek gdy userId jest null")
//        void shouldThrowExceptionWhenUserIdIsNull() {
//            // when & then
//            assertThatThrownBy(() -> deckService.getDecksByFilter(null, true, null))
//                    .isInstanceOf(IllegalArgumentException.class)
//                    .hasMessageContaining("UserId nie może być pusty");
//        }
//
//        @Test
//        @DisplayName("Powinien zwrócić pustą listę gdy nie ma talii")
//        void shouldReturnEmptyListWhenNoDecks() {
//            // given
//            when(deckRepository.findByFilters(USER_ID, null, null)).thenReturn(List.of());
//
//            // when
//            List<DeckDto> result = deckService.getDecksByFilter(USER_ID, null, null);
//
//            // then
//            assertThat(result).isEmpty();
//        }
//    }
//
//    @Nested
//    @DisplayName("getPublicDecks() - Pobieranie talii publicznych")
//    class GetPublicDecksTests {
//
//        @Test
//        @DisplayName("Powinien pobrać wszystkie publiczne talie")
//        void shouldGetAllPublicDecks() {
//            // given
//            Deck publicDeck1 = Deck.builder()
//                    .id("deck-1")
//                    .name("Public Deck 1")
//                    .userId("user-1")
//                    .isPublic(true)
//                    .owner(DeckOwner.I)
//                    .wordCount(5)
//                    .build();
//
//            Deck publicDeck2 = Deck.builder()
//                    .id("deck-2")
//                    .name("Public Deck 2")
//                    .userId("user-2")
//                    .isPublic(true)
//                    .owner(DeckOwner.I)
//                    .wordCount(10)
//                    .build();
//
//            when(deckRepository.findByIsPublic(true)).thenReturn(List.of(publicDeck1, publicDeck2));
//
//            // when
//            List<DeckDto> result = deckService.getPublicDecks();
//
//            // then
//            assertThat(result).hasSize(2);
//            assertThat(result).allMatch(DeckDto::isPublic);
//            verify(deckRepository).findByIsPublic(true);
//        }
//
//        @Test
//        @DisplayName("Powinien zwrócić pustą listę gdy nie ma publicznych talii")
//        void shouldReturnEmptyListWhenNoPublicDecks() {
//            // given
//            when(deckRepository.findByIsPublic(true)).thenReturn(List.of());
//
//            // when
//            List<DeckDto> result = deckService.getPublicDecks();
//
//            // then
//            assertThat(result).isEmpty();
//        }
//    }
//
//    @Nested
//    @DisplayName("getDeckDetailsById() - Pobieranie szczegółów talii")
//    class GetDeckDetailsByIdTests {
//
//        @Test
//        @DisplayName("Powinien pobrać szczegóły talii")
//        void shouldGetDeckDetails() {
//            // given
//            when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(testDeck));
//
//            // when
//            DeckDetailsDto result = deckService.getDeckDetailsById(DECK_ID, USER_ID);
//
//            // then
//            assertThat(result).isNotNull();
//            assertThat(result.getId()).isEqualTo(DECK_ID);
//            assertThat(result.getName()).isEqualTo(DECK_NAME);
//            assertThat(result.getDescription()).isEqualTo("Test deck description");
//            assertThat(result.getLearnAlgorithm()).isEqualTo(LearnAlgorithm.GRZESIEK_ALGORITHM);
//            assertThat(result.getLanguageFrom()).isEqualTo(Language.ENGLISH);
//            assertThat(result.getLanguageTo()).isEqualTo(Language.POLISH);
//        }
//    }
//
//    @Nested
//    @DisplayName("editDeckDetails() - Edycja szczegółów talii")
//    class EditDeckDetailsTests {
//
//        @Test
//        @DisplayName("Powinien zaktualizować szczegóły talii")
//        void shouldUpdateDeckDetails() {
//            // given
//            when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(testDeck));
//            when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//            DeckDetailsDto updateDto = DeckDetailsDto.builder()
//                    .name("Updated Name")
//                    .description("Updated description")
//                    .isPublic(true)
//                    .owner(DeckOwner.TEACHER)
//                    .learnAlgorithm(LearnAlgorithm.GRZESIEK_ALGORITHM)
//                    .howManyFlashcardsForOneSession(30L)
//                    .languageFrom(Language.POLISH)
//                    .languageTo(Language.ENGLISH)
//                    .build();
//
//            // when
//            DeckDetailsDto result = deckService.editDeckDetails(DECK_ID, updateDto, USER_ID);
//
//            // then
//            assertThat(result.getName()).isEqualTo("Updated Name");
//            assertThat(result.getDescription()).isEqualTo("Updated description");
//            assertThat(result.getIsPublic()).isTrue();
//            assertThat(result.getOwner()).isEqualTo(DeckOwner.TEACHER);
//            verify(deckRepository).save(testDeck);
//        }
//    }
//
//    @Nested
//    @DisplayName("getTotalFlashcardsCount() - Liczba fiszek")
//    class GetTotalFlashcardsCountTests {
//
//        @Test
//        @DisplayName("Powinien zwrócić liczbę fiszek w talii")
//        void shouldReturnFlashcardsCount() {
//            // given
//            when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(testDeck));
//
//            // when
//            long result = deckService.getTotalFlashcardsCount(DECK_ID, USER_ID);
//
//            // then
//            assertThat(result).isEqualTo(10);
//        }
//    }
//
//    @Nested
//    @DisplayName("updateLearnAlgorithm() - Aktualizacja algorytmu")
//    class UpdateLearnAlgorithmTests {
//
//        @Test
//        @DisplayName("Powinien zaktualizować algorytm nauki")
//        void shouldUpdateLearnAlgorithm() {
//            // given
//            when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(testDeck));
//            when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//            // when
//            String result = deckService.updateLearnAlgorithm(DECK_ID, LearnAlgorithm.GRZESIEK_ALGORITHM, USER_ID);
//
//            // then
//            assertThat(result).isEqualTo(LearnAlgorithm.GRZESIEK_ALGORITHM.name());
//            assertThat(testDeck.getLearnAlgorithm()).isEqualTo(LearnAlgorithm.GRZESIEK_ALGORITHM);
//            verify(deckRepository).save(testDeck);
//        }
//    }
//
//    @Nested
//    @DisplayName("updateFlashcardsPerSession() - Aktualizacja liczby fiszek na sesję")
//    class UpdateFlashcardsPerSessionTests {
//
//        @Test
//        @DisplayName("Powinien zaktualizować liczbę fiszek na sesję")
//        void shouldUpdateFlashcardsPerSession() {
//            // given
//            when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(testDeck));
//            when(deckRepository.save(any(Deck.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//            // when
//            Long result = deckService.updateFlashcardsPerSession(DECK_ID, 50L, USER_ID);
//
//            // then
//            assertThat(result).isEqualTo(50L);
//            assertThat(testDeck.getHowManyFlashcardsForOneSession()).isEqualTo(50L);
//            verify(deckRepository).save(testDeck);
//        }
//    }
//
//    @Nested
//    @DisplayName("getUserDeckCount() - Liczba talii użytkownika")
//    class GetUserDeckCountTests {
//
//        @Test
//        @DisplayName("Powinien zwrócić liczbę talii użytkownika")
//        void shouldReturnUserDeckCount() {
//            // given
//            when(deckRepository.countByUserId(USER_ID)).thenReturn(10L);
//            when(deckRepository.countByUserIdAndIsPublic(USER_ID, true)).thenReturn(3L);
//            when(deckRepository.countByUserIdAndIsPublic(USER_ID, false)).thenReturn(7L);
//
//            // when
//            UserDeckCountDto result = deckService.getUserDeckCount(USER_ID);
//
//            // then
//            assertThat(result.userId()).isEqualTo(USER_ID);
//            assertThat(result.totalDecks()).isEqualTo(10L);
//            assertThat(result.publicDecks()).isEqualTo(3L);
//            assertThat(result.privateDecks()).isEqualTo(7L);
//        }
//
//        @Test
//        @DisplayName("Powinien rzucić wyjątek gdy userId jest pusty")
//        void shouldThrowExceptionWhenUserIdIsEmpty() {
//            // when & then
//            assertThatThrownBy(() -> deckService.getUserDeckCount(""))
//                    .isInstanceOf(IllegalArgumentException.class)
//                    .hasMessageContaining("UserId nie może być pusty");
//        }
//    }
//
//    @Nested
//    @DisplayName("getDeckStatistics() - Statystyki talii")
//    class GetDeckStatisticsTests {
//
//        @Test
//        @DisplayName("Powinien zwrócić statystyki talii")
//        void shouldReturnDeckStatistics() {
//            // given
//            when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(testDeck));
//            when(flashcardRepository.countByDeckId(DECK_ID)).thenReturn(100L);
//            when(flashcardRepository.countByDeckIdAndIsLearned(DECK_ID, true)).thenReturn(75L);
//            when(sessionRepository.countByDeckId(DECK_ID)).thenReturn(20L);
//            when(sessionRepository.countByDeckIdAndStatus(DECK_ID, SessionStatus.COMPLETED)).thenReturn(15L);
//
//            // when
//            DeckStatisticsDto result = deckService.getDeckStatistics(DECK_ID, USER_ID);
//
//            // then
//            assertThat(result.getDeckId()).isEqualTo(DECK_ID);
//            assertThat(result.getDeckName()).isEqualTo(DECK_NAME);
//            assertThat(result.getTotalFlashcards()).isEqualTo(100);
//            assertThat(result.getLearnedFlashcards()).isEqualTo(75);
//            assertThat(result.getUnlearnedFlashcards()).isEqualTo(25);
//            assertThat(result.getProgressPercentage()).isEqualTo(75.0);
//            assertThat(result.getTotalSessions()).isEqualTo(20);
//            assertThat(result.getCompletedSessions()).isEqualTo(15);
//        }
//
//        @Test
//        @DisplayName("Powinien obliczyć 0% postępu gdy brak fiszek")
//        void shouldReturn0PercentWhenNoFlashcards() {
//            // given
//            when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(testDeck));
//            when(flashcardRepository.countByDeckId(DECK_ID)).thenReturn(0L);
//            when(flashcardRepository.countByDeckIdAndIsLearned(DECK_ID, true)).thenReturn(0L);
//            when(sessionRepository.countByDeckId(DECK_ID)).thenReturn(0L);
//            when(sessionRepository.countByDeckIdAndStatus(DECK_ID, SessionStatus.COMPLETED)).thenReturn(0L);
//
//            // when
//            DeckStatisticsDto result = deckService.getDeckStatistics(DECK_ID, USER_ID);
//
//            // then
//            assertThat(result.getProgressPercentage()).isZero();
//        }
//    }
//
//    @Nested
//    @DisplayName("isDeckNameTaken() - Walidacja nazwy")
//    class IsDeckNameTakenTests {
//
//        @Test
//        @DisplayName("Powinien zwrócić true gdy nazwa jest zajęta")
//        void shouldReturnTrueWhenNameIsTaken() {
//            // given
//            when(deckRepository.existsByNameAndUserId(DECK_NAME, USER_ID)).thenReturn(true);
//
//            // when
//            boolean result = deckService.isDeckNameTaken(USER_ID, DECK_NAME);
//
//            // then
//            assertThat(result).isTrue();
//        }
//
//        @Test
//        @DisplayName("Powinien zwrócić false gdy nazwa jest dostępna")
//        void shouldReturnFalseWhenNameIsAvailable() {
//            // given
//            when(deckRepository.existsByNameAndUserId(DECK_NAME, USER_ID)).thenReturn(false);
//
//            // when
//            boolean result = deckService.isDeckNameTaken(USER_ID, DECK_NAME);
//
//            // then
//            assertThat(result).isFalse();
//        }
//
//        @Test
//        @DisplayName("Powinien rzucić wyjątek gdy userId jest pusty")
//        void shouldThrowExceptionWhenUserIdIsEmpty() {
//            // when & then
//            assertThatThrownBy(() -> deckService.isDeckNameTaken("", DECK_NAME))
//                    .isInstanceOf(IllegalArgumentException.class)
//                    .hasMessageContaining("userId lub deckName nie może być pusty");
//        }
//
//        @Test
//        @DisplayName("Powinien rzucić wyjątek gdy deckName jest pusty")
//        void shouldThrowExceptionWhenDeckNameIsEmpty() {
//            // when & then
//            assertThatThrownBy(() -> deckService.isDeckNameTaken(USER_ID, ""))
//                    .isInstanceOf(IllegalArgumentException.class)
//                    .hasMessageContaining("userId lub deckName nie może być pusty");
//        }
//    }
//}
