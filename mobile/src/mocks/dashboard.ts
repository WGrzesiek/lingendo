import type {
  StudentStatistics,
  Deck,
  LeaderboardEntry,
  RecentActivityItem,
} from '../types/dashboard';

/**
 * Zamockowane statystyki ucznia
 */
export const MOCK_STATISTICS: StudentStatistics = {
  activeDecks: 5,
  completedLessonsThisMonth: 23,
  streakDays: 7,
  totalPoints: 1250,
  pointsThisWeek: 180,
};

/**
 * Zamockowane kursy użytkownika
 */
export const MOCK_DECKS: Deck[] = [
  {
    id: 1,
    name: 'Deutsch Basics A1',
    description: 'Podstawowe słownictwo niemieckie',
    totalCards: 100,
    learnedCards: 45,
    progress: 45,
    lastStudied: '2025-01-17T10:30:00Z',
  },
  {
    id: 2,
    name: 'English Business',
    description: 'Słownictwo biznesowe',
    totalCards: 80,
    learnedCards: 20,
    progress: 25,
    lastStudied: '2025-01-16T14:00:00Z',
  },
  {
    id: 3,
    name: 'Hiszpański dla początkujących',
    description: 'Frazy i zwroty codzienne',
    totalCards: 60,
    learnedCards: 60,
    progress: 100,
    lastStudied: '2025-01-15T09:00:00Z',
  },
];

/**
 * Zamockowany ranking
 */
export const MOCK_LEADERBOARD: LeaderboardEntry[] = [
  { rank: 1, userId: 10, username: 'anna_nowak', points: 2450 },
  { rank: 2, userId: 11, username: 'piotr_wisniewski', points: 2100 },
  { rank: 3, userId: 1, username: 'jan_kowalski', points: 1250, isCurrentUser: true },
  { rank: 4, userId: 12, username: 'maria_zielinska', points: 980 },
  { rank: 5, userId: 13, username: 'tomek_lewandowski', points: 750 },
];

/**
 * Zamockowana ostatnia aktywność
 */
export const MOCK_RECENT_ACTIVITY: RecentActivityItem[] = [
  {
    id: 1,
    type: 'lesson_completed',
    title: 'Ukończono lekcję',
    description: 'Deutsch Basics A1 - Czasowniki',
    timestamp: '2025-01-17T10:30:00Z',
  },
  {
    id: 2,
    type: 'streak_reached',
    title: 'Seria 7 dni!',
    description: 'Utrzymujesz serię nauki przez tydzień',
    timestamp: '2025-01-17T00:00:00Z',
  },
  {
    id: 3,
    type: 'achievement_earned',
    title: 'Nowe osiągnięcie',
    description: 'Zdobyto odznakę "Początkujący Poliglota"',
    timestamp: '2025-01-16T18:00:00Z',
  },
  {
    id: 4,
    type: 'deck_started',
    title: 'Rozpoczęto nowy kurs',
    description: 'English Business',
    timestamp: '2025-01-15T12:00:00Z',
  },
];
