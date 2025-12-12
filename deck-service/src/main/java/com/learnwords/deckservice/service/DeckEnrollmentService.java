package com.learnwords.deckservice.service;

import com.learnwords.deckservice.dto.course.FlashcardsWithStatus;
import com.learnwords.deckservice.dto.deckEnrollment.CreateDeckEnrollmentDto;
import com.learnwords.deckservice.dto.deckEnrollment.DeckEnrollmentDto;
import com.learnwords.deckservice.dto.dashboard.StudentMyCourseListItemDto;
import com.learnwords.deckservice.enums.LearnAlgorithm;
import com.learnwords.deckservice.enums.ReviewSchedule;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DeckEnrollmentService {
    void enrollUserToDeck(String userId, String deckId, CreateDeckEnrollmentDto createDeckEnrollmentDto);
    void unenrollUserFromDeck(String userId, String deckId);

    void updateLearnAlgorithm(String enrollmentId, String userId, LearnAlgorithm algorithm);
    void updateHowManyFlashcardsForOneSession(String enrollmentId, String userId, int limit);
    Page<StudentMyCourseListItemDto> getStudentEnrollments(String userId, int page, int size);
    DeckEnrollmentDto getEnrollment(String userId, String deckId);
    void updateReviewSchedule(String enrollmentId, String userId, ReviewSchedule schedule) ;

}
