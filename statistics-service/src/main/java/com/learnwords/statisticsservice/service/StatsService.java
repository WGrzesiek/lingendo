package com.learnwords.statisticsservice.service;

import com.learnwords.statisticsservice.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class StatsService {
    private final UserRepository userRepository;
    private final DeckEnrollmentRepository deckEnrollmentRepository;
    private final DeckRepository deckRepository;
    private final FlashcardRepository flashcardRepository;
    private final SessionRepository sessionRepository;

    public StatsService(UserRepository userRepository,
                        DeckEnrollmentRepository deckEnrollmentRepository,
                        DeckRepository deckRepository,
                        FlashcardRepository flashcardRepository,
                        SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.deckEnrollmentRepository = deckEnrollmentRepository;
        this.deckRepository = deckRepository;
        this.flashcardRepository = flashcardRepository;
        this.sessionRepository = sessionRepository;
    }

    public Map<String, Object> getUserStats(String userId) {
        Integer streak = userRepository.getUserStreak(userId);
        Long totalPoints = userRepository.getTotalPoints(userId);
        Integer createdDecks = deckRepository.getCreatedDecksCountByUser(userId);
        Long enrolledDecks = deckEnrollmentRepository.getDeckEnrollmentsCountByUser(userId);
        Long completedDecks = deckEnrollmentRepository.getDeckCompletionsCountByUser(userId);
        Integer sessionsCompleted = sessionRepository.getCompletedSessionsCountByUser(userId);
        Integer flashcardsCreated = flashcardRepository.getCreatedFlashcardsCountByUser(userId);
        Map<String, Integer> flashcardAnswersStats = flashcardRepository.getAnsweredFlashcardsCountByUser(userId);
        Map<String, Long> pointsPerMonth = userRepository.getPointsPerMonth(userId);
        Double averageAnswersPerSession = sessionRepository.getAverageAnswersPerSession(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("streak", streak);
        result.put("totalPoints", totalPoints);
        result.put("createdDecks", createdDecks);
        result.put("enrolledDecks", enrolledDecks);
        result.put("completedDecks", completedDecks);
        result.put("sessionsCompleted", sessionsCompleted);
        result.put("flashcardsCreated", flashcardsCreated);
        result.put("flashcardsAnswered", flashcardAnswersStats.get("answered_flashcards"));
        result.put("flashcardsAnsweredCorrectly", flashcardAnswersStats.get("correct_answers"));
        result.put("pointsPerMonth", pointsPerMonth);
        result.put("averageAnswersPerSession", averageAnswersPerSession);
        return result;
    }

    public Map<String, Object> getUserStats(String userId, Integer lastDays) {
        Integer streak = userRepository.getUserStreak(userId);
        Long totalPoints = userRepository.getTotalPoints(userId);
        Integer createdDecks = deckRepository.getCreatedDecksCountByUser(userId, lastDays);
        Long enrolledDecks = deckEnrollmentRepository.getDeckEnrollmentsCountByUser(userId, lastDays);
        Long completedDecks = deckEnrollmentRepository.getDeckCompletionsCountByUser(userId, lastDays);
        Integer sessionsCompleted = sessionRepository.getCompletedSessionsCountByUser(userId, lastDays);
        Integer flashcardsCreated = flashcardRepository.getCreatedFlashcardsCountByUser(userId, lastDays);
        Map<String, Integer> flashcardAnswersStats = flashcardRepository.getAnsweredFlashcardsCountByUser(userId, lastDays);
        Map<String, Long> pointsPerMonth = userRepository.getPointsPerMonth(userId);
        Double averageAnswersPerSession = sessionRepository.getAverageAnswersPerSession(userId, lastDays);

        Map<String, Object> result = new HashMap<>();
        result.put("streak", streak);
        result.put("totalPoints", totalPoints);
        result.put("createdDecks", createdDecks);
        result.put("enrolledDecks", enrolledDecks);
        result.put("completedDecks", completedDecks);
        result.put("sessionsCompleted", sessionsCompleted);
        result.put("flashcardsCreated", flashcardsCreated);
        result.put("flashcardsAnswered", flashcardAnswersStats.get("answered_flashcards"));
        result.put("flashcardsAnsweredCorrectly", flashcardAnswersStats.get("correct_answers"));
        result.put("pointsPerMonth", pointsPerMonth);
        result.put("averageAnswersPerSession", averageAnswersPerSession);
        return result;
    }

    public Map<String, Long> getPointsPerMonth(String userId) {
        return userRepository.getPointsPerMonth(userId);
    }

    public Map<String, Long> getPointsPerDay(String userId) {
        return userRepository.getPointsPerDay(userId);
    }
}
