package com.learnwords.statisticsservice.service;

import com.learnwords.statisticsservice.dto.leaderboard.LeaderboardEntryDto;
import com.learnwords.statisticsservice.dto.StudentActivityItemDto;
import com.learnwords.statisticsservice.dto.StudentDashboardStatsDto;
import com.learnwords.statisticsservice.dto.UserPointsDto;
import com.learnwords.statisticsservice.dto.leaderboard.LeaderboardOverviewDto;
import com.learnwords.statisticsservice.repository.DashboardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {
    private static final int LAST_ACTIVITY_LIMIT = 5;

    private final DashboardRepository dashboardRepository;

    public DashboardService(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    public StudentDashboardStatsDto getStudentDashboard(String userId) {
        int activeDecks = dashboardRepository.getActiveDecks(userId);
        int completedLessons = dashboardRepository.getCompletedLessonsThisMonth(userId);
        int streakDays = dashboardRepository.getStreakDays(userId);
        UserPointsDto points = dashboardRepository.getUserPoints(userId);

        return new StudentDashboardStatsDto(
                activeDecks,
                completedLessons,
                streakDays,
                points.totalPoints(),
                points.pointsThisWeek()
        );
    }

    public List<StudentActivityItemDto> getRecentActivity(String userId) {
        return dashboardRepository.getRecentActivity(userId, LAST_ACTIVITY_LIMIT);
    }

    public LeaderboardOverviewDto getLeaderboardOverview(String userId) {
        return dashboardRepository.getLeaderboardWithMyPosition(userId);
    }


}
