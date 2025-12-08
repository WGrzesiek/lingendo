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

}
