package com.learnwords.statisticsservice.service;

import com.learnwords.statisticsservice.dto.course.DeckStudentsStats;
import com.learnwords.statisticsservice.dto.course.FlashcardAnswersStats;
import com.learnwords.statisticsservice.repository.DeckEnrollmentRepository;
import com.learnwords.statisticsservice.repository.FlashcardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.Tuple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CourseService {
    private final FlashcardRepository flashcardRepository;
    private final DeckEnrollmentRepository deckEnrollmentRepository;

    public CourseService(FlashcardRepository flashcardRepository, DeckEnrollmentRepository deckEnrollmentRepository) {
        this.flashcardRepository = flashcardRepository;
        this.deckEnrollmentRepository = deckEnrollmentRepository;
    }
    //Note dodac kiedys zabezpieczenie czy user ma dostep do danego enrollmentId
    public FlashcardAnswersStats getFlashcardAnswersStats(String enrollmentId) {
        log.debug("Pobieranie statystyk odpowiedzi fiszek dla enrollmentId: {}", enrollmentId);
        return flashcardRepository.getFlashcardAnswersStats(enrollmentId);
    }


    public Map<String, DeckStudentsStats> getTotalStudentsAndCompletedStudentsForDecks(List<String> deckIds) {
        Map<String, Long> totalStudents = deckEnrollmentRepository.getTotalStudentsForDecks(deckIds);
        Map<String, Long> completedStudents = deckEnrollmentRepository.getTotalCompletedStudentsForDecks(deckIds);

        Map<String, DeckStudentsStats> result = new HashMap<>();

        for (String deckId : deckIds) {
            result.put(deckId, new DeckStudentsStats(
                    totalStudents.getOrDefault(deckId, 0L),
                    completedStudents.getOrDefault(deckId, 0L)
            ));
        }

        return result;
    }


}
