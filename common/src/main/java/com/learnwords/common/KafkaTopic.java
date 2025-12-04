package com.learnwords.common;

public class KafkaTopic {
    public static final String CREATE_SENTENCE_TOPIC = "outbox.event.SENTENCE";
    public static final String CREATE_SENTENCE_FOR_DECK_TOPIC = "outbox.event.SENTENCEFORDECK";
    public static final String CREATE_VOCABULARY_TOPIC = "outbox.event.VOCABULARY";
    public static final String CREATE_VOCABULARY_FOR_DECK_TOPIC = "outbox.event.VOCABULARYFORDECK";
    public static final String UPPATED_STATUS = "outbox.event.UPDATE_STATUS";
    public static final String FLASHCARD_ANSWERED_TOPIC = "deck.flashcard.answered";
    public static final String SESSION_COMPLETED_TOPIC = "deck.session.completed";
    public static final String FLASHCARD_PROGRESS_TOPIC = "deck.flashcard.progress";
    public static final String USER_LOGINS_TOPIC = "user-logins";
}
