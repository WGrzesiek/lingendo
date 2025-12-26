package com.learnwords.deckservice.service.grpcClient;

import com.learnwords.auth.v1.GetUserNameByIdResponse;

import java.util.List;
import java.util.Map;

/**
 * Klient gRPC do komunikacji z user-service
 */
public interface UserGrcpClient {
    
    /**
     * Pobiera nazwę użytkownika po ID
     */
    GetUserNameByIdResponse getUserNameById(String userId);

    /**
     * Sprawdza czy użytkownik ma dostęp do zasobów innego użytkownika
     * (przez relację nauczyciel-uczeń lub znajomość)
     */
    boolean hasAccessToUser(String requesterId, String targetUserId);

    /**
     * Sprawdza czy requester jest nauczycielem target
     */
    boolean isTeacherOf(String teacherId, String studentId);

    /**
     * Sprawdza czy dwaj użytkownicy są znajomymi
     */
    boolean areFriends(String userId1, String userId2);

    /**
     * Pobiera listę wszystkich użytkowników, do których dany user ma dostęp
     */
    List<String> getAccessibleUserIds(String userId);

    /**
     * Pobiera listę ID uczniów nauczyciela
     */
    List<String> getStudentIds(String teacherId);

    /**
     * Pobiera listę ID znajomych
     */
    List<String> getFriendIds(String userId);

    /**
     * Sprawdza dostęp do wielu użytkowników naraz (batch)
     */
    Map<String, Boolean> checkAccessBatch(String requesterId, List<String> targetUserIds);

    // === Metody dla grup ===

    /**
     * Pobiera listę ID grup, do których należy użytkownik (jako nauczyciel lub uczeń).
     */
    List<String> getGroupIds(String userId);

    /**
     * Pobiera listę ID grup nauczyciela.
     */
    List<String> getTeacherGroupIds(String teacherId);

    /**
     * Pobiera listę ID uczniów w grupie.
     */
    List<String> getGroupMemberIds(String groupId);

    /**
     * Pobiera listę ID uczniów z wielu grup.
     */
    List<String> getStudentIdsFromGroups(List<String> groupIds);

    /**
     * Sprawdza czy użytkownik jest właścicielem grupy.
     */
    boolean isGroupOwner(String userId, String groupId);

    /**
     * Sprawdza czy uczeń jest w grupie.
     */
    boolean isStudentInGroup(String studentId, String groupId);

    /**
     * Sprawdza czy uczeń jest w którejkolwiek z grup.
     */
    boolean isStudentInAnyGroup(String studentId, List<String> groupIds);
}
