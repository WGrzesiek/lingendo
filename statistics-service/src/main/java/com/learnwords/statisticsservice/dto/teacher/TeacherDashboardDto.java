package com.learnwords.statisticsservice.dto.teacher;

import java.util.List;

/**
 * DTO agregujące wszystkie dane dashboardu nauczyciela
 */
public record TeacherDashboardDto(
        TeacherDashboardStatsDto stats,
        List<TeacherStudentDto> topStudents,
        List<TeacherCourseDto> recentCourses,
        List<TeacherActivityItemDto> activityFeed
) {}
