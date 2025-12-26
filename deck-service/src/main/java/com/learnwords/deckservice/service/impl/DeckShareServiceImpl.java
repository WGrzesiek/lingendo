package com.learnwords.deckservice.service.impl;

import com.learnwords.common.KafkaTopic;
import com.learnwords.common.events.DeckSharedEvent;
import com.learnwords.common.events.DeckShareRevokedEvent;
import com.learnwords.deckservice.dto.share.*;
import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.entity.DeckShare;
import com.learnwords.deckservice.enums.ShareStatus;
import com.learnwords.deckservice.enums.ShareTargetType;
import com.learnwords.deckservice.exception.exceptions.DeckNotFoundException;
import com.learnwords.deckservice.exception.exceptions.DeckShareNotFoundException;
import com.learnwords.deckservice.exception.exceptions.UnauthorizedDeckAccessException;
import com.learnwords.deckservice.repository.DeckRepository;
import com.learnwords.deckservice.repository.DeckShareRepository;
import com.learnwords.deckservice.service.DeckShareService;
import com.learnwords.deckservice.service.event.GenericEventProducer;
import com.learnwords.deckservice.service.grpcClient.UserGrcpClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementacja serwisu do zarządzania udostępnianiem talii.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeckShareServiceImpl implements DeckShareService {

    private final DeckShareRepository shareRepository;
    private final DeckRepository deckRepository;
    private final UserGrcpClient userGrpcClient;
    private final GenericEventProducer eventProducer;

    @Override
    @Transactional
    public DeckShareResponse shareDeck(String userId, ShareDeckRequest request) {
        log.info("Udostępnianie talii {} przez użytkownika {} do {}: {}",
                request.getDeckId(), userId, request.getTargetType(), request.getTargetId());

        Deck deck = findDeckById(request.getDeckId());
        validateDeckOwnership(deck, userId);
        validateShareTarget(userId, request.getTargetType(), request.getTargetId());

        DeckShare share = createShare(deck, userId, request.getTargetType(),
                request.getTargetId(), request.getMessage(), request.getExpiresAt());

        shareRepository.save(share);
        emitDeckSharedEvent(share);

        log.info("Utworzono udostępnienie talii: {}", share.getId());
        return mapToResponse(share, deck);
    }

    @Override
    @Transactional
    public BatchShareResponse shareDeckBatch(String userId, BatchShareDeckRequest request) {
        log.info("Batch udostępnianie talii {} do {} celów typu {}",
                request.getDeckId(), request.getTargetIds().size(), request.getTargetType());

        Deck deck = findDeckById(request.getDeckId());
        validateDeckOwnership(deck, userId);

        List<String> success = new ArrayList<>();
        List<String> failed = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (String targetId : request.getTargetIds()) {
            try {
                validateShareTarget(userId, request.getTargetType(), targetId);

                DeckShare share = createShare(deck, userId, request.getTargetType(),
                        targetId, request.getMessage(), request.getExpiresAt());
                shareRepository.save(share);
                emitDeckSharedEvent(share);

                success.add(targetId);
            } catch (Exception e) {
                failed.add(targetId);
                errors.add(targetId + ": " + e.getMessage());
                log.warn("Nie udało się udostępnić talii {} do {}: {}", 
                        request.getDeckId(), targetId, e.getMessage());
            }
        }

        return BatchShareResponse.builder()
                .success(success)
                .failed(failed)
                .errors(errors)
                .totalProcessed(request.getTargetIds().size())
                .successCount(success.size())
                .failedCount(failed.size())
                .build();
    }

    @Override
    @Transactional
    public DeckShareResponse shareDeckWithAllStudents(String teacherId, String deckId, String message) {
        ShareDeckRequest request = ShareDeckRequest.builder()
                .deckId(deckId)
                .targetType(ShareTargetType.ALL_STUDENTS)
                .targetId(null)
                .message(message)
                .build();
        return shareDeck(teacherId, request);
    }

    @Override
    @Transactional
    public DeckShareResponse shareDeckWithAllFriends(String userId, String deckId, String message) {
        ShareDeckRequest request = ShareDeckRequest.builder()
                .deckId(deckId)
                .targetType(ShareTargetType.ALL_FRIENDS)
                .targetId(null)
                .message(message)
                .build();
        return shareDeck(userId, request);
    }

    @Override
    @Transactional
    public DeckShareResponse shareDeckWithGroup(String userId, String deckId, String groupId, String message) {
        ShareDeckRequest request = ShareDeckRequest.builder()
                .deckId(deckId)
                .targetType(ShareTargetType.GROUP)
                .targetId(groupId)
                .message(message)
                .build();
        return shareDeck(userId, request);
    }

    @Override
    @Transactional
    public DeckShareResponse shareDeckWithUser(String userId, String deckId, String targetUserId, String message) {
        ShareDeckRequest request = ShareDeckRequest.builder()
                .deckId(deckId)
                .targetType(ShareTargetType.USER)
                .targetId(targetUserId)
                .message(message)
                .build();
        return shareDeck(userId, request);
    }

    @Override
    @Transactional
    public void revokeDeckShare(String userId, String shareId) {
        log.info("Wycofywanie udostępnienia {} przez użytkownika {}", shareId, userId);

        DeckShare share = shareRepository.findById(shareId)
                .orElseThrow(() -> new DeckShareNotFoundException("Nie znaleziono udostępnienia: " + shareId));

        if (!share.getOwnerId().equals(userId)) {
            throw new UnauthorizedDeckAccessException("Nie masz uprawnień do wycofania tego udostępnienia");
        }

        share.revoke();
        shareRepository.save(share);
        emitDeckShareRevokedEvent(share);

        log.info("Wycofano udostępnienie: {}", shareId);
    }

    @Override
    @Transactional
    public void revokeAllDeckShares(String userId, String deckId) {
        log.info("Wycofywanie wszystkich udostępnień talii {} przez użytkownika {}", deckId, userId);

        Deck deck = findDeckById(deckId);
        validateDeckOwnership(deck, userId);

        List<DeckShare> shares = shareRepository.findByDeckIdAndStatus(deckId, ShareStatus.ACTIVE);
        for (DeckShare share : shares) {
            share.revoke();
            shareRepository.save(share);
            emitDeckShareRevokedEvent(share);
        }

        log.info("Wycofano {} udostępnień talii {}", shares.size(), deckId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeckShareResponse> getDeckShares(String userId, String deckId) {
        log.debug("Pobieranie udostępnień talii {} dla użytkownika {}", deckId, userId);

        Deck deck = findDeckById(deckId);
        validateDeckOwnership(deck, userId);

        List<DeckShare> shares = shareRepository.findByDeckIdAndStatus(deckId, ShareStatus.ACTIVE);
        return shares.stream()
                .map(share -> mapToResponse(share, deck))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeckShareResponse> getMyShares(String userId, int page, int size) {
        log.debug("Pobieranie udostępnień użytkownika {}", userId);

        Page<DeckShare> shares = shareRepository.findByOwnerIdAndStatus(
                userId, ShareStatus.ACTIVE, PageRequest.of(page, size));

        List<DeckShareResponse> responses = shares.getContent().stream()
                .map(share -> {
                    Deck deck = share.getDeck();
                    return mapToResponse(share, deck);
                })
                .toList();

        return new PageImpl<>(responses, shares.getPageable(), shares.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SharedDeckDto> getSharedWithMe(String userId, int page, int size) {
        log.debug("Pobieranie talii udostępnionych użytkownikowi {}", userId);

        List<String> groupIds = userGrpcClient.getGroupIds(userId);
        List<String> teacherIds = userGrpcClient.getAccessibleUserIds(userId);
        List<String> friendIds = userGrpcClient.getFriendIds(userId);

        List<String> accessibleDeckIds = shareRepository.findAccessibleDeckIds(
                userId,
                groupIds.isEmpty() ? Collections.singletonList("__none__") : groupIds,
                teacherIds.isEmpty() ? Collections.singletonList("__none__") : teacherIds,
                friendIds.isEmpty() ? Collections.singletonList("__none__") : friendIds);

        if (accessibleDeckIds.isEmpty()) {
            return Page.empty();
        }

        List<Deck> decks = deckRepository.findAllById(accessibleDeckIds);
        List<SharedDeckDto> dtos = decks.stream()
                .map(deck -> mapToSharedDeckDto(deck, userId))
                .toList();

        int start = page * size;
        int end = Math.min(start + size, dtos.size());
        
        if (start >= dtos.size()) {
            return Page.empty();
        }

        return new PageImpl<>(dtos.subList(start, end), PageRequest.of(page, size), dtos.size());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAccessToDeck(String userId, String deckId) {
        List<String> groupIds = userGrpcClient.getGroupIds(userId);
        List<String> teacherIds = userGrpcClient.getAccessibleUserIds(userId);
        List<String> friendIds = userGrpcClient.getFriendIds(userId);

        return shareRepository.hasAccessToDeck(
                deckId,
                userId,
                groupIds.isEmpty() ? Collections.singletonList("__none__") : groupIds,
                teacherIds.isEmpty() ? Collections.singletonList("__none__") : teacherIds,
                friendIds.isEmpty() ? Collections.singletonList("__none__") : friendIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAccessibleDeckIds(String userId) {
        List<String> groupIds = userGrpcClient.getGroupIds(userId);
        List<String> teacherIds = userGrpcClient.getAccessibleUserIds(userId);
        List<String> friendIds = userGrpcClient.getFriendIds(userId);

        return shareRepository.findAccessibleDeckIds(
                userId,
                groupIds.isEmpty() ? Collections.singletonList("__none__") : groupIds,
                teacherIds.isEmpty() ? Collections.singletonList("__none__") : teacherIds,
                friendIds.isEmpty() ? Collections.singletonList("__none__") : friendIds);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DeckShare> findActiveShareForUser(String userId, String deckId) {
        List<String> groupIds = userGrpcClient.getGroupIds(userId);
        List<String> teacherIds = userGrpcClient.getAccessibleUserIds(userId);
        List<String> friendIds = userGrpcClient.getFriendIds(userId);

        List<DeckShare> shares = shareRepository.findActiveSharesForUserAndDeck(
                deckId,
                userId,
                groupIds.isEmpty() ? Collections.singletonList("__none__") : groupIds,
                teacherIds.isEmpty() ? Collections.singletonList("__none__") : teacherIds,
                friendIds.isEmpty() ? Collections.singletonList("__none__") : friendIds);
        
        return shares.isEmpty() ? Optional.empty() : Optional.of(shares.get(0));
    }

    private Deck findDeckById(String deckId) {
        return deckRepository.findById(deckId)
                .orElseThrow(() -> new DeckNotFoundException("Nie znaleziono talii: " + deckId));
    }

    private void validateDeckOwnership(Deck deck, String userId) {
        if (!deck.getOwnerId().equals(userId)) {
            throw new UnauthorizedDeckAccessException("Nie jesteś właścicielem tej talii");
        }
    }

    private void validateShareTarget(String userId, ShareTargetType targetType, String targetId) {
        switch (targetType) {
            case GROUP -> {
                if (targetId == null || targetId.isBlank()) {
                    throw new IllegalArgumentException("ID grupy jest wymagane");
                }
                if (!userGrpcClient.isGroupOwner(userId, targetId)) {
                    throw new UnauthorizedDeckAccessException("Nie jesteś właścicielem tej grupy");
                }
            }
            case USER -> {
                if (targetId == null || targetId.isBlank()) {
                    throw new IllegalArgumentException("ID użytkownika jest wymagane");
                }
                if (!userGrpcClient.hasAccessToUser(userId, targetId)) {
                    throw new UnauthorizedDeckAccessException("Nie masz relacji z tym użytkownikiem");
                }
            }
            case ALL_STUDENTS, ALL_FRIENDS -> {
            }
        }
    }

    private DeckShare createShare(Deck deck, String ownerId, ShareTargetType targetType,
                                   String targetId, String message, Instant expiresAt) {
        if (targetId != null) {
            if (shareRepository.existsByDeckIdAndTargetTypeAndTargetIdAndStatus(
                    deck.getId(), targetType, targetId, ShareStatus.ACTIVE)) {
                throw new IllegalStateException("Takie udostępnienie już istnieje");
            }
        } else {
            if (shareRepository.existsByDeckIdAndTargetTypeAndTargetIdIsNullAndStatus(
                    deck.getId(), targetType, ShareStatus.ACTIVE)) {
                throw new IllegalStateException("Takie udostępnienie już istnieje");
            }
        }

        return DeckShare.builder()
                .deck(deck)
                .ownerId(ownerId)
                .targetType(targetType)
                .targetId(targetId)
                .message(message)
                .expiresAt(expiresAt)
                .status(ShareStatus.ACTIVE)
                .build();
    }

    private DeckShareResponse mapToResponse(DeckShare share, Deck deck) {
        String targetName = getTargetName(share.getTargetType(), share.getTargetId());

        return DeckShareResponse.builder()
                .id(share.getId())
                .deckId(deck.getId())
                .deckName(deck.getName())
                .ownerId(share.getOwnerId())
                .ownerName(null) // NOTE: pobierz z user-service
                .targetType(share.getTargetType())
                .targetId(share.getTargetId())
                .targetName(targetName)
                .status(share.getStatus())
                .message(share.getMessage())
                .sharedAt(share.getSharedAt())
                .expiresAt(share.getExpiresAt())
                .revokedAt(share.getRevokedAt())
                .build();
    }

    private String getTargetName(ShareTargetType targetType, String targetId) {
        return switch (targetType) {
            case ALL_STUDENTS -> "Wszyscy uczniowie";
            case ALL_FRIENDS -> "Wszyscy znajomi";
            case GROUP -> "Grupa: " + targetId; // NOTE: pobierz nazwę grupy
            case USER -> "Użytkownik: " + targetId; // NOTE: pobierz nazwę użytkownika
        };
    }

    private SharedDeckDto mapToSharedDeckDto(Deck deck, String userId) {
        return SharedDeckDto.builder()
                .deckId(deck.getId())
                .deckName(deck.getName())
                .description(deck.getDescription())
                .ownerId(deck.getOwnerId())
                .ownerName(null) // NOTE: pobierz z user-service
                .sharedVia(null) // NOTE: określ jak udostępniono
                .sharedViaName(null)
                .message(null)
                .flashcardCount(deck.getWordCount())
                .languageFrom(deck.getLanguageFrom() != null ? deck.getLanguageFrom().name() : null)
                .languageTo(deck.getLanguageTo() != null ? deck.getLanguageTo().name() : null)
                .difficulty(deck.getDifficulty() != null ? deck.getDifficulty().name() : null)
                .category(deck.getCategory() != null ? deck.getCategory().name() : null)
                .build();
    }

    private void emitDeckSharedEvent(DeckShare share) {
        try {
            DeckSharedEvent event = DeckSharedEvent.builder()
                    .eventTime(Instant.now())
                    .deckId(share.getDeck().getId())
                    .deckName(share.getDeck().getName())
                    .ownerId(share.getOwnerId())
                    .targetType(share.getTargetType().name())
                    .targetId(share.getTargetId())
                    .sharedAt(share.getSharedAt())
                    .receivedAt(Instant.now())
                    .build();
            eventProducer.send(KafkaTopic.DECK_SHARED, event);
        } catch (Exception e) {
            log.error("Błąd podczas wysyłania eventu DeckShared: {}", e.getMessage());
        }
    }

    private void emitDeckShareRevokedEvent(DeckShare share) {
        try {
            DeckShareRevokedEvent event = DeckShareRevokedEvent.builder()
                    .eventTime(Instant.now())
                    .deckId(share.getDeck().getId())
                    .ownerId(share.getOwnerId())
                    .targetType(share.getTargetType().name())
                    .targetId(share.getTargetId())
                    .revokedAt(share.getRevokedAt())
                    .receivedAt(Instant.now())
                    .build();
            eventProducer.send(KafkaTopic.DECK_SHARE_REVOKED, event);
        } catch (Exception e) {
            log.error("Błąd podczas wysyłania eventu DeckShareRevoked: {}", e.getMessage());
        }
    }
}
