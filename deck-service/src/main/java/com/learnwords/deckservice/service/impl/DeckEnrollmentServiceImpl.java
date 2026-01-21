package com.learnwords.deckservice.service.impl;

import com.learnwords.common.KafkaTopic;
import com.learnwords.common.events.DeckEnrollmentsCreated;
import com.learnwords.deckservice.dto.deckEnrollment.CreateDeckEnrollmentDto;
import com.learnwords.deckservice.dto.deckEnrollment.DeckEnrollmentDto;
import com.learnwords.deckservice.dto.facade.dashboard.StudentMyCourseListItemDto;
import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.entity.DeckEnrollment;
import com.learnwords.deckservice.entity.DeckShare;
import com.learnwords.deckservice.enums.*;
import com.learnwords.deckservice.repository.DeckEnrollmentRepository;
import com.learnwords.deckservice.repository.DeckRepository;
import com.learnwords.deckservice.service.DeckEnrollmentService;
import com.learnwords.deckservice.service.DeckShareService;
import com.learnwords.deckservice.service.UserProgressService;
import com.learnwords.deckservice.service.event.GenericEventProducer;
import com.learnwords.deckservice.service.utils.DeckUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DeckEnrollmentServiceImpl implements DeckEnrollmentService {
    private final DeckRepository deckRepository;
    private final DeckEnrollmentRepository deckEnrollmentRepository;
    private final DeckShareService deckShareService;
    private final GenericEventProducer eventProducer;
    private final UserProgressService userProgressService;


    public DeckEnrollmentServiceImpl(DeckRepository deckRepository, 
                                     DeckEnrollmentRepository deckEnrollmentRepository, 
                                     @Lazy DeckShareService deckShareService,
                                     GenericEventProducer eventProducer,
                                     @Lazy UserProgressService userProgressService) {
        this.eventProducer = eventProducer;
        this.deckRepository = deckRepository;
        this.deckEnrollmentRepository = deckEnrollmentRepository;
        this.deckShareService = deckShareService;
        this.userProgressService = userProgressService;
    }

    @Override
    @Transactional
    public void enrollUserToDeck(String userId, String deckId, CreateDeckEnrollmentDto createDeckEnrollmentDto) {
        Deck deck = deckRepository.getReferenceById(deckId);
        
        EnrollmentContext context = resolveEnrollmentContext(userId, deck);
        
        log.info("Przypisanie uzytkownika {} do talii {} (rola: {}, źródło: {})", 
                userId, deckId, context.role(), context.source());
        
        Long flashcardsPerSession = createDeckEnrollmentDto != null && createDeckEnrollmentDto.getHowManyFlashcardsForOneSession() != null
                ? createDeckEnrollmentDto.getHowManyFlashcardsForOneSession()
                : deck.getHowManyFlashcardsForOneSession();
        
        LearnAlgorithm algorithm = createDeckEnrollmentDto != null && createDeckEnrollmentDto.getPreferredAlgorithm() != null
                ? createDeckEnrollmentDto.getPreferredAlgorithm()
                : deck.getLearnAlgorithm();
        
        DeckEnrollment deckEnrollment = DeckEnrollment.builder()
                .userId(userId)
                .deck(deck)
                .role(context.role())
                .source(context.source())
                .howManyFlashcardsForOneSession(flashcardsPerSession)
                .preferredAlgorithm(algorithm)
                .preferredReviewSchedule(deck.getReviewSchedule())
                .joinedAt(Instant.now())
                .build();
        deckEnrollmentRepository.save(deckEnrollment);
        
        // Inicjalizacja progressu dla wszystkich fiszek w decku
        userProgressService.initializeAllFlashcardsProgressForEnrollment(deckEnrollment);
        
        log.info("Uzytkownik {} zostal przypisany do talii {}", userId, deckId);
        DeckEnrollmentsCreated event = DeckEnrollmentsCreated.builder()
                .eventTime(deckEnrollment.getJoinedAt())
                .deckEnrollmentId(deckEnrollment.getId())
                .deckId(deckId)
                .deckName(deckEnrollment.getDeck().getName())
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
    @Transactional
    public void updateLearnAlgorithm(String enrollmentId, String userId, LearnAlgorithm algorithm) {
        log.info("Aktualizacja algorytmu nauki dla enrollmentId {} na {}", enrollmentId, algorithm);
        DeckEnrollment deckEnrollment = DeckUtils.getDeckEnrollmentIfUserHasPermissions(deckEnrollmentRepository, enrollmentId, userId);
        
        LearnAlgorithm previousAlgorithm = deckEnrollment.getPreferredAlgorithm();
        if (previousAlgorithm != algorithm) {
            log.info("Zmiana algorytmu z {} na {} - resetowanie postępu nauki dla wszystkich fiszek", 
                    previousAlgorithm, algorithm);
            userProgressService.resetAllProgressForEnrollment(deckEnrollment, algorithm);
        }
        
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
//            int learnedWords = enrollment.getLearnedFlashcardsCount();
            long wordsPerSession = deck.getHowManyFlashcardsForOneSession();
            long totalSessions = (long) Math.ceil((double) totalWords / wordsPerSession);
            int completionPercent = (totalWords == 0) ? 0 : (int) ((double) enrollment.getCompletedSessionsCount() / totalSessions * 100);


            return new StudentMyCourseListItemDto(
                    enrollment.getId(),
                    deck.getId(),
                    deck.getName(),
                    deck.getDescription(),
                    (long) Math.ceil((double) totalWords / wordsPerSession),
                    (long) enrollment.getCompletedSessionsCount(),
                    completionPercent,
                    enrollment.getLastAccessedAt(),
                    deck.getDifficulty(),
                    deck.getOwner(),
                    deck.getCategory(),
                    deck.getLanguageFrom(),
                    deck.getLanguageTo()
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
                .preferredReviewSchedule(deckEnrollment.getPreferredReviewSchedule())
                .cardsPerSessionLimit(deckEnrollment.getHowManyFlashcardsForOneSession())


                .totalFlashcardsCount(deckEnrollment.getDeck().getWordCount())
                .learnedFlashcardsCount(deckEnrollment.getLearnedFlashcardsCount())
                .completionPercentage(deckEnrollment.getDeck().getWordCount() == 0 ? 0.0 :
                        ((double) deckEnrollment.getLearnedFlashcardsCount() / deckEnrollment.getDeck().getWordCount()) * 100)
                .joinedAt(deckEnrollment.getJoinedAt())
                .lastAccessedAt(deckEnrollment.getLastAccessedAt())
                .build();
    }

    @Override
    public void updateReviewSchedule(String enrollmentId, String userId, ReviewSchedule schedule) {
        log.info("Aktualizacja harmonogramu powtórek dla enrollmentId {} na {}", enrollmentId, schedule);

        DeckEnrollment deckEnrollment = DeckUtils.getDeckEnrollmentIfUserHasPermissions(
                deckEnrollmentRepository,
                enrollmentId,
                userId
        );

        deckEnrollment.setPreferredReviewSchedule(schedule);
        deckEnrollmentRepository.save(deckEnrollment);

        log.info("Zaktualizowano harmonogram powtórek dla enrollmentId {} na {}", enrollmentId, schedule);
    }

    /**
     * Rozwiązuje kontekst zapisu użytkownika do talii na podstawie:
     * 1. Czy jest właścicielem
     * 2. Czy talia jest publiczna
     * 3. Czy ma udostępnienie (DeckShare) i jakiego typu
     */
    private EnrollmentContext resolveEnrollmentContext(String userId, Deck deck) {
        if (userId.equals(deck.getOwnerId())) {
            return new EnrollmentContext(DeckEnrollmentRole.OWNER, DeckEnrollmentSource.I);
        }

        if (deck.getVisibility() == DeckVisibility.PUBLIC) {
            return new EnrollmentContext(DeckEnrollmentRole.COMMUNITY_OWNER, DeckEnrollmentSource.COMMUNITY);
        }
        
        Optional<DeckShare> shareOpt = deckShareService.findActiveShareForUser(userId, deck.getId());
        
        if (shareOpt.isPresent()) {
            DeckShare share = shareOpt.get();
            return resolveEnrollmentFromShare(share);
        }
        
        log.warn("Użytkownik {} nie ma dostępu do talii {}", userId, deck.getId());
        throw new IllegalStateException("Brak uprawnień do zapisu na tę talię");
    }
    

    private EnrollmentContext resolveEnrollmentFromShare(DeckShare share) {
        return switch (share.getTargetType()) {
            case GROUP, ALL_STUDENTS, USER -> 
                new EnrollmentContext(DeckEnrollmentRole.STUDENT, DeckEnrollmentSource.TEACHER_COURSE);
            case ALL_FRIENDS -> 
                new EnrollmentContext(DeckEnrollmentRole.FRIEND_OWNER, DeckEnrollmentSource.FRIEND_SHARED);
        };
    }

    @Override
    @Transactional
    public int migrateExistingEnrollments() {
        log.info("Rozpoczynam migrację enrollmentów - inicjalizacja progressu dla wszystkich fiszek");
        
        List<DeckEnrollment> allEnrollments = deckEnrollmentRepository.findAll();
        int migratedCount = 0;
        
        for (DeckEnrollment enrollment : allEnrollments) {
            try {
                userProgressService.initializeAllFlashcardsProgressForEnrollment(enrollment);
                migratedCount++;
                log.debug("Zmigrowano enrollment {} dla użytkownika {}", 
                        enrollment.getId(), enrollment.getUserId());
            } catch (Exception e) {
                log.error("Błąd migracji enrollmentu {} dla użytkownika {}: {}", 
                        enrollment.getId(), enrollment.getUserId(), e.getMessage());
            }
        }
        
        log.info("Zakończono migrację. Zmigrowano {} z {} enrollmentów", 
                migratedCount, allEnrollments.size());
        return migratedCount;
    }

    private record EnrollmentContext(DeckEnrollmentRole role, DeckEnrollmentSource source) {}

}
