package com.learnwords.deckservice.service.impl;

import com.learnwords.common.KafkaTopic;
import com.learnwords.common.events.DeckEnrollmentsCreated;
import com.learnwords.deckservice.dto.deckEnrollment.CreateDeckEnrollmentDto;
import com.learnwords.deckservice.dto.deckEnrollment.DeckEnrollmentDto;
import com.learnwords.deckservice.dto.dashboard.StudentMyCourseListItemDto;
import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.entity.DeckEnrollment;
import com.learnwords.deckservice.enums.DeckEnrollmentRole;
import com.learnwords.deckservice.enums.DeckEnrollmentSource;
import com.learnwords.deckservice.enums.DeckVisibility;
import com.learnwords.deckservice.enums.LearnAlgorithm;
import com.learnwords.deckservice.repository.DeckEnrollmentRepository;
import com.learnwords.deckservice.repository.DeckRepository;
import com.learnwords.deckservice.service.DeckEnrollmentService;
import com.learnwords.deckservice.service.event.GenericEventProducer;
import com.learnwords.deckservice.service.utils.DeckUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class DeckEnrollmentServiceImpl implements DeckEnrollmentService {
    private final DeckRepository deckRepository;
    private final DeckEnrollmentRepository deckEnrollmentRepository;
    private final GenericEventProducer eventProducer;

    public DeckEnrollmentServiceImpl(DeckRepository deckRepository, DeckEnrollmentRepository deckEnrollmentRepository, GenericEventProducer eventProducer) {
        this.eventProducer = eventProducer;
        this.deckRepository = deckRepository;
        this.deckEnrollmentRepository = deckEnrollmentRepository;
    }

    @Override
    @Transactional
    public void enrollUserToDeck(String userId, String deckId, CreateDeckEnrollmentDto createDeckEnrollmentDto) {
        DeckVisibility deckVisibility = deckRepository.getReferenceById(deckId).getVisibility();
        log.info("Przypisanie uzytkownika {} do talii {}", userId, deckId);
        DeckEnrollment deckEnrollment = DeckEnrollment.builder()
                .userId(userId)
                .deck(deckRepository.getReferenceById(deckId))
                .role(deckEnrollmentRole(deckVisibility))
                .source(deckEnrollmentSource(deckVisibility))
                .howManyFlashcardsForOneSession(createDeckEnrollmentDto.getHowManyFlashcardsForOneSession())
                .preferredAlgorithm(createDeckEnrollmentDto.getPreferredAlgorithm())
                .joinedAt(Instant.now())
                .build();
        deckEnrollmentRepository.save(deckEnrollment);
        log.info("Uzytkownik {} zostal przypisany do talii {}", userId, deckId);
        DeckEnrollmentsCreated event = DeckEnrollmentsCreated.builder()
                .eventTime(deckEnrollment.getJoinedAt())
                .deckEnrollmentId(deckEnrollment.getId())
                .deckId(deckId)
                .userId(userId)
                .receivedAt(Instant.now())
                .build();
        eventProducer.send(KafkaTopic.DECK_ENROLLMENT_CREATED, event);
    }

    @Override
    public void unenrollUserFromDeck(String userId, String deckId) {
        log.info("Usuniecie uzytkownika {} z talii {}", userId, deckId);
        DeckUtils.checkDeckEnrollmentIsExistsAndUserHasPermissions(deckEnrollmentRepository, userId, deckId);
        deckEnrollmentRepository.deleteByDeckIdAndUserId(deckId, userId);
        log.info("Uzytkownik {} zostal usuniety z talii {}", userId, deckId);
    }

    @Override
    public void updateLearnAlgorithm(String enrollmentId, String userId, LearnAlgorithm algorithm) {
        log.info("Aktualizacja algorytmu nauki dla enrollmentId {} na {}", enrollmentId, algorithm);
        DeckEnrollment deckEnrollment = DeckUtils.getDeckEnrollmentIfUserHasPermissions(deckEnrollmentRepository, enrollmentId, userId);
        deckEnrollment.setPreferredAlgorithm(algorithm);
        deckEnrollmentRepository.save(deckEnrollment);
        log.info("Zaktualizowano algorytm nauki dla enrollmentId {} na {}", enrollmentId, algorithm);
    }

    @Override
    public void updateHowManyFlashcardsForOneSession(String enrollmentId, String userId, int limit) {
        log.info("Aktualizacja limitu fiszek na sesje dla enrollmentId {} na {}", enrollmentId, limit);

        DeckEnrollment deckEnrollment = DeckUtils.getDeckEnrollmentIfUserHasPermissions(deckEnrollmentRepository, enrollmentId, userId);
        deckEnrollment.setHowManyFlashcardsForOneSession((long) limit);
        deckEnrollmentRepository.save(deckEnrollment);
        log.info("Zaktualizowano limit fiszek na sesje dla enrollmentId {} na {}", enrollmentId, limit);

    }

    @Override
    public Page<StudentMyCourseListItemDto> getStudentEnrollments(String userId, int page, int size) {
        if (userId == null || userId.isBlank()) {
            log.error("UserId jest pusty");
            throw new IllegalArgumentException("UserId nie może być pusty");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastAccessedAt").descending());
        Page<DeckEnrollment> enrollments = deckEnrollmentRepository.findAllByUserId(userId, pageable);
        log.debug("Pobieranie talii dla użytkownika: {}", userId);
        return enrollments.map(enrollment -> {
            Deck deck = enrollment.getDeck();

            int totalWords = deck.getWordCount();
            int learnedWords = enrollment.getLearnedFlashcardsCount();
            int completionPercent = (totalWords == 0) ? 0 : (int) ((double) learnedWords / totalWords * 100);

            return new StudentMyCourseListItemDto(
                    deck.getId(),
                    deck.getName(),
                    deck.getDescription(),
                    (long) enrollment.getCompletedSessionsCount(),
                    (long) enrollment.getCompletedSessionsCount(),
                    completionPercent,
                    enrollment.getLastAccessedAt(),
                    deck.getDifficulty(),
                    deck.getOwner(),
                    deck.getCategory()
            );
        });

//        Pageable pageable = PageRequest.of(
//                page,
//                size,
//                Sort.by(
//                        Sort.Order.asc("completionPercent"),
//                        Sort.Order.desc("lastAccessed")
//                )
//        );
//        return deckRepository.findByUserId(userId, pageable)
//                .map(this::toDto);
    }


    @Override
    public DeckEnrollmentDto getEnrollment(String userId, String deckId) {
        log.info("Pobieranie enrollment dla uzytkownika {} i talii {}", userId, deckId);
        DeckEnrollment deckEnrollment = DeckUtils.getDeckEnrollmentIfUserHasPermissions(deckEnrollmentRepository, deckId, userId);
        log.info("Pobrano enrollment dla uzytkownika {} i talii {}", userId, deckId);
        return DeckEnrollmentDto.builder()
                .id(deckEnrollment.getId())
                .deckId(deckEnrollment.getDeck().getId())
                .deckName(deckEnrollment.getDeck().getName())
                .userId(deckEnrollment.getUserId())
                .role(deckEnrollment.getRole())
                .preferredAlgorithm(deckEnrollment.getPreferredAlgorithm())
                .cardsPerSessionLimit(deckEnrollment.getHowManyFlashcardsForOneSession())


                .totalFlashcardsCount(deckEnrollment.getDeck().getWordCount())
                .learnedFlashcardsCount(deckEnrollment.getLearnedFlashcardsCount())
                .completionPercentage(deckEnrollment.getDeck().getWordCount() == 0 ? 0.0 :
                        ((double) deckEnrollment.getLearnedFlashcardsCount() / deckEnrollment.getDeck().getWordCount()) * 100)
                .joinedAt(deckEnrollment.getJoinedAt())
                .lastAccessedAt(deckEnrollment.getLastAccessedAt())
                .build();
    }

    private DeckEnrollmentRole deckEnrollmentRole(DeckVisibility deckVisibility) {
        return switch (deckVisibility) {
            case PRIVATE -> DeckEnrollmentRole.OWNER;
            case STUDENTS_ONLY -> DeckEnrollmentRole.STUDENT;
            case FRIENDS_ONLY -> DeckEnrollmentRole.FRIEND_OWNER;
            case PUBLIC -> DeckEnrollmentRole.COMMUNITY_OWNER;
        };
    }

    private DeckEnrollmentSource deckEnrollmentSource(DeckVisibility deckVisibility) {
        return switch (deckVisibility) {
            case PRIVATE -> DeckEnrollmentSource.I;
            case STUDENTS_ONLY -> DeckEnrollmentSource.TEACHER_COURSE;
            case FRIENDS_ONLY -> DeckEnrollmentSource.FRIEND_SHARED;
            case PUBLIC -> DeckEnrollmentSource.COMMUNITY;
        };
    }

}
