package com.learnwords.statisticsservice.service;

import com.learnwords.statisticsservice.dto.course.FlashcardAnswersStats;
import com.learnwords.statisticsservice.repository.FlashcardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CourseService {
    private final FlashcardRepository flashcardRepository;

    public CourseService(FlashcardRepository flashcardRepository) {
        this.flashcardRepository = flashcardRepository;
    }
    //Note dodac kiedys zabezpieczenie czy user ma dostep do danego enrollmentId
    public FlashcardAnswersStats getFlashcardAnswersStats(String enrollmentId) {
        log.debug("Pobieranie statystyk odpowiedzi fiszek dla enrollmentId: {}", enrollmentId);
        return flashcardRepository.getFlashcardAnswersStats(enrollmentId);
    }
}
