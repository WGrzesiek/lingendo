package com.learnwords.statisticsservice.repository;

import com.learnwords.statisticsservice.dto.group.*;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GroupStatisticsRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_GROUP_SQL = """
        INSERT INTO analytics.groups
            (event_time, group_id, group_name, teacher_id, status)
        VALUES (?, ?, ?, ?, 'ACTIVE')
        """;

    private static final String INSERT_GROUP_MEMBER_SQL = """
        INSERT INTO analytics.group_members
            (event_time, group_id, student_id, teacher_id, status)
        VALUES (?, ?, ?, ?, 'ACTIVE')
        """;

    private static final String INSERT_GROUP_MEMBER_REMOVED_SQL = """
        INSERT INTO analytics.group_members
            (event_time, group_id, student_id, teacher_id, status)
        SELECT now(), group_id, student_id, teacher_id, 'REMOVED'
        FROM analytics.group_members
        WHERE group_id = ? AND student_id = ?
        LIMIT 1
        """;

    private static final String INSERT_GROUP_SHARED_DECK_SQL = """
        INSERT INTO analytics.group_shared_decks
            (event_time, group_id, deck_id, deck_name, teacher_id)
        VALUES (?, ?, ?, ?, ?)
        """;

    private static final String SELECT_GROUP_TOTAL_MEMBERS_SQL = """
        SELECT count(DISTINCT student_id)
        FROM analytics.group_members
        WHERE group_id = ? AND status = 'ACTIVE'
        """;

    private static final String SELECT_GROUP_ACTIVE_MEMBERS_SQL = """
        SELECT count(DISTINCT gm.student_id)
        FROM analytics.group_members gm
        INNER JOIN analytics.user_activity ua ON gm.student_id = ua.user_id
        WHERE gm.group_id = ? AND gm.status = 'ACTIVE'
          AND ua.event_time >= date_trunc('month', current_date)
        """;

    private static final String SELECT_GROUP_SHARED_DECKS_SQL = """
        SELECT count(DISTINCT deck_id)
        FROM analytics.group_shared_decks
        WHERE group_id = ?
        """;

    private static final String SELECT_GROUP_COMPLETED_LESSONS_SQL = """
        SELECT count(*)
        FROM analytics.sessions_finished sf
        INNER JOIN analytics.group_members gm ON sf.user_id = gm.student_id
        WHERE gm.group_id = ? AND gm.status = 'ACTIVE'
        """;

    private static final String SELECT_GROUP_TOTAL_POINTS_SQL = """
        SELECT sum(points)
        FROM analytics.group_leaderboard
        WHERE group_id = ?
        """;

    private static final String SELECT_GROUP_EXTENDED_STATS_SQL = """
        SELECT
            count(*) FILTER (WHERE fa.correct = 1) AS total_correct_answers,
            CAST(coalesce(sum(fa.time_taken_ms) / 60000, 0) AS bigint) AS total_study_time_minutes,
            count(DISTINCT fa.session_id) AS total_sessions,
            CASE WHEN count(*) > 0 THEN count(*) FILTER (WHERE fa.correct = 1) * 100.0 / count(*) ELSE 0 END AS average_accuracy
        FROM analytics.flashcard_answers fa
        INNER JOIN analytics.group_members gm ON fa.user_id = gm.student_id
        WHERE gm.group_id = ? AND gm.status = 'ACTIVE'
        """;

    private static final String SELECT_GROUP_AVG_WORDS_PER_DAY_SQL = """
        SELECT
            CASE WHEN count(DISTINCT (fa.event_time)::date) > 0 THEN count(*) * 1.0 / count(DISTINCT (fa.event_time)::date) ELSE 0 END AS avg_words_per_day
        FROM analytics.flashcard_answers fa
        INNER JOIN analytics.group_members gm ON fa.user_id = gm.student_id
        WHERE gm.group_id = ? AND gm.status = 'ACTIVE'
        """;

    private static final String SELECT_GROUP_TOP_MEMBERS_SQL = """
        SELECT
            gm.student_id AS student_id,
            (SELECT username FROM analytics.user_dim WHERE user_id = gm.student_id) AS student_name,
            coalesce(sum(gl.points), 0) AS total_points,
            max(ga.event_time) AS last_active
        FROM analytics.group_members gm
        LEFT JOIN analytics.group_leaderboard gl
            ON gm.group_id = gl.group_id
           AND gm.student_id = gl.student_id
        LEFT JOIN analytics.group_activity ga
            ON gm.group_id = ga.group_id
           AND gm.student_id = ga.student_id
        WHERE gm.group_id = ? AND gm.status = 'ACTIVE'
        GROUP BY gm.student_id
        ORDER BY total_points DESC
        LIMIT ?
        """;

    private static final String SELECT_GROUP_ACTIVITY_FEED_SQL = """
        SELECT event_time, student_id, student_name, activity_type, deck_id, deck_name
        FROM analytics.group_activity
        WHERE group_id = ?
        ORDER BY event_time DESC
        LIMIT ?
        """;

    private static final String SELECT_GROUP_LEADERBOARD_SQL = """
        SELECT
            fa.user_id AS student_id,
            (SELECT username FROM analytics.user_dim WHERE user_id = fa.user_id) AS student_name,
            count(*) FILTER (WHERE fa.correct = 1) AS correct_answers,
            count(DISTINCT fa.session_id) AS total_sessions,
            CASE WHEN count(*) > 0 THEN count(*) FILTER (WHERE fa.correct = 1) * 100.0 / count(*) ELSE 0 END AS accuracy
        FROM analytics.flashcard_answers fa
        INNER JOIN analytics.group_members gm ON fa.user_id = gm.student_id
        WHERE gm.group_id = ? AND gm.status = 'ACTIVE'
          AND fa.event_time >= current_date - ?
        GROUP BY fa.user_id
        ORDER BY correct_answers DESC
        LIMIT ?
        """;

    private static final String SELECT_GROUP_SHARED_COURSES_SQL = """
        SELECT
            gsd.deck_id AS deck_id,
            gsd.deck_name AS deck_name,
            count(DISTINCT ga.student_id) AS students_count,
            max(ga.event_time) AS last_activity
        FROM analytics.group_shared_decks gsd
        LEFT JOIN analytics.group_activity ga
            ON gsd.group_id = ga.group_id
           AND gsd.deck_id = ga.deck_id
        WHERE gsd.group_id = ?
        GROUP BY gsd.deck_id, gsd.deck_name
        ORDER BY last_activity DESC NULLS LAST
        LIMIT ?
        """;

    private static final String SELECT_TEACHER_GROUPS_SQL = """
        SELECT
            g.group_id AS group_id,
            g.group_name AS group_name,
            count(DISTINCT gm.student_id) AS member_count
        FROM analytics.groups g
        LEFT JOIN analytics.group_members gm
            ON g.group_id = gm.group_id
           AND gm.status = 'ACTIVE'
        WHERE g.teacher_id = ? AND g.status = 'ACTIVE'
        GROUP BY g.group_id, g.group_name
        """;


    public void createGroup(String groupId, String groupName, String teacherId, Instant createdAt) {
        jdbcTemplate.update(
                INSERT_GROUP_SQL,
                Timestamp.from(createdAt != null ? createdAt : Instant.now()),
                groupId,
                groupName,
                teacherId
        );
    }

    public void addMember(String groupId, String studentId, String teacherId, Instant joinedAt) {
        jdbcTemplate.update(
                INSERT_GROUP_MEMBER_SQL,
                Timestamp.from(joinedAt != null ? joinedAt : Instant.now()),
                groupId,
                studentId,
                teacherId
        );
    }

    public void removeMember(String groupId, String studentId) {
        jdbcTemplate.update(
                INSERT_GROUP_MEMBER_REMOVED_SQL,
                groupId,
                studentId
        );
    }

    public void addSharedDeck(String groupId, String deckId, String deckName, String teacherId, Instant sharedAt) {
        jdbcTemplate.update(
                INSERT_GROUP_SHARED_DECK_SQL,
                Timestamp.from(sharedAt != null ? sharedAt : Instant.now()),
                groupId,
                deckId,
                deckName,
                teacherId
        );
    }

    public GroupStatsDto getGroupStats(String groupId) {
        Integer totalMembersRaw =
                jdbcTemplate.queryForObject(SELECT_GROUP_TOTAL_MEMBERS_SQL, Integer.class, groupId);
        Integer activeMembersRaw =
                jdbcTemplate.queryForObject(SELECT_GROUP_ACTIVE_MEMBERS_SQL, Integer.class, groupId);
        Integer sharedDecksRaw =
                jdbcTemplate.queryForObject(SELECT_GROUP_SHARED_DECKS_SQL, Integer.class, groupId);
        Long completedLessonsRaw =
                jdbcTemplate.queryForObject(SELECT_GROUP_COMPLETED_LESSONS_SQL, Long.class, groupId);
        Long totalPointsRaw =
                jdbcTemplate.queryForObject(SELECT_GROUP_TOTAL_POINTS_SQL, Long.class, groupId);

        int totalMembers = totalMembersRaw != null ? totalMembersRaw : 0;
        int activeMembers = activeMembersRaw != null ? activeMembersRaw : 0;
        int sharedDecks = sharedDecksRaw != null ? sharedDecksRaw : 0;
        long completedLessons = completedLessonsRaw != null ? completedLessonsRaw : 0L;
        long totalPoints = totalPointsRaw != null ? totalPointsRaw : 0L;

        // Rozszerzone statystyki
        record ExtendedStats(long totalCorrectAnswers, long totalStudyTimeMinutes, long totalSessions, double averageAccuracy) {}
        ExtendedStats extendedStats = jdbcTemplate.queryForObject(
                SELECT_GROUP_EXTENDED_STATS_SQL,
                (rs, rowNum) -> new ExtendedStats(
                        rs.getLong("total_correct_answers"),
                        rs.getLong("total_study_time_minutes"),
                        rs.getLong("total_sessions"),
                        rs.getDouble("average_accuracy")
                ),
                groupId
        );

        Double avgWordsPerDayRaw = jdbcTemplate.queryForObject(
                SELECT_GROUP_AVG_WORDS_PER_DAY_SQL, Double.class, groupId);

        long totalWordsLearned = extendedStats != null ? extendedStats.totalCorrectAnswers() : 0L;
        long totalStudyTimeMinutes = extendedStats != null ? extendedStats.totalStudyTimeMinutes() : 0L;
        long totalSessions = extendedStats != null ? extendedStats.totalSessions() : 0L;
        double averageAccuracy = extendedStats != null ? extendedStats.averageAccuracy() : 0.0;
        double avgWordsPerDay = avgWordsPerDayRaw != null ? avgWordsPerDayRaw : 0.0;


        return new GroupStatsDto(
                totalMembers,
                activeMembers,
                sharedDecks,
                completedLessons,
                totalPoints,
                totalWordsLearned,
                totalStudyTimeMinutes,
                totalSessions,
                averageAccuracy,
                avgWordsPerDay
        );
    }

    public List<GroupMemberDto> getTopMembers(String groupId, int limit) {
        return jdbcTemplate.query(
                SELECT_GROUP_TOP_MEMBERS_SQL,
                (rs, rowNum) -> {
                    Timestamp lastActive = rs.getTimestamp("last_active");
                    return new GroupMemberDto(
                            rs.getString("student_id"),
                            rs.getString("student_name"),
                            rs.getLong("total_points"),
                            lastActive != null ? lastActive.toInstant() : Instant.now()
                    );
                },
                groupId, limit
        );
    }

    public List<GroupMemberDto> getAllMembers(String groupId) {
        return getTopMembers(groupId, 1000);
    }

    public List<GroupActivityItemDto> getActivityFeed(String groupId, int limit) {
        return jdbcTemplate.query(
                SELECT_GROUP_ACTIVITY_FEED_SQL,
                (rs, rowNum) -> {
                    Timestamp eventTime = rs.getTimestamp("event_time");
                    return new GroupActivityItemDto(
                            eventTime != null ? eventTime.toInstant() : Instant.now(),
                            rs.getString("student_id"),
                            rs.getString("student_name"),
                            rs.getString("activity_type"),
                            rs.getString("deck_id"),
                            rs.getString("deck_name")
                    );
                },
                groupId, limit
        );
    }

    public List<GroupLeaderboardEntryDto> getLeaderboard(String groupId, int days, int limit) {
        return jdbcTemplate.query(
                SELECT_GROUP_LEADERBOARD_SQL,
                (rs, rowNum) -> new GroupLeaderboardEntryDto(
                        rowNum + 1,
                        rs.getString("student_id"),
                        rs.getString("student_name"),
                        rs.getLong("correct_answers"),
                        rs.getInt("total_sessions"),
                        rs.getDouble("accuracy")
                ),
                groupId, days, limit
        );
    }

    public List<GroupCourseDto> getSharedCourses(String groupId, int limit) {
        return jdbcTemplate.query(
                SELECT_GROUP_SHARED_COURSES_SQL,
                (rs, rowNum) -> {
                    Timestamp lastActivity = rs.getTimestamp("last_activity");
                    return new GroupCourseDto(
                            rs.getString("deck_id"),
                            rs.getString("deck_name"),
                            rs.getInt("students_count"),
                            lastActivity != null ? lastActivity.toInstant() : Instant.now()
                    );
                },
                groupId, limit
        );
    }

    public List<GroupInfoDto> getTeacherGroups(String teacherId) {
        return jdbcTemplate.query(
                SELECT_TEACHER_GROUPS_SQL,
                (rs, rowNum) -> new GroupInfoDto(
                        rs.getString("group_id"),
                        rs.getString("group_name"),
                        rs.getInt("member_count")
                ),
                teacherId
        );
    }
}
