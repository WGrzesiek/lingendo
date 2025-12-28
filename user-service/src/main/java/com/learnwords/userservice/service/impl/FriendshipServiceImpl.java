package com.learnwords.userservice.service.impl;

import com.learnwords.common.KafkaTopic;
import com.learnwords.common.events.FriendshipAcceptedEvent;
import com.learnwords.common.events.FriendshipRemovedEvent;
import com.learnwords.userservice.dtos.friendship.*;
import com.learnwords.userservice.entity.User;
import com.learnwords.userservice.entity.UserFriendship;
import com.learnwords.userservice.enums.FriendshipStatus;
import com.learnwords.userservice.events.GenericEventProducer;
import com.learnwords.userservice.exception.exceptions.*;
import com.learnwords.userservice.repository.FriendshipRepository;
import com.learnwords.userservice.repository.UserRepository;
import com.learnwords.userservice.service.FriendshipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementacja serwisu do zarządzania relacjami przyjaźni
 */
@Slf4j
@Service
public class FriendshipServiceImpl implements FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final GenericEventProducer genericEventProducer;

    public FriendshipServiceImpl(FriendshipRepository friendshipRepository, 
                                  UserRepository userRepository,
                                 GenericEventProducer genericEventProducer) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
        this.genericEventProducer = genericEventProducer;
    }

    // === Zaproszenia do znajomych ===

    @Override
    @Transactional
    public FriendRequestResponse sendFriendRequest(String userId, String targetUserId) {
        log.info("Wysyłanie zaproszenia do znajomych: {} -> {}", userId, targetUserId);

        if (userId.equals(targetUserId)) {
            throw new InvalidOperationException("Nie możesz wysłać zaproszenia do siebie");
        }

        User sender = findUserById(userId);
        User target = findUserById(targetUserId);

        if (friendshipRepository.existsByUsers(userId, targetUserId)) {
            throw new RelationAlreadyExistsException(
                    "Relacja z tym użytkownikiem już istnieje lub masz oczekujące zaproszenie");
        }

        UserFriendship friendship = UserFriendship.builder()
                .id(UUID.randomUUID().toString())
                .user1(sender)
                .user2(target)
                .requestedBy(sender)
                .status(FriendshipStatus.PENDING)
                .build();

        friendshipRepository.save(friendship);
        log.info("Wysłano zaproszenie do znajomych: {} -> {}", userId, targetUserId);

        return mapToFriendRequestResponse(friendship);
    }

    @Override
    @Transactional
    public FriendResponse acceptFriendRequest(String userId, String friendshipId) {
        log.info("Akceptowanie zaproszenia: {} przez użytkownika: {}", friendshipId, userId);

        UserFriendship friendship = findFriendshipById(friendshipId);

        if (!canAcceptRequest(friendship, userId)) {
            throw new UnauthorizedOperationException("Nie możesz zaakceptować tego zaproszenia");
        }

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new InvalidOperationException("To zaproszenie nie jest już oczekujące");
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendshipRepository.save(friendship);

        genericEventProducer.send(KafkaTopic.FRIENDSHIP_ACCEPTED, FriendshipAcceptedEvent.builder()
                .eventTime(Instant.now())
                .userId1(friendship.getUser1().getId())
                .userId2(friendship.getUser2().getId())
                .build());

        log.info("Zaakceptowano zaproszenie: {}", friendshipId);

        return mapToFriendResponse(friendship, userId);
    }

    @Override
    @Transactional
    public void rejectFriendRequest(String userId, String friendshipId) {
        log.info("Odrzucanie zaproszenia: {} przez użytkownika: {}", friendshipId, userId);

        UserFriendship friendship = findFriendshipById(friendshipId);

        if (!canAcceptRequest(friendship, userId)) {
            throw new UnauthorizedOperationException("Nie możesz odrzucić tego zaproszenia");
        }

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new InvalidOperationException("To zaproszenie nie jest już oczekujące");
        }

        friendshipRepository.delete(friendship);
        log.info("Odrzucono zaproszenie: {}", friendshipId);
    }

    @Override
    @Transactional
    public void cancelFriendRequest(String userId, String friendshipId) {
        log.info("Anulowanie zaproszenia: {} przez użytkownika: {}", friendshipId, userId);

        UserFriendship friendship = findFriendshipById(friendshipId);

        if (!friendship.getRequestedBy().getId().equals(userId)) {
            throw new UnauthorizedOperationException("Możesz anulować tylko własne zaproszenia");
        }

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new InvalidOperationException("To zaproszenie nie jest już oczekujące");
        }

        friendshipRepository.delete(friendship);
        log.info("Anulowano zaproszenie: {}", friendshipId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FriendRequestResponse> getPendingRequests(String userId, int page, int size) {
        log.debug("Pobieranie oczekujących zaproszeń dla: {}", userId);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return friendshipRepository.findPendingRequestsForUser(userId, pageable)
                .map(this::mapToFriendRequestResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FriendRequestResponse> getSentRequests(String userId, int page, int size) {
        log.debug("Pobieranie wysłanych zaproszeń przez: {}", userId);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return friendshipRepository.findPendingSentByUser(userId, pageable)
                .map(this::mapToFriendRequestResponse);
    }

    // === Zarządzanie znajomymi ===

    @Override
    @Transactional(readOnly = true)
    public Page<FriendResponse> getFriends(String userId, int page, int size) {
        log.debug("Pobieranie znajomych użytkownika: {}", userId);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return friendshipRepository
                .findFriendsByUserIdAndStatus(userId, FriendshipStatus.ACCEPTED, pageable)
                .map(f -> mapToFriendResponse(f, userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FriendResponse> getAllFriends(String userId) {
        log.debug("Pobieranie wszystkich znajomych użytkownika: {}", userId);
        return friendshipRepository
                .findAllFriendsByUserIdAndStatus(userId, FriendshipStatus.ACCEPTED)
                .stream()
                .map(f -> mapToFriendResponse(f, userId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeFriend(String userId, String friendId) {
        log.info("Usuwanie znajomego: {} przez użytkownika: {}", friendId, userId);

        UserFriendship friendship = friendshipRepository
                .findByUsersAndStatus(userId, friendId, FriendshipStatus.ACCEPTED)
                .orElseThrow(() -> new RelationNotFoundException(
                        "Nie znaleziono relacji z tym użytkownikiem"));

        friendshipRepository.delete(friendship);

        genericEventProducer.send(KafkaTopic.FRIENDSHIP_REMOVED, FriendshipRemovedEvent.builder()
                .eventTime(Instant.now())
                .userId1(userId)
                .userId2(friendId)
                .reason("REMOVED")
                .build());

        log.info("Usunięto znajomego: {} przez użytkownika: {}", friendId, userId);
    }

    @Override
    @Transactional
    public void blockUser(String userId, String userToBlockId) {
        log.info("Blokowanie użytkownika: {} przez: {}", userToBlockId, userId);

        if (userId.equals(userToBlockId)) {
            throw new InvalidOperationException("Nie możesz zablokować siebie");
        }

        User blocker = findUserById(userId);
        User blocked = findUserById(userToBlockId);

        var existingRelation = friendshipRepository.findByUsers(userId, userToBlockId);

        if (existingRelation.isPresent()) {
            UserFriendship friendship = existingRelation.get();
            friendship.setStatus(FriendshipStatus.BLOCKED);
            friendship.setRequestedBy(blocker);
            friendshipRepository.save(friendship);
        } else {
            UserFriendship friendship = UserFriendship.builder()
                    .id(UUID.randomUUID().toString())
                    .user1(blocker)
                    .user2(blocked)
                    .requestedBy(blocker)
                    .status(FriendshipStatus.BLOCKED)
                    .build();
            friendshipRepository.save(friendship);
        }

        genericEventProducer.send(KafkaTopic.FRIENDSHIP_REMOVED, FriendshipRemovedEvent.builder()
                .eventTime(Instant.now())
                .userId1(userId)
                .userId2(userToBlockId)
                .reason("BLOCKED")
                .build());

        log.info("Zablokowano użytkownika: {} przez: {}", userToBlockId, userId);
    }

    @Override
    @Transactional
    public void unblockUser(String userId, String userToUnblockId) {
        log.info("Odblokowanie użytkownika: {} przez: {}", userToUnblockId, userId);

        UserFriendship friendship = friendshipRepository
                .findByUsersAndStatus(userId, userToUnblockId, FriendshipStatus.BLOCKED)
                .orElseThrow(() -> new RelationNotFoundException(
                        "Ten użytkownik nie jest zablokowany"));

        if (!friendship.getRequestedBy().getId().equals(userId)) {
            throw new UnauthorizedOperationException("Nie możesz odblokować tego użytkownika");
        }

        friendshipRepository.delete(friendship);
        log.info("Odblokowano użytkownika: {} przez: {}", userToUnblockId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FriendResponse> getBlockedUsers(String userId, int page, int size) {
        log.debug("Pobieranie zablokowanych użytkowników przez: {}", userId);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return friendshipRepository
                .findFriendsByUserIdAndStatus(userId, FriendshipStatus.BLOCKED, pageable)
                .map(f -> mapToFriendResponse(f, userId));
    }

    // === Wyszukiwanie i sprawdzanie ===

    @Override
    @Transactional(readOnly = true)
    public Page<UserSearchResponse> searchUsers(String userId, String query, int page, int size) {
        log.debug("Wyszukiwanie użytkowników: '{}' przez: {}", query, userId);
        PageRequest pageable = PageRequest.of(page, size);
        return userRepository.searchUsers(userId, query, pageable)
                .map(user -> mapToUserSearchResponse(user, userId));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean areFriends(String userId1, String userId2) {
        return friendshipRepository.areFriends(userId1, userId2);
    }

    @Override
    @Transactional(readOnly = true)
    public FriendshipStatsResponse getFriendshipStats(String userId) {
        log.debug("Pobieranie statystyk znajomych dla: {}", userId);

        long totalFriends = friendshipRepository.countByUserIdAndStatus(userId, FriendshipStatus.ACCEPTED);
        long pendingReceived = friendshipRepository.countPendingRequestsForUser(userId);
        long blockedUsers = friendshipRepository.countByUserIdAndStatus(userId, FriendshipStatus.BLOCKED);
        long pendingSent = friendshipRepository.countPendingSentByUser(userId);

        return new FriendshipStatsResponse(totalFriends, pendingReceived, pendingSent, blockedUsers);
    }

    // === Metody pomocnicze ===

    private User findUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Nie znaleziono użytkownika: " + userId));
    }

    private UserFriendship findFriendshipById(String friendshipId) {
        return friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RelationNotFoundException(
                        "Nie znaleziono relacji: " + friendshipId));
    }

    private boolean canAcceptRequest(UserFriendship friendship, String userId) {
        String requesterId = friendship.getRequestedBy().getId();
        String user1Id = friendship.getUser1().getId();
        String user2Id = friendship.getUser2().getId();

        return (user1Id.equals(userId) || user2Id.equals(userId)) && !requesterId.equals(userId);
    }

    private FriendRequestResponse mapToFriendRequestResponse(UserFriendship friendship) {
        User fromUser = friendship.getRequestedBy();
        User toUser = friendship.getUser1().getId().equals(fromUser.getId())
                ? friendship.getUser2()
                : friendship.getUser1();

        return new FriendRequestResponse(
                friendship.getId(),
                fromUser.getId(),
                fromUser.getUsername(),
                fromUser.getFirstName(),
                fromUser.getLastName(),
                toUser.getId(),
                toUser.getUsername(),
                friendship.getStatus(),
                friendship.getCreatedAt()
        );
    }

    private FriendResponse mapToFriendResponse(UserFriendship friendship, String currentUserId) {
        User friend = friendship.getUser1().getId().equals(currentUserId)
                ? friendship.getUser2()
                : friendship.getUser1();

        return new FriendResponse(
                friendship.getId(),
                friend.getId(),
                friend.getUsername(),
                friend.getFirstName(),
                friend.getLastName(),
                friend.getEmail(),
                friendship.getStatus(),
                friendship.getUpdatedAt()
        );
    }

    private UserSearchResponse mapToUserSearchResponse(User user, String currentUserId) {
        var existingRelation = friendshipRepository.findByUsers(currentUserId, user.getId());

        boolean isFriend = existingRelation
                .map(f -> f.getStatus() == FriendshipStatus.ACCEPTED)
                .orElse(false);

        boolean hasPendingRequest = existingRelation
                .map(f -> f.getStatus() == FriendshipStatus.PENDING)
                .orElse(false);

        return new UserSearchResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                isFriend,
                hasPendingRequest
        );
    }
}
