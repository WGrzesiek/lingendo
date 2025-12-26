package com.learnwords.userservice.service;

import com.learnwords.userservice.dtos.friendship.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Serwis do zarządzania relacjami przyjaźni między użytkownikami
 */
public interface FriendshipService {
    FriendRequestResponse sendFriendRequest(String userId, String targetUserId);
    FriendResponse acceptFriendRequest(String userId, String friendshipId);
    void rejectFriendRequest(String userId, String friendshipId);
    Page<FriendRequestResponse> getPendingRequests(String userId, Pageable pageable);
    void cancelFriendRequest(String userId, String friendshipId);
    Page<FriendRequestResponse> getSentRequests(String userId, Pageable pageable);
    Page<FriendResponse> getFriends(String userId, Pageable pageable);
    List<FriendResponse> getAllFriends(String userId);
    void removeFriend(String userId, String friendId);
    void blockUser(String userId, String userToBlockId);
    void unblockUser(String userId, String userToUnblockId);
    Page<FriendResponse> getBlockedUsers(String userId, Pageable pageable);
    Page<UserSearchResponse> searchUsers(String userId, String query, Pageable pageable);
    boolean areFriends(String userId1, String userId2);
    FriendshipStatsResponse getFriendshipStats(String userId);
}
