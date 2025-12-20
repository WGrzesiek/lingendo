package com.learnwords.statisticsservice.controller;

import com.learnwords.statisticsservice.service.StatsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/stats")
@Tag(name = "Statistics Service", description = "API for statistics service")
public class StatsController {
    private static final String USER_ID_HEADER = "X-User-Id";
    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping()
    public ResponseEntity<Map<String, Object>> getStats(
            @RequestHeader(USER_ID_HEADER) String userId
    )
    {
        Map<String, Object> stats = statsService.getUserStats(userId);
        return ResponseEntity.ok(stats);
    }

}
