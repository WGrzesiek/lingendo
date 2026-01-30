package com.learnwords.userservice.service;

import com.learnwords.userservice.dtos.friendship.*;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Serwis do zarządzania relacjami przyjaźni między użytkownikami
 */
public interface FriendshipService {
    FriendRequestResponse sendFriendRequest(String userId, String targetUserId);
    FriendResponse acceptFriendRequest(String userId, String friendshipId);
    void rejectFriendRequest(String userId, String friendshipId);
    Page<FriendRequestResponse> getPendingRequests(String userId, int page, int size);
    void cancelFriendRequest(String userId, String friendshipId);
    Page<FriendRequestResponse> getSentRequests(String userId, int page, int size);
    Page<FriendResponse> getFriends(String userId, int page, int size);
    List<FriendResponse> getAllFriends(String userId);
    void removeFriend(String userId, String friendId);
    void blockUser(String userId, String userToBlockId);
    void unblockUser(String userId, String userToUnblockId);
    Page<FriendResponse> getBlockedUsers(String userId, int page, int size);
    Page<UserSearchResponse> searchUsers(String userId, String query, int page, int size);
    boolean areFriends(String userId1, String userId2);
    FriendshipStatsResponse getFriendshipStats(String userId);
}
