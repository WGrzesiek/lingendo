package com.learnwords.statisticsservice.dto.group;

/**
 * DTO podstawowych informacji o grupie.
 */
public record GroupInfoDto(
        String groupId,
        String groupName,
        int memberCount
) {}
