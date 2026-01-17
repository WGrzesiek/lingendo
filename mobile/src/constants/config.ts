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
    CREATE: '/v1/decks',
    DETAILS: (id: string) => `/v1/decks/${id}`,
    UPDATE: (id: string) => `/v1/decks/${id}`,
    DELETE: (id: string) => `/v1/decks/${id}`,
    CARDS: (deckId: string) => `/v1/decks/${deckId}/cards`,
  },
  DECK_ENROLLMENT: {
    ENROLL: (deckId: string) => `/v1/decks/${deckId}/enrollments`,
    GET_MY_ENROLLMENTS: '/v1/decks/enrollments/my',

  },
  CARDS: {
    CREATE: '/v1/cards',
    UPDATE: (id: string) => `/v1/cards/${id}`,
    DELETE: (id: string) => `/v1/cards/${id}`,
  },
  LEARNING: {
    SESSION: '/v1/learning/session',
    SUBMIT: '/v1/learning/submit',
    PROGRESS: '/v1/learning/progress',
  },
  COURSES: {
    LIST: '/v1/courses',
    DETAILS: (id: string) => `/v1/courses/${id}`,
    ENROLL: (id: string) => `/v1/courses/${id}/enroll`,
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
