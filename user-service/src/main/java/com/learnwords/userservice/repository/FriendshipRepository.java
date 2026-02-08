package com.learnwords.userservice.repository;

import com.learnwords.userservice.entity.UserFriendship;
import com.learnwords.userservice.enums.FriendshipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repozytorium do zarządzania relacjami przyjaźni między użytkownikami
 */
public interface FriendshipRepository extends JpaRepository<UserFriendship, String> {

    @Query("SELECT f FROM UserFriendship f WHERE " +
            "(f.user1.id = :userId1 AND f.user2.id = :userId2) OR " +
            "(f.user1.id = :userId2 AND f.user2.id = :userId1)")
    Optional<UserFriendship> findByUsers(@Param("userId1") String userId1, @Param("userId2") String userId2);

    @Query("SELECT f FROM UserFriendship f WHERE " +
            "((f.user1.id = :userId1 AND f.user2.id = :userId2) OR " +
            "(f.user1.id = :userId2 AND f.user2.id = :userId1)) AND f.status = :status")
    Optional<UserFriendship> findByUsersAndStatus(
            @Param("userId1") String userId1,
            @Param("userId2") String userId2,
            @Param("status") FriendshipStatus status);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM UserFriendship f WHERE " +
            "(f.user1.id = :userId1 AND f.user2.id = :userId2) OR " +
            "(f.user1.id = :userId2 AND f.user2.id = :userId1)")
    boolean existsByUsers(@Param("userId1") String userId1, @Param("userId2") String userId2);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM UserFriendship f WHERE " +
            "((f.user1.id = :userId1 AND f.user2.id = :userId2) OR " +
            "(f.user1.id = :userId2 AND f.user2.id = :userId1)) AND f.status = 'ACCEPTED'")
    boolean areFriends(@Param("userId1") String userId1, @Param("userId2") String userId2);

    @Query("SELECT f FROM UserFriendship f WHERE " +
            "(f.user1.id = :userId OR f.user2.id = :userId) AND f.status = :status")
    Page<UserFriendship> findFriendsByUserIdAndStatus(
            @Param("userId") String userId,
            @Param("status") FriendshipStatus status,
            Pageable pageable);

    @Query("SELECT f FROM UserFriendship f WHERE " +
            "(f.user1.id = :userId OR f.user2.id = :userId) AND f.status = :status")
    List<UserFriendship> findAllFriendsByUserIdAndStatus(
            @Param("userId") String userId,
            @Param("status") FriendshipStatus status);

    @Query("SELECT f FROM UserFriendship f WHERE " +
            "((f.user1.id = :userId AND f.requestedBy.id != :userId) OR " +
            "(f.user2.id = :userId AND f.requestedBy.id != :userId)) AND f.status = 'PENDING'")
    Page<UserFriendship> findPendingRequestsForUser(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT f FROM UserFriendship f WHERE " +
            "f.requestedBy.id = :userId AND f.status = 'PENDING'")
    Page<UserFriendship> findPendingSentByUser(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT COUNT(f) FROM UserFriendship f WHERE " +
            "(f.user1.id = :userId OR f.user2.id = :userId) AND f.status = :status")
    long countByUserIdAndStatus(@Param("userId") String userId, @Param("status") FriendshipStatus status);

    @Query("SELECT COUNT(f) FROM UserFriendship f WHERE " +
            "((f.user1.id = :userId AND f.requestedBy.id != :userId) OR " +
            "(f.user2.id = :userId AND f.requestedBy.id != :userId)) AND f.status = 'PENDING'")
    long countPendingRequestsForUser(@Param("userId") String userId);

    @Query("SELECT COUNT(f) FROM UserFriendship f WHERE " +
            "f.requestedBy.id = :userId AND f.status = 'PENDING'")
    long countPendingSentByUser(@Param("userId") String userId);

    @Query("SELECT CASE WHEN f.user1.id = :userId THEN f.user2.id ELSE f.user1.id END " +
            "FROM UserFriendship f WHERE " +
            "(f.user1.id = :userId OR f.user2.id = :userId) AND f.status = 'ACCEPTED'")
    List<String> findFriendIdsByUserId(@Param("userId") String userId);
}
