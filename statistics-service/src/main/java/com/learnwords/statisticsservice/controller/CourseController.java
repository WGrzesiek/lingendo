package com.learnwords.statisticsservice.controller;

import com.learnwords.statisticsservice.dto.course.FlashcardAnswersStats;
import com.learnwords.statisticsservice.service.CourseService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
