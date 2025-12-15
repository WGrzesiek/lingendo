package com.learnwords.deckservice.controller;

import com.learnwords.deckservice.dto.facade.course.CourseHeaderInfo;
import com.learnwords.deckservice.dto.facade.course.CourseSettings;
import com.learnwords.deckservice.dto.facade.course.FlashcardsWithStatus;
import com.learnwords.deckservice.dto.deckEnrollment.DeckEnrollmentDto;
import com.learnwords.deckservice.dto.facade.dashboard.StudentMyCourseListItemDto;
import com.learnwords.deckservice.dto.deckEnrollment.CreateDeckEnrollmentDto;
import com.learnwords.deckservice.enums.LearnAlgorithm;
import com.learnwords.deckservice.enums.ReviewSchedule;
import com.learnwords.deckservice.facade.CourseViewFacade;
import com.learnwords.deckservice.service.DeckEnrollmentService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/decks")
@Tag(
        name = "Deck Enrollment Management",
        description = "API do zarządzania zapisami użytkownika na talie (kursy)"
)
public class DeckEnrollmentController {

    private static final String USER_ID_HEADER = "X-User-Id";

    private final DeckEnrollmentService deckEnrollmentService;
    private final CourseViewFacade courseViewFacade;

    public DeckEnrollmentController(DeckEnrollmentService deckEnrollmentService, CourseViewFacade courseViewFacade) {
        this.deckEnrollmentService = deckEnrollmentService;
        this.courseViewFacade = courseViewFacade;
    }

    /**
     * Zapis użytkownika na talię (kurs).
     */
    @PostMapping("/{deckId}/enrollments")
    public ResponseEntity<Void> enrollUserToDeck(
            @Parameter(description = "ID talii (kursu)", required = true, example = "deck-123")
            @PathVariable String deckId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId,
            @Parameter(description = "Dane konfiguracji zapisu na talię", required = true)
            @RequestBody CreateDeckEnrollmentDto createDeckEnrollmentDto
    ) {
        log.debug("Zapis użytkownika {} na talię {} z danymi: {}",
                userId, deckId, createDeckEnrollmentDto);

        deckEnrollmentService.enrollUserToDeck(userId, deckId, createDeckEnrollmentDto);

        log.info("Użytkownik {} został zapisany na talię {}", userId, deckId);
        return ResponseEntity.ok().build();
    }

