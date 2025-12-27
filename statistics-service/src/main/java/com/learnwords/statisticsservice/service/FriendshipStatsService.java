package com.learnwords.statisticsservice.service;

import com.learnwords.statisticsservice.dto.friendship.FriendLeaderboardEntryDto;
import com.learnwords.statisticsservice.dto.friendship.FriendsStatsDto;
import com.learnwords.statisticsservice.repository.FriendshipStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendshipStatsService {

    private final FriendshipStatsRepository repository;

    public FriendsStatsDto getFriendsStats(String userId, LocalDate startDate, LocalDate endDate, int limit) {
        log.debug("Pobieranie statystyk znajomych dla userId={}", userId);
        
        int totalFriends = repository.countFriends(userId);
        List<FriendLeaderboardEntryDto> leaderboard = repository.getFriendsLeaderboard(userId, startDate, endDate, limit);
        
        return new FriendsStatsDto(totalFriends, leaderboard);
    }

    public List<FriendLeaderboardEntryDto> getLeaderboard(String userId, LocalDate startDate, LocalDate endDate, int limit) {
        return repository.getFriendsLeaderboard(userId, startDate, endDate, limit);
    }

    public int countFriends(String userId) {
        return repository.countFriends(userId);
    }
}
