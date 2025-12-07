package com.learnwords.statisticsservice.service;

import com.learnwords.statisticsservice.dto.StudentDashboardDto;
import com.learnwords.statisticsservice.dto.UserPointsDto;
import com.learnwords.statisticsservice.repository.DashboardRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final DashboardRepository dashboardRepository;

    public DashboardService(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    public StudentDashboardDto getStudentDashboard(String userId) {
        int activeDecks = dashboardRepository.getActiveDecks(userId);
        int completedLessons = dashboardRepository.getCompletedLessonsThisMonth(userId);
        int streakDays = dashboardRepository.getStreakDays(userId);
        UserPointsDto points = dashboardRepository.getUserPoints(userId);

        return new StudentDashboardDto(
                activeDecks,
                completedLessons,
                streakDays,
                points.totalPoints(),
                points.pointsThisWeek()
        );
    }
}
