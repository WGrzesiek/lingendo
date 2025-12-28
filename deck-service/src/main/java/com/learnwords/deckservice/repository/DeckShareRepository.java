package com.learnwords.deckservice.repository;

import com.learnwords.deckservice.entity.DeckShare;
import com.learnwords.deckservice.enums.ShareStatus;
import com.learnwords.deckservice.enums.ShareTargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repozytorium do zarządzania udostępnieniami talii.
 */
public interface DeckShareRepository extends JpaRepository<DeckShare, String> {


    Optional<DeckShare> findByDeckIdAndTargetTypeAndTargetId(String deckId, ShareTargetType targetType, String targetId);
    Optional<DeckShare> findByDeckIdAndTargetTypeAndTargetIdIsNull(String deckId, ShareTargetType targetType);
    boolean existsByDeckIdAndTargetTypeAndTargetIdAndStatus(String deckId, ShareTargetType targetType, String targetId, ShareStatus status);
    boolean existsByDeckIdAndTargetTypeAndTargetIdIsNullAndStatus(String deckId, ShareTargetType targetType, ShareStatus status);
    List<DeckShare> findByDeckIdAndStatus(String deckId, ShareStatus status);
    Page<DeckShare> findByOwnerIdAndStatus(String ownerId, ShareStatus status, Pageable pageable);

    @Query("SELECT ds FROM DeckShare ds WHERE ds.targetType = 'GROUP' AND ds.targetId = :groupId AND ds.status = 'ACTIVE'")
    List<DeckShare> findActiveSharesForGroup(@Param("groupId") String groupId);

    @Query("SELECT ds FROM DeckShare ds WHERE ds.targetType = 'GROUP' AND ds.targetId IN :groupIds AND ds.status = 'ACTIVE'")
    List<DeckShare> findActiveSharesForGroups(@Param("groupIds") List<String> groupIds);

    @Query("SELECT ds FROM DeckShare ds WHERE ds.ownerId = :teacherId AND ds.targetType = 'ALL_STUDENTS' AND ds.status = 'ACTIVE'")
    List<DeckShare> findActiveSharesForAllStudentsOfTeacher(@Param("teacherId") String teacherId);

    @Query("SELECT ds FROM DeckShare ds WHERE ds.ownerId = :userId AND ds.targetType = 'ALL_FRIENDS' AND ds.status = 'ACTIVE'")
    List<DeckShare> findActiveSharesForAllFriendsOfUser(@Param("userId") String userId);

    @Query("SELECT ds FROM DeckShare ds WHERE ds.targetType = 'USER' AND ds.targetId = :userId AND ds.status = 'ACTIVE'")
    List<DeckShare> findActiveSharesForUser(@Param("userId") String userId);

     @Query("""
            SELECT DISTINCT ds.deck.id FROM DeckShare ds 
            WHERE ds.status = 'ACTIVE' 
            AND (
                (ds.targetType = 'USER' AND ds.targetId = :userId)
                OR (ds.targetType = 'GROUP' AND ds.targetId IN :groupIds)
                OR (ds.targetType = 'ALL_STUDENTS' AND ds.ownerId IN :teacherIds)
                OR (ds.targetType = 'ALL_FRIENDS' AND ds.ownerId IN :friendIds)
            )
            """)
    List<String> findAccessibleDeckIds(
            @Param("userId") String userId,
            @Param("groupIds") List<String> groupIds,
            @Param("teacherIds") List<String> teacherIds,
            @Param("friendIds") List<String> friendIds);

    @Query("""
            SELECT COUNT(ds) > 0 FROM DeckShare ds 
            WHERE ds.deck.id = :deckId AND ds.status = 'ACTIVE' 
            AND (
                (ds.targetType = 'USER' AND ds.targetId = :userId)
                OR (ds.targetType = 'GROUP' AND ds.targetId IN :groupIds)
                OR (ds.targetType = 'ALL_STUDENTS' AND ds.ownerId IN :teacherIds)
                OR (ds.targetType = 'ALL_FRIENDS' AND ds.ownerId IN :friendIds)
            )
            """)
    boolean hasAccessToDeck(
            @Param("deckId") String deckId,
            @Param("userId") String userId,
            @Param("groupIds") List<String> groupIds,
            @Param("teacherIds") List<String> teacherIds,
            @Param("friendIds") List<String> friendIds);

    long countByDeckIdAndStatus(String deckId, ShareStatus status);

    long countByOwnerIdAndStatus(String ownerId, ShareStatus status);

    @Query("""
            SELECT ds FROM DeckShare ds 
            WHERE ds.deck.id = :deckId AND ds.status = 'ACTIVE' 
            AND (
                (ds.targetType = 'USER' AND ds.targetId = :userId)
                OR (ds.targetType = 'GROUP' AND ds.targetId IN :groupIds)
                OR (ds.targetType = 'ALL_STUDENTS' AND ds.ownerId IN :teacherIds)
                OR (ds.targetType = 'ALL_FRIENDS' AND ds.ownerId IN :friendIds)
            )
            ORDER BY ds.sharedAt DESC
            """)
    List<DeckShare> findActiveSharesForUserAndDeck(
            @Param("deckId") String deckId,
            @Param("userId") String userId,
            @Param("groupIds") List<String> groupIds,
            @Param("teacherIds") List<String> teacherIds,
            @Param("friendIds") List<String> friendIds);

    List<DeckShare> findAllByDeckIdAndOwnerId(String deckId, String userId);
}

