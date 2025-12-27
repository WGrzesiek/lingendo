package com.learnwords.statisticsservice.dto.group;

import java.util.List;

/**
 * Kompletny DTO dashboardu grupy.
 */
public record GroupDashboardDto(
        GroupStatsDto stats,
        List<GroupMemberDto> topMembers,
        List<GroupCourseDto> sharedCourses,
        List<GroupActivityItemDto> activityFeed
) {}
