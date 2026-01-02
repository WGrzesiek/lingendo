package com.learnwords.statisticsservice.repository;

import com.learnwords.statisticsservice.dto.teacher.TeacherActivityItemDto;
import com.learnwords.statisticsservice.dto.teacher.TeacherCourseDto;
import com.learnwords.statisticsservice.dto.teacher.TeacherDashboardStatsDto;
import com.learnwords.statisticsservice.dto.teacher.TeacherStatsDetailsDto;
import com.learnwords.statisticsservice.dto.teacher.TeacherStudentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class TeacherDashboardRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_STUDENT_SQL = """
        INSERT INTO analytics.teacher_students 
            (event_time, teacher_id, student_id, status)
        VALUES (now(), ?, ?, 'ACTIVE')
        """;

    private static final String INSERT_STUDENT_REMOVED_SQL = """
        INSERT INTO analytics.teacher_students 
            (event_time, teacher_id, student_id, status)
        VALUES (now(), ?, ?, 'REMOVED')
        """;

    private static final String INSERT_SHARED_DECK_SQL = """
        INSERT INTO analytics.teacher_shared_decks
            (event_time, teacher_id, deck_id, deck_name)
        VALUES (now(), ?, ?, ?)
        """;

    private static final String INSERT_STUDENT_ACTIVITY_SQL = """
        INSERT INTO analytics.teacher_student_activity
            (event_time, teacher_id, student_id, student_name, activity_type, deck_id, deck_name)
        VALUES (?, ?, ?, ?, 'NEW_STUDENT', '', '')
        """;

    private static final String SELECT_TOTAL_STUDENTS_SQL = """
        SELECT count(DISTINCT student_id)
        FROM analytics.teacher_students FINAL
        WHERE teacher_id = ? AND status = 'ACTIVE'
        """;

    private static final String SELECT_ACTIVE_STUDENTS_SQL = """
        SELECT count(DISTINCT ts.student_id)
        FROM analytics.teacher_students ts FINAL
        INNER JOIN analytics.user_activity ua ON ts.student_id = ua.user_id
        WHERE ts.teacher_id = ?
          AND ts.status = 'ACTIVE'
          AND ua.event_time >= toStartOfMonth(today())
        """;

    private static final String SELECT_SHARED_DECKS_SQL = """
        SELECT count(DISTINCT deck_id)
        FROM analytics.teacher_shared_decks FINAL
        WHERE teacher_id = ?
        """;

    private static final String SELECT_COMPLETED_LESSONS_SQL = """
        SELECT count()
        FROM analytics.sessions_finished sf
        INNER JOIN analytics.teacher_students ts FINAL ON sf.user_id = ts.student_id
        WHERE ts.teacher_id = ?
          AND ts.status = 'ACTIVE'
        """;

    private static final String SELECT_TOP_STUDENTS_SQL = """
        SELECT
            ts.student_id AS student_id,
            dictGet('analytics.usernames_dict', 'username', ts.student_id) AS student_name,
            coalesce(sum(upd.points), 0) AS total_points,
            max(tsa.event_time) AS last_active
        FROM analytics.teacher_students ts FINAL
        LEFT JOIN analytics.user_points_daily upd ON ts.student_id = upd.user_id
        LEFT JOIN analytics.teacher_student_activity tsa 
            ON ts.teacher_id = tsa.teacher_id 
           AND ts.student_id = tsa.student_id
        WHERE ts.teacher_id = ?
          AND ts.status = 'ACTIVE'
        GROUP BY ts.student_id
        ORDER BY total_points DESC
        LIMIT ?
        """;

    private static final String SELECT_TEACHER_COURSES_SQL = """
        SELECT
            tsd.deck_id AS deck_id,
            tsd.deck_name AS deck_name,
            count(DISTINCT tsa.student_id) AS students_count,
            max(tsa.event_time) AS last_activity
        FROM analytics.teacher_shared_decks tsd FINAL
        LEFT JOIN analytics.teacher_student_activity tsa 
            ON tsd.teacher_id = tsa.teacher_id
           AND tsd.deck_id = tsa.deck_id
        WHERE tsd.teacher_id = ?
        GROUP BY tsd.deck_id, tsd.deck_name
        ORDER BY last_activity DESC NULLS LAST
        LIMIT ?
        """;

    private static final String SELECT_ACTIVITY_FEED_SQL = """
        SELECT event_time, student_id, student_name, activity_type, deck_id, deck_name
        FROM analytics.teacher_student_activity
        WHERE teacher_id = ?
        ORDER BY event_time DESC
        LIMIT ?
        """;

    // === Szczegółowe statystyki nauczyciela ===
    
    private static final String SELECT_TEACHER_CREATED_DECKS_SQL = """
        SELECT count()
        FROM analytics.deck_created
        WHERE user_id = ?
        """;

    private static final String SELECT_TEACHER_CREATED_FLASHCARDS_SQL = """
        SELECT count()
        FROM analytics.flashcard_created
        WHERE user_id = ?
        """;

    private static final String SELECT_STUDENT_POINTS_SQL = """
        SELECT coalesce(sum(upd.points), 0)
        FROM analytics.user_points_daily upd
        INNER JOIN analytics.teacher_students ts FINAL ON upd.user_id = ts.student_id
        WHERE ts.teacher_id = ?
          AND ts.status = 'ACTIVE'
        """;

    private static final String SELECT_STUDENT_SESSIONS_SQL = """
        SELECT count()
        FROM analytics.sessions_finished sf
        INNER JOIN analytics.teacher_students ts FINAL ON sf.user_id = ts.student_id
        WHERE ts.teacher_id = ?
          AND ts.status = 'ACTIVE'
        """;

    private static final String SELECT_STUDENT_ANSWERS_SQL = """
        SELECT 
            coalesce(countIf(correct = 1), 0) AS correct_answers,
            count() AS total_answers
        FROM analytics.flashcard_answers fa
        INNER JOIN analytics.teacher_students ts FINAL ON fa.user_id = ts.student_id
        WHERE ts.teacher_id = ?
          AND ts.status = 'ACTIVE'
        """;

    private static final String SELECT_STUDENT_POINTS_PER_MONTH_SQL = """
        SELECT 
            formatDateTime(date, '%Y%m') AS year_month,
            sum(points) AS total_points
        FROM analytics.user_points_daily upd
        INNER JOIN analytics.teacher_students ts FINAL ON upd.user_id = ts.student_id
        WHERE ts.teacher_id = ?
          AND ts.status = 'ACTIVE'
          AND upd.date >= toStartOfMonth(today() - INTERVAL 11 MONTH)
        GROUP BY year_month
        ORDER BY year_month
        """;

    public void addStudent(String teacherId, String studentId) {
        jdbcTemplate.update(INSERT_STUDENT_SQL, teacherId, studentId);
    }

    public void removeStudent(String teacherId, String studentId) {
        jdbcTemplate.update(INSERT_STUDENT_REMOVED_SQL, teacherId, studentId);
    }

    public void addSharedDeck(String teacherId, String deckId, String deckName) {
        jdbcTemplate.update(INSERT_SHARED_DECK_SQL, teacherId, deckId, deckName);
    }

    public void addNewStudentActivity(String teacherId, String studentId, String studentName, Instant eventTime) {
        jdbcTemplate.update(
                INSERT_STUDENT_ACTIVITY_SQL,
                Timestamp.from(eventTime),
                teacherId,
                studentId,
                studentName
        );
    }

    public TeacherDashboardStatsDto getTeacherStats(String teacherId) {
        Integer totalStudentsRaw =
                jdbcTemplate.queryForObject(SELECT_TOTAL_STUDENTS_SQL, Integer.class, teacherId);
        Integer activeStudentsRaw =
                jdbcTemplate.queryForObject(SELECT_ACTIVE_STUDENTS_SQL, Integer.class, teacherId);
        Integer sharedDecksRaw =
                jdbcTemplate.queryForObject(SELECT_SHARED_DECKS_SQL, Integer.class, teacherId);
        Long completedLessonsRaw =
                jdbcTemplate.queryForObject(SELECT_COMPLETED_LESSONS_SQL, Long.class, teacherId);

        int totalStudents = totalStudentsRaw != null ? totalStudentsRaw : 0;
        int activeStudents = activeStudentsRaw != null ? activeStudentsRaw : 0;
        int sharedDecks = sharedDecksRaw != null ? sharedDecksRaw : 0;
        long completedLessons = completedLessonsRaw != null ? completedLessonsRaw : 0L;

        return new TeacherDashboardStatsDto(
                totalStudents,
                activeStudents,
                sharedDecks,
                completedLessons
        );
    }

    public List<TeacherStudentDto> getTopStudents(String teacherId, int limit) {
        return jdbcTemplate.query(
                SELECT_TOP_STUDENTS_SQL,
                (rs, rowNum) -> {
                    Timestamp lastActive = rs.getTimestamp("last_active");
                    return new TeacherStudentDto(
                            rs.getString("student_id"),
                            rs.getString("student_name"),
                            rs.getLong("total_points"),
                            lastActive != null ? lastActive.toInstant() : Instant.now()
                    );
                },
                teacherId, limit
        );
    }

    public List<TeacherCourseDto> getTeacherCourses(String teacherId, int limit) {
        return jdbcTemplate.query(
                SELECT_TEACHER_COURSES_SQL,
                (rs, rowNum) -> {
                    Timestamp lastActivity = rs.getTimestamp("last_activity");
                    return new TeacherCourseDto(
                            rs.getString("deck_id"),
                            rs.getString("deck_name"),
                            rs.getInt("students_count"),
                            lastActivity != null ? lastActivity.toInstant() : Instant.now()
                    );
                },
                teacherId, limit
        );
    }

    public List<TeacherActivityItemDto> getActivityFeed(String teacherId, int limit) {
        return jdbcTemplate.query(
                SELECT_ACTIVITY_FEED_SQL,
                (rs, rowNum) -> {
                    Timestamp eventTime = rs.getTimestamp("event_time");
                    return new TeacherActivityItemDto(
                            eventTime != null ? eventTime.toInstant() : Instant.now(),
                            rs.getString("student_id"),
                            rs.getString("student_name"),
                            rs.getString("activity_type"),
                            rs.getString("deck_id"),
                            rs.getString("deck_name")
                    );
                },
                teacherId, limit
        );
    }

    public TeacherStatsDetailsDto getTeacherStatsDetails(String teacherId) {
        // Utworzone kursy
        Integer createdDecksRaw = jdbcTemplate.queryForObject(
                SELECT_TEACHER_CREATED_DECKS_SQL, Integer.class, teacherId);
        int createdDecks = createdDecksRaw != null ? createdDecksRaw : 0;

        // Utworzone fiszki
        Integer createdFlashcardsRaw = jdbcTemplate.queryForObject(
                SELECT_TEACHER_CREATED_FLASHCARDS_SQL, Integer.class, teacherId);
        int createdFlashcards = createdFlashcardsRaw != null ? createdFlashcardsRaw : 0;

        // Suma punktów studentów
        Long studentPointsRaw = jdbcTemplate.queryForObject(
                SELECT_STUDENT_POINTS_SQL, Long.class, teacherId);
        long totalStudentPoints = studentPointsRaw != null ? studentPointsRaw : 0L;

        // Sesje ukończone przez studentów
        Long studentSessionsRaw = jdbcTemplate.queryForObject(
                SELECT_STUDENT_SESSIONS_SQL, Long.class, teacherId);
        long totalStudentSessions = studentSessionsRaw != null ? studentSessionsRaw : 0L;

        // Poprawne i wszystkie odpowiedzi
        long[] answers = jdbcTemplate.queryForObject(
                SELECT_STUDENT_ANSWERS_SQL,
                (rs, rowNum) -> new long[] {
                        rs.getLong("correct_answers"),
                        rs.getLong("total_answers")
                },
                teacherId
        );
        long totalCorrectAnswers = answers != null ? answers[0] : 0L;
        long totalAnswers = answers != null ? answers[1] : 0L;
        double averageAccuracy = totalAnswers > 0 
                ? Math.round((double) totalCorrectAnswers / totalAnswers * 1000) / 10.0 
                : 0.0;

        // Uczniowie
        Integer totalStudentsRaw = jdbcTemplate.queryForObject(
                SELECT_TOTAL_STUDENTS_SQL, Integer.class, teacherId);
        int totalStudents = totalStudentsRaw != null ? totalStudentsRaw : 0;

        Integer activeStudentsRaw = jdbcTemplate.queryForObject(
                SELECT_ACTIVE_STUDENTS_SQL, Integer.class, teacherId);
        int activeStudents = activeStudentsRaw != null ? activeStudentsRaw : 0;

        // Punkty na miesiąc
        Map<String, Long> pointsPerMonth = new HashMap<>();
        jdbcTemplate.query(
                SELECT_STUDENT_POINTS_PER_MONTH_SQL,
                (rs) -> {
                    pointsPerMonth.put(rs.getString("year_month"), rs.getLong("total_points"));
                },
                teacherId
        );

        return new TeacherStatsDetailsDto(
                createdDecks,
                createdFlashcards,
                totalStudentPoints,
                totalStudentSessions,
                averageAccuracy,
                activeStudents,
                totalStudents,
                totalCorrectAnswers,
                totalAnswers,
                pointsPerMonth
        );
    }
}
