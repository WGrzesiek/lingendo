/**
 * Klucze React Query
 *
 * Strategia: Ogólne klucze - lepiej odświeżyć za dużo niż za mało.
 * Przy mutacjach invalidujemy całe grupy zamiast pojedynczych query.
 */
export const QUERY_KEYS = {
  USER: ['user'] as const,
  DASHBOARD: ['dashboard'] as const,
  DECKS: ['decks'] as const,
  CARDS: ['cards'] as const,
  LEARNING: ['learning'] as const,
  COURSES: ['courses'] as const,
  LEADERBOARD: ['leaderboard'] as const,
  SETTINGS: ['settings'] as const,
} as const;



/**
 * Grupy kluczy do masowej invalidacji
 * Używaj po większych akcjach (np. zakończenie sesji nauki)
 */
export const INVALIDATION_GROUPS = {
  AFTER_LEARNING: [QUERY_KEYS.DASHBOARD, QUERY_KEYS.LEARNING, QUERY_KEYS.LEADERBOARD] as const,
  AFTER_DECK_MUTATION: [QUERY_KEYS.DECKS, QUERY_KEYS.CARDS] as const,
  AFTER_USER_UPDATE: [QUERY_KEYS.USER, QUERY_KEYS.DASHBOARD] as const,


  ON_LOGOUT: [
    QUERY_KEYS.USER,
    QUERY_KEYS.DASHBOARD,
    QUERY_KEYS.DECKS,
    QUERY_KEYS.CARDS,
    QUERY_KEYS.LEARNING,
    QUERY_KEYS.COURSES,
    QUERY_KEYS.LEADERBOARD,
  ] as const,
} as const;
