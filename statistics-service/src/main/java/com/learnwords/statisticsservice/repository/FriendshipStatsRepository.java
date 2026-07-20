package com.learnwords.statisticsservice.repository;

import com.learnwords.common.events.FriendshipAcceptedEvent;
import com.learnwords.common.events.FriendshipRemovedEvent;
import com.learnwords.statisticsservice.dto.friendship.FriendEnrichedDto;
import com.learnwords.statisticsservice.dto.friendship.FriendLeaderboardEntryDto;
import com.learnwords.statisticsservice.dto.friendship.UserStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendshipStatsRepository {

    private final JdbcTemplate jdbcTemplate;

    public void saveFriendshipAccepted(FriendshipAcceptedEvent event) {
        jdbcTemplate.update("""
            INSERT INTO analytics.user_friendships (event_time, user_id, friend_id, status)
            VALUES (?, ?, ?, 'ACTIVE')
            """, Timestamp.from(event.eventTime()), event.userId1(), event.userId2());
        
        // odwrotna relacja
        jdbcTemplate.update("""
            INSERT INTO analytics.user_friendships (event_time, user_id, friend_id, status)
            VALUES (?, ?, ?, 'ACTIVE')
            """, Timestamp.from(event.eventTime()), event.userId2(), event.userId1());
    }

    public void saveFriendshipRemoved(FriendshipRemovedEvent event) {
        jdbcTemplate.update("""
            INSERT INTO analytics.user_friendships (event_time, user_id, friend_id, status)
            VALUES (now(), ?, ?, ?)
            """, event.userId1(), event.userId2(), event.reason());
        
        jdbcTemplate.update("""
            INSERT INTO analytics.user_friendships (event_time, user_id, friend_id, status)
            VALUES (now(), ?, ?, ?)
            """, event.userId2(), event.userId1(), event.reason());
    }

    public int countFriends(String userId) {
        Integer count = jdbcTemplate.queryForObject("""
            SELECT count(DISTINCT friend_id)
            FROM analytics.user_friendships
            WHERE user_id = ? AND status = 'ACTIVE'
            """, Integer.class, userId);
        return count != null ? count : 0;
    }

    public List<FriendLeaderboardEntryDto> getFriendsLeaderboard(String userId, LocalDate startDate, LocalDate endDate, int limit) {
        return jdbcTemplate.query("""
            SELECT
                friend_id,
                dictGet('analytics.usernames_dict', 'username', friend_id) AS friend_name,
                sum(sessions) AS total_sessions,
                sum(correct) AS total_correct,
                sum(total) AS total_answers,
                sum(points) AS total_points
            FROM analytics.friends_stats_daily
            WHERE user_id = ? AND day >= ? AND day <= ?
            GROUP BY friend_id
            ORDER BY total_points DESC
            LIMIT ?
            """,
            (rs, rowNum) -> new FriendLeaderboardEntryDto(
                rowNum + 1,
                rs.getString("friend_id"),
                rs.getString("friend_name"),
                rs.getLong("total_points"),
                rs.getInt("total_sessions"),
                rs.getInt("total_answers") > 0 
                    ? rs.getInt("total_correct") * 100.0 / rs.getInt("total_answers") 
                    : 0
            ),
            userId, startDate, endDate, limit
        );
    }

    public List<String> getActiveFriendIds(String userId) {
        return jdbcTemplate.queryForList("""
            SELECT DISTINCT friend_id
            FROM analytics.user_friendships
            WHERE user_id = ? AND status = 'ACTIVE'
            """, String.class, userId);
    }

    public UserStatsDto getUserStats(String targetUserId) {
        return jdbcTemplate.queryForObject("""
            WITH user_points AS (
                SELECT
                    sum(points) AS total_points,
                    sum(points) FILTER (WHERE day >= date_trunc('week', current_date)) AS weekly_points
                FROM analytics.user_points_daily
                WHERE user_id = ?
            ),
            user_sessions AS (
                SELECT
                    count(DISTINCT session_id) AS total_sessions
                FROM analytics.sessions_finished
                WHERE user_id = ?
            ),
            user_answers AS (
                SELECT
                    count(*) AS total_answers,
                    count(*) FILTER (WHERE correct = 1) AS total_correct
                FROM analytics.flashcard_answers
                WHERE user_id = ?
            ),
            user_rank AS (
                SELECT rank, username
                FROM analytics.leaderboard_snapshot
                WHERE user_id = ?
            ),
            user_streak AS (
                SELECT subtitle AS streak_info
                FROM analytics.user_activity
                WHERE type = 'LOGIN' AND user_id = ?
                ORDER BY event_time DESC
                LIMIT 1
            ),
            user_last_active AS (
                SELECT max(event_time) AS last_active
                FROM analytics.user_activity
                WHERE user_id = ?
            )
            SELECT
                coalesce(ur.username, '') AS username,
                coalesce(up.total_points, 0) AS total_points,
                coalesce(up.weekly_points, 0) AS weekly_points,
                coalesce(ur.rank, 0) AS global_rank,
                coalesce(us.total_sessions, 0) AS total_sessions,
                coalesce(ua.total_correct, 0) AS total_correct,
                coalesce(ua.total_answers, 0) AS total_answers,
                ust.streak_info,
                ula.last_active
            FROM user_points up
            CROSS JOIN user_sessions us
            CROSS JOIN user_answers ua
            LEFT JOIN user_rank ur ON 1=1
            LEFT JOIN user_streak ust ON 1=1
            LEFT JOIN user_last_active ula ON 1=1
            """,
            (rs, rowNum) -> {
                String streakInfo = rs.getString("streak_info");
                int streakDays = 0;
                if (streakInfo != null && !streakInfo.isEmpty()) {
                    String digits = streakInfo.replaceAll("\\D+", "");
                    if (!digits.isEmpty()) {
                        streakDays = Integer.parseInt(digits);
                    }
                }
                int totalCorrect = rs.getInt("total_correct");
                int totalAnswers = rs.getInt("total_answers");
                double accuracy = totalAnswers > 0 ? (totalCorrect * 100.0 / totalAnswers) : 0;
                
                java.sql.Timestamp lastActive = rs.getTimestamp("last_active");
                String lastActiveStr = lastActive != null ? lastActive.toInstant().toString() : null;
                
                return new UserStatsDto(
                    targetUserId,
                    rs.getString("username"),
                    rs.getLong("total_points"),
                    rs.getLong("weekly_points"),
                    rs.getInt("global_rank"),
                    rs.getInt("total_sessions"),
                    streakDays,
                    accuracy,
                    totalCorrect,
                    totalAnswers,
                    lastActiveStr
                );
            },
            targetUserId, targetUserId, targetUserId, targetUserId, targetUserId, targetUserId
        );
    }

    public List<FriendEnrichedDto> getFriendsEnriched(String userId) {
        return jdbcTemplate.query("""
            WITH active_friends AS (
                SELECT DISTINCT friend_id
                FROM analytics.user_friendships
                WHERE user_id = ? AND status = 'ACTIVE'
            ),
            friends_points AS (
                SELECT
                    user_id,
                    sum(points) AS total_points,
                    sum(points) FILTER (WHERE day >= date_trunc('week', current_date)) AS weekly_points
                FROM analytics.user_points_daily
                WHERE user_id IN (SELECT friend_id FROM active_friends)
                GROUP BY user_id
            ),
            friends_rank AS (
                SELECT user_id, rank, username
                FROM analytics.leaderboard_snapshot
                WHERE user_id IN (SELECT friend_id FROM active_friends)
            )
            SELECT
                af.friend_id,
                coalesce(fr.username, '') AS username,
                coalesce(fp.total_points, 0) AS total_points,
                coalesce(fp.weekly_points, 0) AS weekly_points,
                coalesce(fr.rank, 0) AS global_rank
            FROM active_friends af
            LEFT JOIN friends_points fp ON af.friend_id = fp.user_id
            LEFT JOIN friends_rank fr ON af.friend_id = fr.user_id
            ORDER BY total_points DESC
            """,
            (rs, rowNum) -> new FriendEnrichedDto(
                rs.getString("friend_id"),
                rs.getString("username"),
                rs.getLong("total_points"),
                rs.getLong("weekly_points"),
                rs.getInt("global_rank")
            ),
            userId
        );
    }
}
