package com.learnwords.statisticsservice.repository;

import com.learnwords.common.events.FriendshipAcceptedEvent;
import com.learnwords.common.events.FriendshipRemovedEvent;
import com.learnwords.statisticsservice.dto.friendship.FriendLeaderboardEntryDto;
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
            FROM analytics.user_friendships FINAL
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
            FROM analytics.user_friendships FINAL
            WHERE user_id = ? AND status = 'ACTIVE'
            """, String.class, userId);
    }
}
