package com.learnwords.deckservice.service.impl;

import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.enums.DeckVisibility;
import com.learnwords.deckservice.repository.DeckRepository;
import com.learnwords.deckservice.service.DeckShareService;
import com.learnwords.deckservice.service.grpcClient.UserGrcpClient;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@Epic("Decks")
@Feature("DeckAccessService")
@DisplayName("DeckAccessServiceImpl - testy jednostkowe")
class DeckAccessServiceImplTest {

    private static final String USER_ID = "user-123";
    private static final String OTHER_USER_ID = "user-456";
    private static final String DECK_ID = "deck-1";

    @Mock
    private UserGrcpClient userGrpcClient;
    @Mock
    private DeckRepository deckRepository;
    @Mock
    private DeckShareService deckShareService;

    @InjectMocks
    private DeckAccessServiceImpl deckAccessService;

    @Test
    @Story("Weryfikacja dostępu")
    @DisplayName("Dostęp dla właściciela")
    @Description("Zwraca true gdy użytkownik jest właścicielem talii")
    @Severity(SeverityLevel.CRITICAL)
    void canAccessDeck_shouldReturnTrue_whenUserIsOwner() {
        boolean result = deckAccessService.canAccessDeck(USER_ID, DECK_ID, USER_ID);

        assertThat(result).isTrue();
        verifyNoInteractions(deckRepository);
        verifyNoInteractions(deckShareService);
    }

    @Test
    @Story("Weryfikacja dostępu")
    @DisplayName("Dostęp do publicznej talii")
    @Description("Zwraca true gdy talia ma widoczność PUBLIC")
    @Severity(SeverityLevel.CRITICAL)
    void canAccessDeck_shouldReturnTrue_whenDeckIsPublic() {
        Deck publicDeck = Deck.builder()
                .id(DECK_ID)
                .visibility(DeckVisibility.PUBLIC)
                .build();

        when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(publicDeck));

        boolean result = deckAccessService.canAccessDeck(USER_ID, DECK_ID, OTHER_USER_ID);

        assertThat(result).isTrue();
        verify(deckShareService, never()).hasAccessToDeck(anyString(), anyString());
    }

    @Test
    @Story("Weryfikacja dostępu")
    @DisplayName("Dostęp przez udostępnienie")
    @Description("Deleguje sprawdzenie do DeckShareService dla prywatnej talii innego użytkownika")
    @Severity(SeverityLevel.CRITICAL)
    void canAccessDeck_shouldCheckShareService_whenDeckIsPrivateAndUserNotOwner() {
        Deck privateDeck = Deck.builder()
                .id(DECK_ID)
                .visibility(DeckVisibility.PRIVATE)
                .build();

        when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(privateDeck));
        when(deckShareService.hasAccessToDeck(USER_ID, DECK_ID)).thenReturn(true);

        boolean result = deckAccessService.canAccessDeck(USER_ID, DECK_ID, OTHER_USER_ID);

        assertThat(result).isTrue();
        verify(deckShareService).hasAccessToDeck(USER_ID, DECK_ID);
    }

    @Test
    @Story("Weryfikacja dostępu")
    @DisplayName("Brak dostępu")
    @Description("Zwraca false gdy talia prywatna i brak udostępnienia")
    @Severity(SeverityLevel.CRITICAL)
    void canAccessDeck_shouldReturnFalse_whenNoAccess() {
        Deck privateDeck = Deck.builder()
                .id(DECK_ID)
                .visibility(DeckVisibility.PRIVATE)
                .build();

        when(deckRepository.findById(DECK_ID)).thenReturn(Optional.of(privateDeck));
        when(deckShareService.hasAccessToDeck(USER_ID, DECK_ID)).thenReturn(false);

        boolean result = deckAccessService.canAccessDeck(USER_ID, DECK_ID, OTHER_USER_ID);

        assertThat(result).isFalse();
    }

    @Test
    @Story("Weryfikacja edycji")
    @DisplayName("Edycja dla właściciela")
    @Description("Zwraca true tylko gdy użytkownik jest właścicielem talii")
    @Severity(SeverityLevel.CRITICAL)
    void canEditDeck_shouldReturnTrue_whenUserIsOwner() {
        boolean result = deckAccessService.canEditDeck(USER_ID, USER_ID);
        assertThat(result).isTrue();
    }

    @Test
    @Story("Weryfikacja edycji")
    @DisplayName("Brak edycji dla innych")
    @Description("Zwraca false gdy użytkownik nie jest właścicielem")
    @Severity(SeverityLevel.NORMAL)
    void canEditDeck_shouldReturnFalse_whenUserIsNotOwner() {
        boolean result = deckAccessService.canEditDeck(USER_ID, OTHER_USER_ID);
        assertThat(result).isFalse();
    }
}
