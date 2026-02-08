package com.learnwords.deckservice.controller;

import com.learnwords.common.dto.WordDto;
import com.learnwords.deckservice.dto.flashcard.FlashcardDto;

import com.learnwords.deckservice.service.FlashcardService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1")
@Tag(
        name = "Flashcard Management",
        description = "API do zarządzania fiszkami w taliach"
)
public class FlashcardController {

    private static final String USER_ID_HEADER = "X-User-Id";

    private final FlashcardService flashcardService;

    public FlashcardController(FlashcardService flashcardService) {
        this.flashcardService = flashcardService;
    }

    /**
     * Dodanie istniejącego słowa jako fiszki do talii.
     */
    @PostMapping("/decks/{deckId}/flashcards")
    public ResponseEntity<Void> addFlashcardToDeck(
            @Parameter(description = "ID talii (kursu)", required = true, example = "deck-123")
            @PathVariable String deckId,
            @Parameter(description = "ID słowa, z którego ma powstać fiszka", required = true, example = "word-123")
            @RequestParam String wordId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Dodawanie fiszki do talii {} z wordId {} przez userId {}",
                deckId, wordId, userId);

        flashcardService.addFlashcardToDeck(deckId, wordId, userId);

        log.info("Dodano fiszkę do talii {} z wordId {} (userId: {})",
                deckId, wordId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Usunięcie fiszki z talii.
     */
    @DeleteMapping("/decks/{deckId}/flashcards/{flashcardId}")
    public ResponseEntity<Void> removeFlashcardFromDeck(
            @Parameter(description = "ID talii (kursu)", required = true, example = "deck-123")
            @PathVariable String deckId,
            @Parameter(description = "ID fiszki do usunięcia", required = true, example = "flashcard-123")
            @PathVariable String flashcardId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Usuwanie fiszki {} z talii {} przez userId {}",
                flashcardId, deckId, userId);

        flashcardService.removeFlashcardFromDeck(deckId, flashcardId, userId);

        log.info("Usunięto fiszkę {} z talii {} (userId: {})",
                flashcardId, deckId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Aktualizacja treści fiszki (np. edycja słowa/tłumaczeń).
     */
    @PutMapping("/flashcards/{flashcardId}")
    public ResponseEntity<Void> updateFlashcardContent(
            @Parameter(description = "ID fiszki do aktualizacji", required = true, example = "flashcard-123")
            @PathVariable String flashcardId,
            @Parameter(description = "Nowa treść słowa powiązanego z fiszką", required = true)
            @RequestBody WordDto newWord,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Aktualizacja treści fiszki {} przez userId {}: {}",
                flashcardId, userId, newWord);

        flashcardService.updateFlashcardContent(flashcardId, newWord, userId);

        log.info("Zaktualizowano treść fiszki {} (userId: {})", flashcardId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Pobranie pojedynczej fiszki po ID.
     */
    @GetMapping("/flashcards/{flashcardId}")
    public ResponseEntity<FlashcardDto> getFlashcardById(
            @Parameter(description = "ID fiszki", required = true, example = "flashcard-123")
            @PathVariable String flashcardId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Pobieranie fiszki {} przez userId {}", flashcardId, userId);

        FlashcardDto flashcard = flashcardService.getFlashcardById(flashcardId, userId);

        log.info("Pobrano fiszkę {} (userId: {})", flashcardId, userId);
        return ResponseEntity.ok(flashcard);
    }

    /**
     * Pobranie wszystkich fiszek z danej talii.
     */
    @GetMapping("/decks/{deckId}/flashcards")
    public ResponseEntity<List<FlashcardDto>> getAllFlashcardsFromDeck(
            @Parameter(description = "ID talii (kursu)", required = true, example = "deck-123")
            @PathVariable String deckId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Pobieranie wszystkich fiszek z talii {} dla userId {}", deckId, userId);

        List<FlashcardDto> flashcards = flashcardService.getAllFlashcardsFromDeck(deckId, userId);

        log.info("Pobrano {} fiszek z talii {} (userId: {})",
                flashcards.size(), deckId, userId);
        return ResponseEntity.ok(flashcards);
    }

    @GetMapping("/decks/{deckId}/flashcards/page")
    public ResponseEntity<Page<FlashcardDto>> getFlashcardsPageFromDeck(
            @Parameter(description = "ID talii (kursu)", required = true, example = "deck-123")
            @PathVariable String deckId,
            @Parameter(description = "Numer strony (0-indexed)", required = true, example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Rozmiar strony", required = true, example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Pobieranie strony {} (rozmiar {}) fiszek z talii {} dla userId {}",
                page, size, deckId, userId);

        Page<FlashcardDto> flashcardsPage = flashcardService.getFlashcardsFromDeckPaged(deckId, userId, page, size);

        log.info("Pobrano stronę {} fiszek z talii {} (userId: {})",
                page, deckId, userId);
        return ResponseEntity.ok(flashcardsPage);
    }

}
