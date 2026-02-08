package com.learnwords.statisticsservice.service;

import com.learnwords.statisticsservice.dto.teacher.*;
import com.learnwords.statisticsservice.repository.TeacherDashboardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serwis do obsługi dashboardu nauczyciela
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherDashboardService {

    private static final int TOP_STUDENTS_LIMIT = 5;
    private static final int COURSES_LIMIT = 5;
    private static final int ACTIVITY_FEED_LIMIT = 10;

    private final TeacherDashboardRepository teacherDashboardRepository;

    public TeacherDashboardDto getTeacherDashboard(String teacherId) {
        log.debug("Pobieranie dashboardu dla nauczyciela: {}", teacherId);

        TeacherDashboardStatsDto stats = getTeacherStats(teacherId);
        List<TeacherStudentDto> topStudents = getTopStudents(teacherId);
        List<TeacherCourseDto> recentCourses = getRecentCourses(teacherId);
        List<TeacherActivityItemDto> activityFeed = getActivityFeed(teacherId);

        return new TeacherDashboardDto(stats, topStudents, recentCourses, activityFeed);
    }

    public TeacherDashboardStatsDto getTeacherStats(String teacherId) {
        log.debug("Pobieranie statystyk dla nauczyciela: {}", teacherId);
        return teacherDashboardRepository.getTeacherStats(teacherId);
    }

    public List<TeacherStudentDto> getTopStudents(String teacherId) {
        return getTopStudents(teacherId, TOP_STUDENTS_LIMIT);
    }

    public List<TeacherStudentDto> getTopStudents(String teacherId, int limit) {
        log.debug("Pobieranie top {} uczniów dla nauczyciela: {}", limit, teacherId);
        return teacherDashboardRepository.getTopStudents(teacherId, limit);
    }

    public List<TeacherStudentDto> getAllStudents(String teacherId) {
        log.debug("Pobieranie wszystkich uczniów dla nauczyciela: {}", teacherId);
        return teacherDashboardRepository.getTopStudents(teacherId, 1000);
    }

     public List<TeacherCourseDto> getRecentCourses(String teacherId) {
        return getRecentCourses(teacherId, COURSES_LIMIT);
    }

    public List<TeacherCourseDto> getRecentCourses(String teacherId, int limit) {
        log.debug("Pobieranie {} kursów dla nauczyciela: {}", limit, teacherId);
        return teacherDashboardRepository.getTeacherCourses(teacherId, limit);
    }

    public List<TeacherActivityItemDto> getActivityFeed(String teacherId) {
        return getActivityFeed(teacherId, ACTIVITY_FEED_LIMIT);
    }

    public List<TeacherActivityItemDto> getActivityFeed(String teacherId, int limit) {
        log.debug("Pobieranie {} aktywności dla nauczyciela: {}", limit, teacherId);
        return teacherDashboardRepository.getActivityFeed(teacherId, limit);
    }

    public TeacherStatsDetailsDto getTeacherStatsDetails(String teacherId) {
        log.debug("Pobieranie szczegółowych statystyk dla nauczyciela: {}", teacherId);
        return teacherDashboardRepository.getTeacherStatsDetails(teacherId);
    }
}
