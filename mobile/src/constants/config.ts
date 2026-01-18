/**
 * Konfiguracja API
 */
export const API_CONFIG = {
  BASE_URL: 'http://staging.ibis-tautara.ts.net:8811/api',
  TIMEOUT: 10000,
} as const;

/**
 * Endpointy API - pogrupowane według domeny
 */
export const ENDPOINTS = {
  AUTH: {
    LOGIN: '/v1/gateway/login',
    REGISTER: '/v1/users/register',
    REFRESH: '/v1/gateway/refresh',
    LOGOUT: '/v1/gateway/logout',
    ME: '/v1/gateway/me',
  },
  DASHBOARD: {
    STUDENT_STATS: '/v1/dashboard/student/stats',
    STUDENT_ACTIVITY: '/v1/dashboard/student/activity',
    STUDENT_LEADERBOARD: '/v1/dashboard/student/leaderboard',
    TEACHER_STATS: '/v1/dashboard/teacher/stats',
  },
  DECKS: {
    LIST: '/v1/decks',
    PUBLIC: '/v1/decks/public',
    USER: '/v1/decks/user',
    USER_FILTER: '/v1/decks/user/filter',
    USER_COUNT: '/v1/decks/user/count',
    VALIDATE_NAME: '/v1/decks/validate-name',
    BY_ID: (id: string) => `/v1/decks/${id}`,
    DETAILS: (id: string) => `/v1/decks/${id}/details`,
    VISIBILITY: (id: string) => `/v1/decks/${id}/visibility`,
    OWNER: (id: string) => `/v1/decks/${id}/owner`,
    NAME: (id: string) => `/v1/decks/${id}/name`,
    ALGORITHM: (id: string) => `/v1/decks/${id}/learnAlgorithm`,
    FLASHCARDS_PER_SESSION: (id: string) => `/v1/decks/${id}/flashcardsPerSession`,
    STATISTICS: (id: string) => `/v1/decks/${id}/statistics`,
    FLASHCARDS_PAGE: (deckId: string) => `/v1/decks/${deckId}/flashcards/page`,
  },
  ENROLLMENT: {
    MY: '/v1/decks/enrollments/my',
    ENROLL: (deckId: string) => `/v1/decks/${deckId}/enrollments`,
    UNENROLL: (enrollmentId: string) => `/v1/decks/enrollments/${enrollmentId}`,
    ALGORITHM: (enrollmentId: string, algorithm: string) =>
      `/v1/decks/enrollments/${enrollmentId}/algorithm?algorithm=${algorithm}`,
    SESSION_LIMIT: (enrollmentId: string, limit: number) =>
      `/v1/decks/enrollments/${enrollmentId}/session-limit?limit=${limit}`,
    REVIEW_SCHEDULE: (enrollmentId: string, mode: string) =>
      `/v1/decks/enrollments/${enrollmentId}/review-schedule?mode=${mode}`,
  },
  VOCABULARY: {
    CREATE_BATCH: '/v1/vocabulary/create-batch',
    CREATE_BATCH_FOR_DECK: (deckId: string) => `/v1/vocabulary/deck/${deckId}/create-batch`,
  },
  COURSES: {
    MY_STATS: '/v1/courses/my-course/stats',
    LIST: '/v1/courses',
    DETAILS: (id: string) => `/v1/courses/${id}`,
    ENROLL: (id: string) => `/v1/courses/${id}/enroll`,
    HEADER: (enrollmentId: string) => `/v1/decks/${enrollmentId}/course-header`,
    PROGRESS: (enrollmentId: string) => `/v1/decks/sessions/${enrollmentId}/session-progres`,
    SETTINGS: (enrollmentId: string) => `/v1/decks/enrollment/${enrollmentId}/settings`,
    WORDS: (enrollmentId: string) => `/v1/decks/enrollments/${enrollmentId}/course-view`,
    FLASHCARD_STATS: (enrollmentId: string) => `/v1/courses/${enrollmentId}/flashcards/stats`,
    INITIALIZE_SESSION: (enrollmentId: string) =>
      `/v1/decks/${enrollmentId}/sessions?flashcardFetchStrategy=ALPHABETICAL&type=LEARNING`,
  },
  LEARNING: {
    SESSION: '/v1/learning/session',
    SUBMIT: '/v1/learning/submit',
    PROGRESS: '/v1/learning/progress',
    NEXT_FLASHCARD: (sessionId: string) => `/v1/decks/sessions/${sessionId}/next`,
    SUBMIT_ANSWER: (sessionId: string, flashcardId: string) =>
      `/v1/decks/sessions/${sessionId}/flashcards/${flashcardId}/answer`,
    HEADER_PROGRESS: (sessionId: string) => `/v1/decks/sessions/${sessionId}/learn-header`,
    COMPLETE_SESSION: (sessionId: string) => `/v1/decks/sessions/${sessionId}/complete`,
  },
  REVIEW: {
    HEADER: (enrollmentId: string) => `/v1/enrollments/${enrollmentId}/review-header`,
    WORDS: (enrollmentId: string) => `/v1/decks/enrollments/${enrollmentId}/review-words-view`,
    NEXT_FLASHCARD: (enrollmentId: string) => `/v1/decks/reviews/enrollments/${enrollmentId}/next`,
    SUBMIT_ANSWER: (flashcardId: string) => `/v1/decks/reviews/flashcards/${flashcardId}/answer`,
  },
} as const;

/**
 * Konfiguracja aplikacji
 */
export const APP_CONFIG = {
  APP_NAME: 'Lingendo',
  VERSION: '1.0.0',
} as const;

/**
 * Klucze storage (SecureStore)
 */
export const STORAGE_KEYS = {
  ACCESS_TOKEN: 'access_token',
  REFRESH_TOKEN: 'refresh_token',
  USER: 'user',
} as const;
