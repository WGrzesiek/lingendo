package com.learnwords.deckservice.controller;

import com.learnwords.deckservice.service.Session.FlashcardFetchStrategy.FlashcardFetchStrategy;
import com.learnwords.deckservice.service.Session.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/{deckId}/initializeSession")
    public ResponseEntity<String> initializeSession(@PathVariable String deckId, @RequestBody FlashcardFetchStrategy flashcardFetchStrategy) {

        String sessionId = sessionService.initializeSession(deckId, flashcardFetchStrategy);
        return ResponseEntity.ok(sessionId);
        }
    }
