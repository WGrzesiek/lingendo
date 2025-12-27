package com.learnwords.statisticsservice.controller;

import com.learnwords.statisticsservice.dto.friendship.FriendLeaderboardEntryDto;
import com.learnwords.statisticsservice.dto.friendship.FriendsStatsDto;
import com.learnwords.statisticsservice.service.FriendshipStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Kontroler do obsługi statystyk znajomych
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/stats/friends")
@RequiredArgsConstructor
@Tag(name = "Statystyki znajomych", description = "Endpointy do pobierania statystyk i porównań ze znajomymi")
public class FriendshipStatsController {

    private final FriendshipStatsService friendshipStatsService;

    @GetMapping
    @Operation(
            summary = "Pobierz statystyki znajomych",
            description = "Zwraca kompletne statystyki znajomych użytkownika w podanym okresie"
    )
    public ResponseEntity<FriendsStatsDto> getFriendsStats(
            @Parameter(description = "ID użytkownika", required = true)
            @RequestHeader("X-User-Id") String userId,
            
            @Parameter(description = "Data początkowa okresu (domyślnie: 7 dni wstecz)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "Data końcowa okresu (domyślnie: dziś)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            
            @Parameter(description = "Maksymalna liczba znajomych w rankingu")
            @RequestParam(defaultValue = "10") int limit
    ) {
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        LocalDate start = startDate != null ? startDate : end.minusDays(7);
        
        log.debug("GET /api/v1/stats/friends - userId={}, okres={} - {}", userId, start, end);
        
        FriendsStatsDto stats = friendshipStatsService.getFriendsStats(userId, start, end, limit);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/leaderboard")
    @Operation(
            summary = "Pobierz ranking znajomych",
            description = "Zwraca ranking znajomych według zdobytych punktów"
    )
    public ResponseEntity<List<FriendLeaderboardEntryDto>> getLeaderboard(
            @Parameter(description = "ID użytkownika", required = true)
            @RequestHeader("X-User-Id") String userId,
            
            @Parameter(description = "Data początkowa okresu")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "Data końcowa okresu")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            
            @Parameter(description = "Maksymalna liczba wyników")
            @RequestParam(defaultValue = "10") int limit
    ) {
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        LocalDate start = startDate != null ? startDate : end.minusDays(7);
        
        log.debug("GET /api/v1/stats/friends/leaderboard - userId={}", userId);
        
        List<FriendLeaderboardEntryDto> leaderboard = 
                friendshipStatsService.getLeaderboard(userId, start, end, limit);
        return ResponseEntity.ok(leaderboard);
    }
}
