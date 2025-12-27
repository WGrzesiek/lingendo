package com.learnwords.statisticsservice.service;

import com.learnwords.statisticsservice.dto.group.*;
import com.learnwords.statisticsservice.repository.GroupStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupStatisticsService {

    private static final int TOP_MEMBERS_LIMIT = 5;
    private static final int COURSES_LIMIT = 5;
    private static final int ACTIVITY_FEED_LIMIT = 10;
//    private static final int LEADERBOARD_LIMIT = 10;
//    private static final int LEADERBOARD_DAYS = 30;

    private final GroupStatisticsRepository repository;

    public GroupDashboardDto getGroupDashboard(String groupId) {
        log.debug("Pobieranie dashboardu dla grupy: {}", groupId);
        return new GroupDashboardDto(
            getGroupStats(groupId),
            getTopMembers(groupId),
            getSharedCourses(groupId),
            getActivityFeed(groupId)
        );
    }

    public GroupStatsDto getGroupStats(String groupId) {
        return repository.getGroupStats(groupId);
    }

    public List<GroupMemberDto> getTopMembers(String groupId) {
        return getTopMembers(groupId, TOP_MEMBERS_LIMIT);
    }

    public List<GroupMemberDto> getTopMembers(String groupId, int limit) {
        return repository.getTopMembers(groupId, limit);
    }

    public List<GroupMemberDto> getAllMembers(String groupId) {
        return repository.getAllMembers(groupId);
    }

    public List<GroupCourseDto> getSharedCourses(String groupId) {
        return getSharedCourses(groupId, COURSES_LIMIT);
    }

    public List<GroupCourseDto> getSharedCourses(String groupId, int limit) {
        return repository.getSharedCourses(groupId, limit);
    }

    public List<GroupActivityItemDto> getActivityFeed(String groupId) {
        return getActivityFeed(groupId, ACTIVITY_FEED_LIMIT);
    }

    public List<GroupActivityItemDto> getActivityFeed(String groupId, int limit) {
        return repository.getActivityFeed(groupId, limit);
    }

    public List<GroupLeaderboardEntryDto> getLeaderboard(String groupId, int days, int limit) {
        return repository.getLeaderboard(groupId, days, limit);
    }

    public List<GroupInfoDto> getTeacherGroups(String teacherId) {
        return repository.getTeacherGroups(teacherId);
    }
}
