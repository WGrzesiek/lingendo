package com.learnwords.common;

public enum EventStatus {
    // --- podstawowe ---
    CREATED,
    PUBLISHED,
    QUEUED,
    RECEIVED,
    VALIDATED,
    PROCESSING,
    COMPLETED,
    // --- błędy / retry ---
    RETRYING,
    FAILED,
    DEAD_LETTERED,
    SKIPPED,
    // --- domenowe / dodatkowe ---
    COMPENSATED,
    CANCELED,
    TIMEOUT;
}