    /**
     * Wypisanie użytkownika z talii (kursu).
     */
    @DeleteMapping("/{deckId}/enrollments")
    public ResponseEntity<Void> unenrollUserFromDeck(
            @Parameter(description = "ID talii (kursu)", required = true, example = "deck-123")
            @PathVariable String deckId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Wypisywanie użytkownika {} z talii {}", userId, deckId);

        deckEnrollmentService.unenrollUserFromDeck(userId, deckId);

        log.info("Użytkownik {} został wypisany z talii {}", userId, deckId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Zmiana algorytmu nauki dla konkretnego zapisu (enrollment).
     */
    @PutMapping("/enrollments/{enrollmentId}/algorithm")
    public ResponseEntity<Void> updateLearnAlgorithm(
            @Parameter(description = "ID zapisu na talię (enrollment)", required = true, example = "enrollment-123")
            @PathVariable String enrollmentId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId,
            @Parameter(description = "Nowy algorytm nauki", required = true, example = "GRZESIEK_ALGORITHM")
            @RequestParam LearnAlgorithm algorithm
    ) {
        log.debug("Aktualizacja algorytmu nauki na {} dla enrollment {} (userId: {})",
                algorithm, enrollmentId, userId);

        deckEnrollmentService.updateLearnAlgorithm(enrollmentId, userId, algorithm);

        log.info("Zaktualizowano algorytm nauki na {} dla enrollment {} (userId: {})",
                algorithm, enrollmentId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Zmiana limitu fiszek na jedną sesję dla zapisu (enrollment).
     */
    @PutMapping("/enrollments/{enrollmentId}/session-limit")
    public ResponseEntity<Void> updateHowManyFlashcardsForOneSession(
            @Parameter(description = "ID zapisu na talię (enrollment)", required = true, example = "enrollment-123")
            @PathVariable String enrollmentId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId,
            @Parameter(description = "Limit fiszek na jedną sesję", required = true, example = "20")
            @RequestParam int limit
    ) {
        log.debug("Aktualizacja limitu fiszek na {} dla enrollment {} (userId: {})",
                limit, enrollmentId, userId);

        deckEnrollmentService.updateHowManyFlashcardsForOneSession(enrollmentId, userId, limit);

        log.info("Zaktualizowano limit fiszek na {} dla enrollment {} (userId: {})",
                limit, enrollmentId, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Listowanie zapisów użytkownika (moje kursy).
     */
    @GetMapping("/enrollments/my")
    public ResponseEntity<Page<StudentMyCourseListItemDto>> getStudentEnrollments(
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId,
            @Parameter(description = "Numer strony (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Rozmiar strony", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        log.debug("Pobieranie zapisów (kursów) użytkownika {} - page: {}, size: {}", userId, page, size);
        Page<StudentMyCourseListItemDto> enrollments =
                deckEnrollmentService.getStudentEnrollments(userId, page, size);
        log.info("Pobrano {} zapisów dla użytkownika {} (page: {}, size: {})",
                enrollments.getNumberOfElements(), userId, page, size);
        return ResponseEntity.ok(enrollments);
    }

    /**
     * Pobranie szczegółów zapisu użytkownika na konkretną talię.
     */
    @GetMapping("/{deckId}/enrollment")
    public ResponseEntity<DeckEnrollmentDto> getEnrollment(
            @Parameter(description = "ID talii (kursu)", required = true, example = "deck-123")
            @PathVariable String deckId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Pobieranie zapisu użytkownika {} na talię {}", userId, deckId);
        DeckEnrollmentDto enrollment = deckEnrollmentService.getEnrollment(userId, deckId);
        log.info("Pobrano zapis użytkownika {} na talię {}", userId, deckId);
        return ResponseEntity.ok(enrollment);
    }

    /**
     * Pobranie fiszek + statusów użytkownika dla widoku kursu (course-view).
     */
    @GetMapping("/enrollments/{enrollmentId}/course-view")
    public ResponseEntity<Page<FlashcardsWithStatus>> getCourseView(
            @Parameter(description = "ID zapisu na talię (enrollment)", required = true, example = "enr-123")
            @PathVariable String enrollmentId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId,
            @Parameter(description = "Numer strony (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Rozmiar strony", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        log.debug("Pobieranie course-view dla enrollment {} (userId: {}, page: {}, size: {})",
                enrollmentId, userId, page, size);
        Page<FlashcardsWithStatus> result =
                courseViewFacade.getFlashcardsForCourseView(userId, enrollmentId, page, size);
        log.info("Pobrano {} fiszek dla enrollment {} (userId: {})",
                result.getContent().size(), enrollmentId, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{enrollmentId}/course-header")
    public CourseHeaderInfo getCourseHeader(
            @Parameter(description = "ID zapisu na talię (enrollment)", required = true, example = "enr-123")
            @PathVariable String enrollmentId,
            @Parameter(description = "ID użytkownika z nagłówka", required = true, example = "user-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        return courseViewFacade.getCourseHeaderInfo(enrollmentId, userId);
    }

    @GetMapping("/enrollment/{enrollmentId}/settings")
    public ResponseEntity<CourseSettings> getCourseSettings(
            @Parameter(description = "ID zapisu na talię (enrollment)", required = true, example = "enr-123")
            @PathVariable String enrollmentId,

            @Parameter(description = "ID użytkownika pobierany z nagłówka", required = true, example = "user-xyz-123")
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.debug("Pobieranie CourseSettings: enrollmentId={}, userId={}", enrollmentId, userId);

        CourseSettings settings = courseViewFacade.getCourseSettings(enrollmentId, userId);

        log.info("Zwrócono ustawienia kursu dla enrollmentId={} (userId={})", enrollmentId, userId);

        return ResponseEntity.ok(settings);
    }

    @PutMapping("/enrollments/{enrollmentId}/review-schedule")
    public ResponseEntity<Void> updateReviewSchedulePreference(
            @PathVariable String enrollmentId,
            @RequestParam("mode") ReviewSchedule schedule,
            @RequestHeader(USER_ID_HEADER) String userId
    ) {
        log.info("Żądanie zmiany harmonogramu powtórek: enrollmentId={}, userId={}, mode={}",
                enrollmentId, userId, schedule);

        deckEnrollmentService.updateReviewSchedule(enrollmentId, userId, schedule);

        return ResponseEntity.noContent().build();
    }

}
