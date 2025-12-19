package com.learnwords.statisticsservice.controller;

import com.learnwords.statisticsservice.dto.course.DeckStatsRequest;
import com.learnwords.statisticsservice.dto.course.DeckStudentsStats;
import com.learnwords.statisticsservice.dto.course.FlashcardAnswersStats;
import com.learnwords.statisticsservice.service.CourseService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/courses")
@Tag(name = "Courses", description = "API do zarządzania kursami")
public class CourseController {
    private static final String USER_ID_HEADER = "X-User-Id";

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/{enrollmentId}/flashcards/stats")
    public ResponseEntity<FlashcardAnswersStats> getFlashcardAnswersStats(
            @Parameter(description = "ID talii (kursu)", required = true, example = "deck-123")
            @PathVariable String enrollmentId) {
        log.debug("Otrzymano żądanie pobrania statystyk odpowiedzi fiszek dla enrollmentId: {}", enrollmentId);
        FlashcardAnswersStats stats = courseService.getFlashcardAnswersStats(enrollmentId);
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/my-course/stats")
    public ResponseEntity<Map<String, DeckStudentsStats>> getStatsForMyDecks(
            @RequestHeader(USER_ID_HEADER) String userId,
            @RequestBody DeckStatsRequest request
    ) {
        if (request.deckIds() == null || request.deckIds().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // log: z jakich talii użytkownik chce statystyki
        log.info("Statystyki talii dla userId={} | deckIds={}", userId, request.deckIds());

        // pobierz statystyki z serwisu
        Map<String, DeckStudentsStats> stats =
                courseService.getTotalStudentsAndCompletedStudentsForDecks(request.deckIds());

//        // konwersja do listy DTO
//        List<DeckStatsResponse> response = stats.entrySet().stream()
//                .map(e -> new DeckStatsResponse(
//                        e.getKey(),
//                        e.getValue().totalStudents(),
//                        e.getValue().completedStudents()
//                ))
//                .toList();

        return ResponseEntity.ok(stats);
    }





}
