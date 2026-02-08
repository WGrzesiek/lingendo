package com.learnwords.common;

public class KafkaTopic {
    public static final String CREATE_SENTENCE_TOPIC = "outbox.event.SENTENCE";
    public static final String CREATE_SENTENCE_FOR_DECK_TOPIC = "outbox.event.SENTENCEFORDECK";
    public static final String CREATE_VOCABULARY_TOPIC = "outbox.event.VOCABULARY";
    public static final String CREATE_VOCABULARY_FOR_DECK_TOPIC = "outbox.event.VOCABULARYFORDECK";
    public static final String UPPATED_STATUS = "outbox.event.UPDATE_STATUS";


    public static final String USER_LOGINS_TOPIC = "user-logins";
    public static final String DECK_CREATED = "deck.created";
    public static final String FLASHCARD_CREATED = "deck.flashcard.created";
    public static final String SESSION_STARTED = "deck.session.started";
    public static final String FLASHCARD_ANSWERED = "deck.flashcard.answered";
    public static final String SESSION_FINISHED = "deck.session.finished";
    public static final String DECK_ENROLLMENT_CREATED = "deck.enrollment.created";
    public static final String DECK_ENROLLMENT_FINISHED = "deck.enrollment.finished";

    // Teacher-Student events
    public static final String TEACHER_STUDENT_JOINED = "teacher.student.joined";
    public static final String TEACHER_STUDENT_REMOVED = "teacher.student.removed";
    public static final String TEACHER_DECK_SHARED = "teacher.deck.shared";

    // Friendship events
    public static final String FRIENDSHIP_ACCEPTED = "friendship.accepted";
    public static final String FRIENDSHIP_REMOVED = "friendship.removed";

    // Group events
    public static final String GROUP_CREATED = "group.created";
    public static final String GROUP_MEMBER_ADDED = "group.member.added";
    public static final String GROUP_MEMBER_REMOVED = "group.member.removed";

    // Deck sharing events
    public static final String DECK_SHARED = "deck.shared";
    public static final String DECK_SHARE_REVOKED = "deck.share.revoked";

    // AI Sentence generation events
    public static final String AI_SENTENCE_GENERATED = "outbox.event.SentenceGeneration";

}
