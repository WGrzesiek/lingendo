export const qk = {
    learning: {
        nextFlashcard: (sessionId: string) => ["learning", "session", sessionId, "nextFlashcard"] as const,
        headerProgress: (sessionId: string) => ["learning", "session", sessionId, "headerProgress"] as const,
        nextFlashcardReview: (enrollmentId: string) => ["learning", "enrollment", enrollmentId, "nextFlashcardReview"] as const,
    },
} as const;
